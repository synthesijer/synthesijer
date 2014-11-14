package synthesijer.scheduler;

import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.IdentifierGenerator;
import synthesijer.SynthesijerUtils;
import synthesijer.ast.Expr;
import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstTypeVisitor;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Type;
import synthesijer.ast.expr.ArrayAccess;
import synthesijer.ast.expr.AssignExpr;
import synthesijer.ast.expr.AssignOp;
import synthesijer.ast.expr.BinaryExpr;
import synthesijer.ast.expr.FieldAccess;
import synthesijer.ast.expr.Ident;
import synthesijer.ast.expr.Literal;
import synthesijer.ast.expr.MethodInvocation;
import synthesijer.ast.expr.NewArray;
import synthesijer.ast.expr.NewClassExpr;
import synthesijer.ast.expr.ParenExpr;
import synthesijer.ast.expr.SynthesijerExprVisitor;
import synthesijer.ast.expr.TypeCast;
import synthesijer.ast.expr.UnaryExpr;
import synthesijer.ast.statement.BlockStatement;
import synthesijer.ast.statement.BreakStatement;
import synthesijer.ast.statement.ContinueStatement;
import synthesijer.ast.statement.ExprStatement;
import synthesijer.ast.statement.ForStatement;
import synthesijer.ast.statement.IfStatement;
import synthesijer.ast.statement.ReturnStatement;
import synthesijer.ast.statement.SkipStatement;
import synthesijer.ast.statement.SwitchStatement;
import synthesijer.ast.statement.SynchronizedBlock;
import synthesijer.ast.statement.TryStatement;
import synthesijer.ast.statement.VariableDecl;
import synthesijer.ast.statement.WhileStatement;
import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.ComponentType;
import synthesijer.ast.type.MySelfType;
import synthesijer.ast.type.PrimitiveTypeKind;

/**
 * A visitor class to dump module hierarchy of AST as XML.
 * 
 * @author miyo
 *
 */
public class GenSchedulerBoardVisitor implements SynthesijerAstVisitor{
	
	private final IdentifierGenerator idGen;
	
	private final GenSchedulerBoardVisitor parent;
	
	private final SchedulerInfo info;
	
	/**
	 * target board
	 */
	private final SchedulerBoard board;
	
	/**
	 * the stepId where the step pointer visits as normal flow
	 */
	private final int exitId;
	
	/**
	 * the stepId where the step pointer visits by "break" jump
	 */
	private final int breakId;

	/**
	 * the stepId where the step pointer visits by "continue" jump
	 */
	private final int continueId;
	
	/**
	 * 1st stepId of this visitor
	 */
	private int entryId = -1;
	
	/**
	 * last added Item
	 */
	private SchedulerItem lastItem;
	
	private final Hashtable<String, VariableOperand> varTable = new Hashtable<>();
	
	private SchedulerItem methodExit;
	
	public GenSchedulerBoardVisitor(SchedulerInfo info, IdentifierGenerator idGen) {
		this.info = info;
		this.parent = null;
		this.board = null;
		this.idGen = idGen;
		this.exitId = -1;
		this.continueId = -1;		
		this.breakId = -1;
		info.addVarTable(varTable);
	}

	private GenSchedulerBoardVisitor(GenSchedulerBoardVisitor parent, SchedulerBoard board, int exitId, int breakId, int continueId) {
		this.parent = parent;
		this.info = parent.info;
		this.board = board;
		this.idGen = parent.idGen;
		this.exitId = exitId;
		this.methodExit = parent.methodExit;
		this.lastItem = parent.lastItem;
		this.breakId = breakId;
		this.continueId = continueId;		
		info.addVarTable(varTable);
	}

	public IdentifierGenerator getIdGen(){
		return idGen;
	}
	
	public SchedulerBoard getBoard(){
		return board;
	}
	
	public int getEntryId(){
		return entryId;
	}
	
	public void setEntryId(int id){
		entryId = id;
	}
	
	public VariableOperand search(String key){
		if(varTable.containsKey(key)){
			return varTable.get(key);
		}else if(parent != null){
			return parent.search(key);
		}else{
			return null;
		}
	}
	
	public void addVariable(String name, VariableOperand v){
		varTable.put(name, v);
	}
	
	private GenSchedulerBoardVisitor stepIn(Statement stmt, int exitId, int breakId, int continueId){
		GenSchedulerBoardVisitor v = new GenSchedulerBoardVisitor(this, board, exitId, breakId, continueId);
		stmt.accept(v);
		SchedulerItem ret = v.addSchedulerItem(new SchedulerItem(board, Op.JP, null, null)); // exit from this block
		ret.setBranchId(exitId);
		lastItem = v.lastItem; // update last item
		return v;
	}
	
	private Operand stepIn(Expr expr){
		GenSchedulerBoardExprVisitor v = new GenSchedulerBoardExprVisitor(this);
		expr.accept(v);
		return v.getOperand();
	}

	private Type stepIn(Type type){
		GenSchedulerBoardTypeVisitor v = new GenSchedulerBoardTypeVisitor(this);
		type.accept(v);
		return v.getType();
	}

//	private SchedulerItem entry, exit;

	@Override
	public void visitModule(Module o) {
		for(VariableDecl v : o.getVariableDecls()) {
			v.accept(this);
		}
		for (Method m : o.getMethods()) {
			if(m.isConstructor()) continue; // skip 
			if(m.isUnsynthesizable()) continue; // skip
			SchedulerBoard b = new SchedulerBoard(m.getName(), m);
			info.addBoard(b);
			this.methodExit = b.addItemInNewSlot(new SchedulerItem(b, Op.METHOD_EXIT, null, null));
			// breakId and continueId will not be defined for method
			GenSchedulerBoardVisitor child = new GenSchedulerBoardVisitor(this, b, methodExit.getStepId(), -1, -1);
			m.accept(child);
		}
	}

	@Override
	public void visitMethod(Method o) {
		
		SchedulerItem entry = board.addItemInNewSlot(new MethodEntryItem(board, o.getName()));
		lastItem = entry;

		for(VariableDecl v: o.getArgs()){
			v.accept(this);
		}

		// breakId and continueId will not be defined for method
		GenSchedulerBoardVisitor v = stepIn(o.getBody(), exitId, -1, -1);
		entry.setBranchId(v.entryId);
	}

	@Override
	public void visitBlockStatement(BlockStatement o) {
		for (Statement s : o.getStatements()) {
			s.accept(this);
		}
	}

	@Override
	public void visitBreakStatement(BreakStatement o) {
		SchedulerItem br = addSchedulerItem(new SchedulerItem(board, Op.BREAK, null, null));
		br.setBranchId(breakId);
	}

	@Override
	public void visitContinueStatement(ContinueStatement o) {
		SchedulerItem br = addSchedulerItem(new SchedulerItem(board, Op.CONTINUE, null, null));
		br.setBranchId(continueId);
	}

	@Override
	public void visitExprStatement(ExprStatement o) {
		stepIn(o.getExpr());
	}

	@Override
	public void visitForStatement(ForStatement o) {
		for (Statement s : o.getInitializations()) {
			s.accept(this);
		}
		int afterInitId = lastItem.getStepId(); // return point for "for loop"
		
		Operand flag = stepIn(o.getCondition());
		SchedulerItem fork = addSchedulerItem(new SchedulerItem(board, Op.JT, new Operand[]{flag}, null)); // jump on condition
		SchedulerItem join = addSchedulerItem(new SchedulerItem(board, Op.JP, null, null)); // join point to go to branch following
		
		int beforeUpdateId = lastItem.getStepId(); // entry point for updte statements
		for (Statement s : o.getUpdates()){
			s.accept(this);
		}
		SchedulerItem loop_back = addSchedulerItem(new SchedulerItem(board, Op.JP, null, null)); // join point to go to branch following
		loop_back.setBranchId(afterInitId+1); // after loop body and update, back to condition checking
		
		GenSchedulerBoardVisitor v = stepIn(o.getBody(), beforeUpdateId+1, join.getStepId(), beforeUpdateId);
		fork.setBranchIds(new int[]{v.getEntryId(), join.getStepId()}); // fork into loop body or exit
		join.setBranchId(lastItem.getStepId()+1); // next
	}

	@Override
	public void visitIfStatement(IfStatement o) {
		Operand flag = stepIn(o.getCondition());
		SchedulerItem fork = addSchedulerItem(new SchedulerItem(board, Op.JT, new Operand[]{flag}, null)); // jump on condition
		SchedulerItem join = addSchedulerItem(new SchedulerItem(board, Op.JP, null, null)); // join point to go to branch following
		
		GenSchedulerBoardVisitor v0, v1;
		v0 = stepIn(o.getThenPart(), join.getStepId(), breakId, continueId);
		if (o.getElsePart() == null) {
			fork.setBranchIds(new int[]{v0.getEntryId(), join.getStepId()});
			join.setBranchId(v0.lastItem.getStepId() + 1);
		}else{
			v1 = stepIn(o.getElsePart(), join.getStepId(), breakId, continueId);
			fork.setBranchIds(new int[]{v0.getEntryId(), v1.getEntryId()});
			join.setBranchId(v1.lastItem.getStepId() + 1);
		}
	}

	@Override
	public void visitReturnStatement(ReturnStatement o) {
		SchedulerItem ret;
		if (o.getExpr() != null){
			Operand v = stepIn(o.getExpr());
			ret = addSchedulerItem(new SchedulerItem(board, Op.RETURN, new Operand[]{v}, null));
		}else{
			ret = addSchedulerItem(new SchedulerItem(board, Op.RETURN, null, null));
		}
		ret.setBranchId(methodExit.getStepId());
	}

	@Override
	public void visitSkipStatement(SkipStatement o) {
	}

	@Override
	public void visitSwitchStatement(SwitchStatement o) {
		ArrayList<SwitchStatement.Elem> elements = o.getElements();
		int[] branchIDs = new int[elements.size() + 1]; // #. patterns and default
		
		Operand selector_target = stepIn(o.getSelector()); // target operand

		Operand[] selector_pat = new Operand[elements.size()];
				
		// gather selector operand
		for(int i = 0; i < elements.size(); i++){
			SwitchStatement.Elem elem = elements.get(i);
			selector_pat[i] = stepIn(elem.getPattern());
		}

		SchedulerItem fork = addSchedulerItem(new SelectItem(board, selector_target, selector_pat));
		SchedulerItem join = addSchedulerItem(new SchedulerItem(board, Op.JP, null, null)); // join point to go to branch following
		
		int nextCaseId;
		{ // for default
			int idx = elements.size();
			branchIDs[idx] = lastItem.getStepId() + 1; // currentID + 1
			SwitchStatement.Elem elem = o.getDefaultElement();
			GenSchedulerBoardVisitor v = new GenSchedulerBoardVisitor(this, board, join.getStepId(), join.getStepId(), continueId);
			elem.accept(v);
			lastItem = v.lastItem;
			SchedulerItem ret = addSchedulerItem(new SchedulerItem(board, Op.JP, null, null)); // exit from this block
			ret.setBranchId(join.getStepId());
			nextCaseId = branchIDs[idx];
		}
		
        // switch body (access in manner of reverse order)
		for(int i = 0; i < elements.size(); i++){
			int idx = elements.size() - i - 1;
			branchIDs[idx] = lastItem.getStepId() + 1; // currentID
			SwitchStatement.Elem elem = elements.get(idx);
			GenSchedulerBoardVisitor v = new GenSchedulerBoardVisitor(this, board, nextCaseId, join.getStepId(), continueId);
			elem.accept(v);
			lastItem = v.lastItem;
			SchedulerItem ret = addSchedulerItem(new SchedulerItem(board, Op.JP, null, null)); // exit from this block
			ret.setBranchId(nextCaseId);
			nextCaseId = branchIDs[idx];
		}

		fork.setBranchIds(branchIDs);
		join.setBranchId(lastItem.getStepId() + 1);
	}

	public void visitSwitchCaseElement(SwitchStatement.Elem o) {
		for (Statement s : o.getStatements()){
			s.accept(this);
		}
	}

	@Override
	public void visitSynchronizedBlock(SynchronizedBlock o) {
		visitBlockStatement(o);
	}

	@Override
	public void visitTryStatement(TryStatement o) {
		o.getBody().accept(this);
	}

	@Override
	public void visitVariableDecl(VariableDecl o) {
		Type t = stepIn(o.getVariable().getType());
		String prefix = o.getScope().getMethod() == null ? "class" : o.getScope().getMethod().getName();
		String vName;
		vName = String.format("%s_%s_%04d", prefix, o.getName(), idGen.id());
		VariableOperand v = new VariableOperand(vName, t, o.getVariable());
		//Variable v = new Variable(o.getVariable().getUniqueName(), t);
		varTable.put(o.getName(), v);
		if (o.getInitExpr() != null){
			Operand src = stepIn(o.getInitExpr());
			addSchedulerItem(new SchedulerItem(board, Op.ASSIGN, new Operand[]{src}, v));
		}
	}

	@Override
	public void visitWhileStatement(WhileStatement o) {
		int loopEntryId = lastItem.getStepId(); // return point for "for loop"

		Operand flag = stepIn(o.getCondition());
		SchedulerItem fork = addSchedulerItem(new SchedulerItem(board, Op.JT, new Operand[]{flag}, null)); // jump on condition
		SchedulerItem join = addSchedulerItem(new SchedulerItem(board, Op.JP, null, null)); // join point to go to branch following
		
		GenSchedulerBoardVisitor v = stepIn(o.getBody(), loopEntryId+1, join.getStepId(), loopEntryId+1);

		fork.setBranchIds(new int[]{v.getEntryId(), join.getStepId()}); // fork into loop body or exit
		join.setBranchId(lastItem.getStepId()+1); // next
	}
	
	public SchedulerItem addSchedulerItem(SchedulerItem item){
		if(board == null){
			return item; // skip
		}
		board.addItemInNewSlot(item);
		if(entryId == -1) entryId = item.getStepId();
		lastItem = item;
		return item;
	}

}

class GenSchedulerBoardExprVisitor implements SynthesijerExprVisitor{
	
	private Operand result;
	
	private final GenSchedulerBoardVisitor parent;
	
	public GenSchedulerBoardExprVisitor(GenSchedulerBoardVisitor parent) {
		this.parent = parent;
	}
	
	/**
	 * returns the last result of expression chain.
	 * @return
	 */
	public Operand getOperand(){
		return result;
	}
	
	private Operand stepIn(Expr expr){
		GenSchedulerBoardExprVisitor v = new GenSchedulerBoardExprVisitor(this.parent);
		expr.accept(v);
		return v.getOperand();
	}

	@Override
	public void visitArrayAccess(ArrayAccess o) {
		Operand indexed = stepIn(o.getIndexed());
		Operand index = stepIn(o.getIndex());
		VariableOperand tmp = newVariable("array_access", indexed.getType());
		parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.ARRAY_ACCESS, new Operand[]{indexed, index}, tmp));
		result = tmp;
	}
	
	private boolean isSameType(Type t0, Type t1){
		return t0 == t1;
	}
	
	private VariableOperand addCast(Operand v, Type target){
		Type orig = v.getType();
		VariableOperand tmp = newVariable("cast_expr", target);
		parent.addSchedulerItem(new TypeCastItem(parent.getBoard(), v, tmp, orig, target)); // v::orig -> tmp::target
		return tmp;
	}
	
	@Override
	public void visitAssignExpr(AssignExpr o) {
		Operand lhs = stepIn(o.getLhs());
		Operand rhs = stepIn(o.getRhs());
		if(isSameType(lhs.getType(), rhs.getType()) == false){
			VariableOperand tmp = addCast(rhs, lhs.getType()); // RHS is casted into corresponding to LHS
			if(tmp != null) rhs = tmp;
		}
		parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.ASSIGN, new Operand[]{rhs}, (VariableOperand)lhs));
	}

	@Override
	public void visitAssignOp(AssignOp o) {
		Operand lhs = stepIn(o.getLhs());
		Operand rhs = stepIn(o.getRhs());
		VariableOperand tmp = newVariable("binary_expr", lhs.getType());
		parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.get(o.getOp()), new Operand[]{lhs,rhs}, tmp));
		parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.ASSIGN, new Operand[]{tmp}, (VariableOperand)lhs));
		result = lhs;
	}

	private VariableOperand newVariable(String key, Type t){
		VariableOperand v = new VariableOperand(String.format("%s_%05d", key, parent.getIdGen().id()), t);
		parent.addVariable(v.getName(), v);
		return v; 
	}
	
	private Type getPreferableType(Operand lhs, Operand rhs){
		//SynthesijerUtils.warn("implicit type cast for lhs: " + lhs.getType() + " <- " + rhs.getType());
		if(((lhs.getType() instanceof PrimitiveTypeKind) && (rhs.getType() instanceof PrimitiveTypeKind)) == false){
			SynthesijerUtils.warn("cannot convert:" + lhs.getType() + " <-> " + rhs.getType() + ", use " + lhs.getType());
			return lhs.getType(); // skip
		}
		PrimitiveTypeKind t0 = (PrimitiveTypeKind)(lhs.getType());
		PrimitiveTypeKind t1 = (PrimitiveTypeKind)(rhs.getType());
		if(t0.isInteger() && t1.isInteger()){ // both integer, select bigger one			
			return (t0.getWidth() > t1.getWidth()) ? t0 : t1;
		}
		if(t0.isFloating() && t1.isFloating()){ // both floating point, select bigger one			
			return (t0.getWidth() > t1.getWidth()) ? t0 : t1;
		}
		return t0.isFloating() ? t0 : t1;
	}
	
	@Override
	public void visitBinaryExpr(BinaryExpr o) {
		Operand lhs = stepIn(o.getLhs());
		Operand rhs = stepIn(o.getRhs());
		Type type;
		Op op = Op.get(o.getOp());
		if(op.isForcedType()){
			type = op.getType();
		}else if(isSameType(lhs.getType(), rhs.getType()) == false){
			type = getPreferableType(lhs, rhs); // select type
			if(lhs.getType() != type){
				VariableOperand tmp = addCast(lhs, type);
				if(tmp != null) lhs = tmp;
			}
			if(rhs.getType() != type){
				VariableOperand tmp = addCast(rhs, type);
				if(tmp != null) rhs = tmp;
			}
		}else{
			type = lhs.getType();
		}
		result = newVariable("binary_expr", type);
		parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.get(o.getOp()), new Operand[]{lhs,rhs}, (VariableOperand)result));
	}

	@Override
	public void visitFieldAccess(FieldAccess o) {
		Type type = PrimitiveTypeKind.UNDEFIEND;
		if(o.getType() instanceof ArrayType && o.getIdent().getSymbol().equals("length")){
			type = PrimitiveTypeKind.INT;
		}else if(o.getType() instanceof ComponentType){
			type = o.getIdent().getType();
		}else{
			String klass = ((Ident)o.getSelected()).getSymbol();
			VariableInfo var = GlobalSymbolTable.INSTANCE.searchVariable(klass, o.getIdent().getSymbol());
			type = var.var.getType();
		}
		VariableOperand tmp = newVariable("field_access", type);
		Operand v = stepIn(o.getSelected());
		parent.addSchedulerItem(new FieldAccessItem(parent.getBoard(), (VariableOperand)v, o.getIdent().getSymbol(), null, tmp));
		result = tmp;
	}

	@Override
	public void visitIdent(Ident o) {
		result = parent.search(o.getSymbol());
	}

	@Override
	public void visitLitral(Literal o) {
		result = new ConstantOperand(o.getValueAsStr(), o.getType());
	}

	@Override
	public void visitMethodInvocation(MethodInvocation o) {
		Expr method = o.getMethod();
		VariableOperand tmp = newVariable("method_result", o.getType());
		ArrayList<Expr> params = o.getParameters();
		Operand[] list = new Operand[params.size()];
		for(int i = 0; i < params.size(); i++){
			list[i] = stepIn(params.get(i));
		}
		SchedulerItem item;
		VariableDecl[] vars = o.getTargetMethod().getArgs();
		String[] callee_args = new String[o.getTargetMethod().getArgs().length];
		for(int i = 0; i < callee_args.length; i++){
			callee_args[i] = vars[i].getName();
		}
		if (method instanceof FieldAccess) { // calling other instance method
			FieldAccess fa = (FieldAccess)method;
			item = new MethodInvokeItem(parent.getBoard(), (VariableOperand)stepIn(fa.getSelected()), o.getMethodName(), list, tmp, callee_args);
		}else{ // calling this instance method
			item = new MethodInvokeItem(parent.getBoard(), o.getMethodName(), list, tmp, callee_args);
		}
		parent.addSchedulerItem(item);
		/*
		System.out.println(o.getTargetMethod());
		System.out.println(" " + o.getTargetMethod().getName());
		System.out.println(" " + o.getTargetMethod().getArgs());
		*/
		result = tmp;
	}

	@Override
	public void visitNewArray(NewArray o) {
		for (Expr expr : o.getDimExpr()) {
			expr.accept(this);
		}
	}

	@Override
	public void visitNewClassExpr(NewClassExpr o) {
		for (Expr expr : o.getParameters()) {
			expr.accept(this);
		}
		result = newVariable("new_class", o.getType());
	}

	@Override
	public void visitParenExpr(ParenExpr o) {
		o.getExpr().accept(this);
	}

	@Override
	public void visitTypeCast(TypeCast o) {
		Operand v = stepIn(o.getExpr());
		VariableOperand tmp = newVariable("cast_expr", o.getType());
		parent.addSchedulerItem(new TypeCastItem(parent.getBoard(), v, tmp, v.getType(), o.getType()));
		result = tmp;
	}

	@Override
	public void visitUnaryExpr(UnaryExpr o) {
		Operand v = stepIn(o.getArg());
		ConstantOperand c1 = new ConstantOperand("1", v.getType());
		switch(o.getOp()){
		case INC:{
			VariableOperand tmp = newVariable("binary_expr", v.getType());
			parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.ADD, new Operand[]{v, c1}, tmp));
			parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.ASSIGN, new Operand[]{tmp}, (VariableOperand)v));
			break;
		}
		case DEC:{
			VariableOperand tmp = newVariable("binary_expr", v.getType());
			parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.SUB, new Operand[]{v, c1}, tmp));
			parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.ASSIGN, new Operand[]{tmp}, (VariableOperand)v));
			break;
		}
		default:
			System.out.println("unknown unary expr:" + o.getOp());
		}
		result = v;
	}
	
}

class GenSchedulerBoardTypeVisitor implements SynthesijerAstTypeVisitor {
	
	private Type type;

	public GenSchedulerBoardTypeVisitor(GenSchedulerBoardVisitor parent) {
		// TODO Auto-generated constructor stub
	}
	
	public Type getType(){
		return type;
	}

	@Override
	public void visitArrayType(ArrayType o) {
		o.getElemType().accept(this);
	}

	@Override
	public void visitComponentType(ComponentType o) {
		this.type = o;
	}

	@Override
	public void visitMySelfType(MySelfType o) {
		this.type = o;
	}

	@Override
	public void visitPrimitiveTypeKind(PrimitiveTypeKind o) {
		this.type = o;
	}

}