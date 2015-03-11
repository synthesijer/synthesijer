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
import synthesijer.ast.Variable;
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
import synthesijer.ast.type.ArrayRef;
import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.BitVector;
import synthesijer.ast.type.ComponentRef;
import synthesijer.ast.type.ComponentType;
import synthesijer.ast.type.MySelfType;
import synthesijer.ast.type.PrimitiveTypeKind;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLType;

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
	private final ArrayList<VariableOperand> varList = new ArrayList<>();
	
	private SchedulerItem methodExit;
	
	public GenSchedulerBoardVisitor(SchedulerInfo info, IdentifierGenerator idGen) {
		this.info = info;
		this.parent = null;
		this.board = null;
		this.idGen = idGen;
		this.exitId = -1;
		this.continueId = -1;		
		this.breakId = -1;
		info.addVarTable(varList);
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
		info.addVarTable(varList);
		board.addVarTable(varList);
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
		varList.add(v);
	}
	
	private GenSchedulerBoardVisitor stepIn(Statement stmt, int exitId, int breakId, int continueId){
		GenSchedulerBoardVisitor v = new GenSchedulerBoardVisitor(this, board, exitId, breakId, continueId);
		stmt.accept(v);
		SchedulerItem ret = v.addSchedulerItem(new SchedulerItem(board, Op.JP, null, null)); // exit from this block
		ret.setBranchId(exitId);
		lastItem = v.lastItem; // update last item
		if(entryId < 0) entryId = v.entryId;
		return v;
	}

	private Operand stepIn(Expr expr){
		GenSchedulerBoardExprVisitor v = new GenSchedulerBoardExprVisitor(this);
		expr.accept(v);
		Operand o = v.getOperand();
		return o;
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
			this.methodExit.setBranchId(methodExit.getStepId()+1);
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
		GenSchedulerBoardVisitor forVisitor = new GenSchedulerBoardVisitor(this, board, exitId, breakId, continueId);
		//GenSchedulerBoardVisitor forVisitor = this;
		if(entryId > 0) forVisitor.entryId = entryId; // copy entryID when entryId is already settled.
		for (Statement s : o.getInitializations()) {
			s.accept(forVisitor);
		}
		int afterInitId = forVisitor.lastItem.getStepId(); // return point for "for loop"
		if(entryId == -1) entryId = forVisitor.entryId; // copy entryID when entryId is not settled yet.
		
		Operand flag = forVisitor.stepIn(o.getCondition());
		SchedulerItem fork = forVisitor.addSchedulerItem(new SchedulerItem(board, Op.JT, new Operand[]{flag}, null)); // jump on condition
		SchedulerItem join = forVisitor.addSchedulerItem(new SchedulerItem(board, Op.JP, null, null)); // join point to go to branch following
		
		int beforeUpdateId = forVisitor.lastItem.getStepId(); // entry point for update statements
		for (Statement s : o.getUpdates()){
			s.accept(forVisitor);
		}
		SchedulerItem loop_back = forVisitor.addSchedulerItem(new SchedulerItem(board, Op.JP, null, null)); // join point to go to branch following
		loop_back.setBranchId(afterInitId+1); // after loop body and update, back to condition checking
		
		GenSchedulerBoardVisitor v = forVisitor.stepIn(o.getBody(), beforeUpdateId+1, join.getStepId(), beforeUpdateId);
		fork.setBranchIds(new int[]{v.getEntryId(), join.getStepId()}); // fork into loop body or exit
		join.setBranchId(forVisitor.lastItem.getStepId()+1); // next
		//lastItem = forVisitor.lastItem;
		//varTable = preservVarTable;
//		forVisitor.entryId = entryTmp;
		lastItem = forVisitor.lastItem; // write-back
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
	
	private boolean isCastRequired(Type t0, Type t1){
		if(t0 == t1) return false;
		Type pt0 = t0, pt1 = t1;
		if(pt0 instanceof ArrayRef){
			pt0 = ((ArrayRef) pt0).getRefType().getElemType(); 
		}
		if(pt1 instanceof ArrayRef){
			pt1 = ((ArrayRef) pt1).getRefType().getElemType(); 
		}
		if(pt0 == pt1) return false;
		return true;
	}

	private boolean isFloat(Type t){
		return t == PrimitiveTypeKind.FLOAT;
	}
	
	private boolean isDouble(Type t){
		return t == PrimitiveTypeKind.DOUBLE;
	}
	
	public VariableOperand addCast(Operand v, Type target){
		Operand src = v;
		VariableOperand tmp = newVariable("cast_expr", target);
		if(isDouble(target)){
			// only long->double or float->double are allowed 
			if(v.getType() != PrimitiveTypeKind.LONG && v.getType() != PrimitiveTypeKind.FLOAT){
				src = addCast(src, PrimitiveTypeKind.LONG);
			}
		}else if(isFloat(target)){
			// only int->float or double->float are allowed 
			if(v.getType() != PrimitiveTypeKind.INT && v.getType() != PrimitiveTypeKind.DOUBLE){
				src = addCast(src, PrimitiveTypeKind.INT);
			}
		}else if(isDouble(v.getType())){
			// only double->long or double->float are allowed 
			if(target != PrimitiveTypeKind.LONG && target != PrimitiveTypeKind.FLOAT){
				src = addCast(src, PrimitiveTypeKind.LONG);
			}
		}else if(isFloat(v.getType())){
			// only float->int or float->double are allowed 
			if(target != PrimitiveTypeKind.INT && target != PrimitiveTypeKind.DOUBLE){
				src = addCast(src, PrimitiveTypeKind.INT);
			}
		}
		
		addSchedulerItem(TypeCastItem.newCastItem(getBoard(), src, tmp, src.getType(), target)); // src -> tmp::target
		return tmp;
	}

	private VariableOperand newVariable(String key, Type t){
		VariableOperand v = new VariableOperand(String.format("%s_%05d", key, getIdGen().id()), t);
		addVariable(v.getName(), v);
		return v; 
	}
	
	@Override
	public void visitVariableDecl(VariableDecl o) {
		//Type t = stepIn(o.getVariable().getType());
		Variable vv = o.getVariable();
		Type t = vv.getType();
		String prefix = o.getScope().getMethod() == null ? "class" : o.getScope().getMethod().getName();
		String vName;
		vName = String.format("%s_%s_%04d", prefix, o.getName(), idGen.id());
		VariableOperand v;
		if(vv.getMethod() != null){
			v = new VariableOperand(vName, t, vv.getInitExpr(), vv.isPublic(), vv.isGlobalConstant(), vv.isMethodParam(), vv.getName(), vv.getMethod().getName(), vv.getMethod().isPrivate());
		}else{
			v = new VariableOperand(vName, t, vv.getInitExpr(), vv.isPublic(), vv.isGlobalConstant(), vv.isMethodParam(), vv.getName(), null, false);
		}
		//Variable v = new Variable(o.getVariable().getUniqueName(), t);
		varTable.put(o.getName(), v);
		varList.add(v);
		if (o.getInitExpr() != null){
			//System.out.println(o);

			Operand src = stepIn(o.getInitExpr());
			
			if(src.getType() != PrimitiveTypeKind.DECLARED){ // allows to cast
				if(isCastRequired(v.getType(), src.getType()) == true){
					if(src instanceof VariableOperand){
						VariableOperand tmp = addCast(src, v.getType()); // RHS is casted into corresponding to LHS
						if(tmp != null) src = tmp;
					}else if(src instanceof ConstantOperand){
						// regenerate constant with appropriate type info.
						src = new ConstantOperand(((ConstantOperand) src).getOrigValue(), v.getType());
					}
				}
			}

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

	/*
	private Type stepIn(Type type){
		GenSchedulerBoardTypeVisitor v = new GenSchedulerBoardTypeVisitor(this.parent);
		type.accept(v);
		return v.getType();
	}
	*/
	
	@Override
	public void visitArrayAccess(ArrayAccess o) {
		VariableOperand indexed = (VariableOperand)(stepIn(o.getIndexed()));
		Operand index = stepIn(o.getIndex());
//		System.out.println("visitArrayAccess:index: " + indexed.getName());
//		System.out.println("visitArrayAccess:indexType: " + indexed.getType());
		
		ArrayRef type;
		if(indexed.getType() instanceof ArrayRef){
			type = (ArrayRef)indexed.getType();
		}else if(indexed.getType() instanceof ComponentRef){
			ComponentRef ref = (ComponentRef)indexed.getType();
			type = new ArrayRef((ArrayType)ref.getRefType());
		}else{
			type = new ArrayRef((ArrayType)indexed.getType());
		}
		
		//ArrayRef type = new ArrayRef((ArrayType)indexed.getType());
		VariableOperand tmp = newVariableRef("array_access", type, indexed);
		parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.ARRAY_ACCESS, new Operand[]{indexed, index}, tmp));
		result = tmp;
	}
	
	private boolean isCastRequired(Type t0, Type t1){
		if(t0 == t1) return false;
		Type pt0 = t0, pt1 = t1;
		if(pt0 instanceof ArrayRef){
			pt0 = ((ArrayRef) pt0).getRefType().getElemType(); 
		}
		if(pt1 instanceof ArrayRef){
			pt1 = ((ArrayRef) pt1).getRefType().getElemType(); 
		}
		if(pt0 == pt1) return false;
		return true;
	}
		
	@Override
	public void visitAssignExpr(AssignExpr o) {
		Operand lhs = stepIn(o.getLhs());
		Operand rhs = stepIn(o.getRhs());
		if(isCastRequired(lhs.getType(), rhs.getType()) == true){
			if(rhs instanceof VariableOperand){
				VariableOperand tmp = parent.addCast(rhs, lhs.getType()); // RHS is casted into corresponding to LHS
				if(tmp != null) rhs = tmp;
			}else if(rhs instanceof ConstantOperand){
				((ConstantOperand) rhs).setType(lhs.getType());
			}
		}
		parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.ASSIGN, new Operand[]{rhs}, (VariableOperand)lhs));
	}

	@Override
	public void visitAssignOp(AssignOp o) {
		Operand lhs = stepIn(o.getLhs());
		Operand rhs = stepIn(o.getRhs());
		//VariableOperand tmp = newVariable("binary_expr", stepIn(lhs.getType()));
		VariableOperand tmp = newVariable("binary_expr", lhs.getType());
		Op op = Op.get(o.getOp(), lhs, rhs);
		//parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.get(o.getOp()), new Operand[]{lhs,rhs}, tmp));
		parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), op, new Operand[]{lhs,rhs}, tmp));
		parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.ASSIGN, new Operand[]{tmp}, (VariableOperand)lhs));
		result = lhs;
	}

	private VariableOperand newVariable(String key, Type t){
		VariableOperand v = new VariableOperand(String.format("%s_%05d", key, parent.getIdGen().id()), t);
		parent.addVariable(v.getName(), v);
		return v; 
	}
	
	private VariableOperand newVariable(String key, Type t, Expr initExpr){
		VariableOperand v = new VariableOperand(String.format("%s_%05d", key, parent.getIdGen().id()), t, initExpr, false, false, false, null, "", false);
		parent.addVariable(v.getName(), v);
		return v; 
	}

	private VariableRefOperand newVariableRef(String key, Type t, VariableOperand ref){
		VariableRefOperand v = new VariableRefOperand(String.format("%s_%05d", key, parent.getIdGen().id()), t, ref);
		parent.addVariable(v.getName(), v);
		return v; 
	}

	private int getWidth(Type t){
		if(t instanceof PrimitiveTypeKind){
			return ((PrimitiveTypeKind) t).getWidth();
		}else if(t instanceof BitVector){
			return ((BitVector) t).getWidth();
		}else{
			return -1;
		}
	}
	
	private boolean isFloat(Type t){
		return t == PrimitiveTypeKind.FLOAT;
	}
	
	private boolean isDouble(Type t){
		return t == PrimitiveTypeKind.DOUBLE;
	}
	
	private boolean isFloating(Type t){
		return isFloat(t) || isDouble(t);
	}
	
	private Type getPreferableType(Type t0, Type t1){
		if(t0 == t1){
			return t0;
		}
		if(isFloating(t0) || isFloating(t1)){
			if(isDouble(t0) || isDouble(t1)) return PrimitiveTypeKind.DOUBLE;
			return PrimitiveTypeKind.FLOAT; // t0 and/or t1 is float
		}
		int w0 = getWidth(t0);
		int w1 = getWidth(t1);
		if(w0 < 0 || w1 < 0){
			SynthesijerUtils.warn("cannot convert:" + t0 + " <-> " + t1 + ", then use " + t0);
			return t0; // skip
		}
		return w0 > w1 ? t0 : t1;
	}
	
	@Override
	public void visitBinaryExpr(BinaryExpr o) {
		Operand lhs = stepIn(o.getLhs());
		Operand rhs = stepIn(o.getRhs());
		Type type;
		Op op = Op.get(o.getOp(), lhs, rhs);
		
		
		if(isCastRequired(lhs.getType(), rhs.getType()) == true){
			type = getPreferableType(lhs.getType(), rhs.getType()); // select type
		}else{
			type = lhs.getType();
		}
		
		if(isCastRequired(lhs.getType(), rhs.getType()) == true && lhs.getType() != type){
			if(lhs instanceof VariableOperand){
				VariableOperand tmp = parent.addCast(lhs, type);
				if(tmp != null) lhs = tmp;
			}else if(lhs instanceof ConstantOperand){ // ConstantOperand
				((ConstantOperand) lhs).setType(type);
			}
		}
		if(isCastRequired(lhs.getType(), rhs.getType()) == true && rhs.getType() != type){
			if(rhs instanceof VariableOperand){
				VariableOperand tmp = parent.addCast(rhs, type);
				if(tmp != null) rhs = tmp;
			}else if(rhs instanceof ConstantOperand){ // ConstantOperand
				((ConstantOperand) rhs).setType(type);
			}
		}
		
		if(op.isForcedType()){
			type = op.getType();
		}

		result = newVariable("binary_expr", type);
		parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), op, new Operand[]{lhs,rhs}, (VariableOperand)result));
	}
	
	
	private Type convType(HDLType type){
		if(type instanceof HDLPrimitiveType == false){
			SynthesijerUtils.error("unsupported non-HDLPrimitiveType in user written HDLModule");
			throw new RuntimeException("unsupported non-HDLPrimitiveType in user written HDLModule");
		}
		HDLPrimitiveType t0 = (HDLPrimitiveType)type;
		
		switch(t0.getKind()){
		case BIT:
			return PrimitiveTypeKind.BOOLEAN;
		case VECTOR:
			return new BitVector(t0.getWidth());
		case SIGNED:
			switch(t0.getWidth()){
			case  8: return PrimitiveTypeKind.BYTE;
			case 16: return PrimitiveTypeKind.SHORT;
			case 32: return PrimitiveTypeKind.INT;
			case 64: return PrimitiveTypeKind.LONG;
			default:
				SynthesijerUtils.error("supported only 8-, 16-, 32-, and 64- signed type in user written HDLModule");
				throw new RuntimeException("supported only 8-, 16-, 32-, and 64- signed type in user written HDLModule");
			}
		default:
			SynthesijerUtils.error("unsupported non-HDLPrimitiveType in user written HDLModule");
			throw new RuntimeException("unsupported non-HDLPrimitiveType in user written HDLModule");
		}
	}
	

	@Override
	public void visitFieldAccess(FieldAccess o) {
		VariableOperand tmp;
		Type type = PrimitiveTypeKind.UNDEFIEND;
//		System.out.println("visitFieldAccess:Type: " + o.getType());
		if(o.getType() instanceof ArrayType && o.getIdent().getSymbol().equals("length")){
			type = PrimitiveTypeKind.INT;
			tmp = newVariable("field_access", type);
			Operand v = stepIn(o.getSelected());
			parent.addSchedulerItem(new FieldAccessItem(parent.getBoard(), (VariableOperand)v, o.getIdent().getSymbol(), null, tmp));
		}else if(o.getType() instanceof ComponentType){
			ComponentType ct = (ComponentType)o.getType();
//			System.out.println(" component:" + ct.getName());
//			System.out.println(" field:" + o.getIdent());
			VariableInfo var = GlobalSymbolTable.INSTANCE.searchVariable(ct.getName(), o.getIdent().getSymbol());
//			System.out.println(var);
			if(var == null){
				SynthesijerUtils.error("visitFieldAccess:" + ct.getName() + ":" + o.getIdent().getSymbol());
			}
			if(var.var == null && var.port != null){
				type = convType(var.port.getType());
			}else{
				type = var.var.getType();
			}

			if(type instanceof ArrayType){
				tmp = newVariable("field_access", new ArrayRef((ArrayType)type));
			}else{
				tmp = newVariable("field_access", type);
			}

			//tmp = newVariable("field_access", new ComponentRef(ct, type));
			Operand v = stepIn(o.getSelected());
			parent.addSchedulerItem(new FieldAccessItem(parent.getBoard(), (VariableOperand)v, o.getIdent().getSymbol(), null, tmp));
			//System.out.println("visitFiledAccess: " + tmp.getName() + "::" + tmp.getType());
		}else{
			// "public static final" is only allowed. 
			String klass = ((Ident)o.getSelected()).getSymbol();
			VariableInfo var = GlobalSymbolTable.INSTANCE.searchVariable(klass, o.getIdent().getSymbol());
			type = var.var.getType();
			tmp = newVariable("field_access", type);
			Operand v = stepIn(var.var.getInitExpr());
			parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.ASSIGN, new Operand[]{v}, tmp));
		}
		
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
		MethodInvokeItem item;
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
		item.setBranchId(item.getStepId() + 1);
		item.setNoWait(o.getTargetMethod().isNoWait());
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
		if(v instanceof VariableOperand){
			result = parent.addCast(v, o.getType());
		}else if(v instanceof ConstantOperand){
			//System.out.println(((ConstantOperand) v).getValue());
			result = new ConstantOperand(((ConstantOperand) v).getOrigValue(), o.getType());
		}
	}

	@Override
	public void visitUnaryExpr(UnaryExpr o) {
		Operand v = stepIn(o.getArg());
		ConstantOperand c1 = new ConstantOperand("1", v.getType());
		ConstantOperand c0 = new ConstantOperand("0", v.getType());
		switch(o.getOp()){
		case INC:{
			VariableOperand tmp = newVariable("unary_expr", v.getType());
			parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.ADD, new Operand[]{v, c1}, tmp));
			parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.ASSIGN, new Operand[]{tmp}, (VariableOperand)v));
			result = v;
			break;
		}
		case DEC:{
			VariableOperand tmp = newVariable("unary_expr", v.getType());
			parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.SUB, new Operand[]{v, c1}, tmp));
			parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.ASSIGN, new Operand[]{tmp}, (VariableOperand)v));
			result = v;
			break;
		}
		case MINUS:{
			VariableOperand tmp = newVariable("unary_expr", v.getType());
			if(isFloating(tmp.getType())){
				parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.MSB_FLAP, new Operand[]{v}, tmp));
			}else{
				parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.SUB, new Operand[]{c0, v}, tmp));
			}
			result = tmp;
			break;
		}
		case LNOT:{
			VariableOperand tmp = newVariable("unary_expr", v.getType());
			parent.addSchedulerItem(new SchedulerItem(parent.getBoard(), Op.NOT, new Operand[]{v}, tmp));
			result = tmp;
			break;
		}
		default:
			System.out.println("unknown unary expr:" + o.getOp());
		}
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
	public void visitArrayRef(ArrayRef o){
		//o.getElemType().accept(this);
		this.type = o;
	}

	@Override
	public void visitComponentType(ComponentType o) {
		this.type = o;
	}

	@Override
	public void visitComponentRef(ComponentRef o){
		//o.getElemType().accept(this);
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