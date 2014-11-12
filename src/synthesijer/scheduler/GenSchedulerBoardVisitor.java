package synthesijer.scheduler;

import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.IdentifierGenerator;
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
	
	private final SchedulerBoard board;
	private final IdentifierGenerator idGen;
	private final GenSchedulerBoardVisitor parent;
	
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
	
	private final Hashtable<String, Variable> varTable = new Hashtable<>();
	
	private SchedulerItem methodExit;
	
	public GenSchedulerBoardVisitor(SchedulerBoard board, IdentifierGenerator idGen) {
		this.parent = null;
		this.board = board;
		this.idGen = idGen;
		this.exitId = -1;
		this.continueId = -1;		
		this.breakId = -1;		
	}

	private GenSchedulerBoardVisitor(GenSchedulerBoardVisitor parent, int exitId, int breakId, int continueId) {
		this.parent = parent;
		this.board = parent.board;
		this.idGen = parent.idGen;
		this.exitId = exitId;
		this.methodExit = parent.methodExit;
		this.lastItem = parent.lastItem;
		this.breakId = breakId;		
		this.continueId = continueId;		
	}

	public IdentifierGenerator getIdGen(){
		return idGen;
	}
	
	public int getEntryId(){
		return entryId;
	}
	
	public void setEntryId(int id){
		entryId = id;
	}
	
	public Variable search(String key){
		if(varTable.containsKey(key)){
			return varTable.get(key);
		}else if(parent != null){
			return parent.search(key);
		}else{
			return null;
		}
		
	}
	
	private GenSchedulerBoardVisitor stepIn(Statement stmt, int exitId, int breakId, int continueId){
		GenSchedulerBoardVisitor v = new GenSchedulerBoardVisitor(this, exitId, breakId, continueId);
		stmt.accept(v);
		SchedulerItem ret = v.addMethodItem(new SchedulerItem(Op.JP, null, null)); // exit from this block
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
			this.methodExit = board.addItemInNewSlot(new SchedulerItem(Op.METHOD_EXIT, null, null));
			// breakId and continueId will not be defined for method
			GenSchedulerBoardVisitor child = new GenSchedulerBoardVisitor(this, methodExit.getStepId(), -1, -1);
			m.accept(child);
		}
	}

	@Override
	public void visitMethod(Method o) {
		
		SchedulerItem entry = board.addItemInNewSlot(new MethodEntryItem(o.getName()));
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
		SchedulerItem br = addMethodItem(new SchedulerItem(Op.BREAK, null, null));
		br.setBranchId(breakId);
	}

	@Override
	public void visitContinueStatement(ContinueStatement o) {
		SchedulerItem br = addMethodItem(new SchedulerItem(Op.CONTINUE, null, null));
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
		SchedulerItem fork = addMethodItem(new SchedulerItem(Op.JT, new Operand[]{flag}, null)); // jump on condition
		SchedulerItem join = addMethodItem(new SchedulerItem(Op.JP, null, null)); // join point to go to branch following
		
		int beforeUpdateId = lastItem.getStepId(); // entry point for updte statements
		for (Statement s : o.getUpdates()){
			s.accept(this);
		}
		SchedulerItem loop_back = addMethodItem(new SchedulerItem(Op.JP, null, null)); // join point to go to branch following
		loop_back.setBranchId(afterInitId+1); // after loop body and update, back to condition checking
		
		GenSchedulerBoardVisitor v = stepIn(o.getBody(), beforeUpdateId+1, join.getStepId(), beforeUpdateId);
		fork.setBranchIds(new int[]{v.getEntryId(), join.getStepId()}); // fork into loop body or exit
		join.setBranchId(lastItem.getStepId()+1); // next
	}

	@Override
	public void visitIfStatement(IfStatement o) {
		Operand flag = stepIn(o.getCondition());
		SchedulerItem fork = addMethodItem(new SchedulerItem(Op.JT, new Operand[]{flag}, null)); // jump on condition
		SchedulerItem join = addMethodItem(new SchedulerItem(Op.JP, null, null)); // join point to go to branch following
		
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
			ret = addMethodItem(new SchedulerItem(Op.RETURN, new Operand[]{v}, null));
		}else{
			ret = addMethodItem(new SchedulerItem(Op.RETURN, null, null));
		}
		ret.setBranchId(methodExit.getStepId());
	}

	@Override
	public void visitSkipStatement(SkipStatement o) {
	}

	@Override
	public void visitSwitchStatement(SwitchStatement o) {
		ArrayList<SwitchStatement.Elem> elements = o.getElements();
		Operand[] selector_list = new Operand[elements.size() + 1];
		int[] branchIDs = new int[elements.size() + 1]; // #. patterns and default
		selector_list[0] = stepIn(o.getSelector()); // target operand
				
		// gather selector operand
		for(int i = 0; i < elements.size(); i++){
			SwitchStatement.Elem elem = elements.get(i);
			selector_list[i+1] = stepIn(elem.getPattern());
		}

		SchedulerItem fork = addMethodItem(new SchedulerItem(Op.SELECT, selector_list, null));
		SchedulerItem join = addMethodItem(new SchedulerItem(Op.JP, null, null)); // join point to go to branch following
		
		int nextCaseId;
		{ // for default
			int idx = elements.size();
			branchIDs[idx] = lastItem.getStepId() + 1; // currentID + 1
			SwitchStatement.Elem elem = o.getDefaultElement();
			GenSchedulerBoardVisitor v = new GenSchedulerBoardVisitor(this, join.getStepId(), join.getStepId(), continueId);
			elem.accept(v);
			lastItem = v.lastItem;
			SchedulerItem ret = addMethodItem(new SchedulerItem(Op.JP, null, null)); // exit from this block
			ret.setBranchId(join.getStepId());
			nextCaseId = branchIDs[idx];
		}
		
        // switch body (access in manner of reverse order)
		for(int i = 0; i < elements.size(); i++){
			int idx = elements.size() - i - 1;
			branchIDs[idx] = lastItem.getStepId() + 1; // currentID
			SwitchStatement.Elem elem = elements.get(idx);
			GenSchedulerBoardVisitor v = new GenSchedulerBoardVisitor(this, nextCaseId, join.getStepId(), continueId);
			elem.accept(v);
			lastItem = v.lastItem;
			SchedulerItem ret = addMethodItem(new SchedulerItem(Op.JP, null, null)); // exit from this block
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
		Variable v = new Variable(o.getName(), t);
		varTable.put(o.getName(), v);
		if (o.getInitExpr() != null){
			Operand src = stepIn(o.getInitExpr());
			addMethodItem(new SchedulerItem(Op.ASSIGN, new Operand[]{src}, v));
		}
	}

	@Override
	public void visitWhileStatement(WhileStatement o) {
		int loopEntryId = lastItem.getStepId(); // return point for "for loop"

		Operand flag = stepIn(o.getCondition());
		SchedulerItem fork = addMethodItem(new SchedulerItem(Op.JT, new Operand[]{flag}, null)); // jump on condition
		SchedulerItem join = addMethodItem(new SchedulerItem(Op.JP, null, null)); // join point to go to branch following
		
		GenSchedulerBoardVisitor v = stepIn(o.getBody(), loopEntryId+1, join.getStepId(), loopEntryId+1);

		fork.setBranchIds(new int[]{v.getEntryId(), join.getStepId()}); // fork into loop body or exit
		join.setBranchId(lastItem.getStepId()+1); // next
	}
	
	public SchedulerItem addMethodItem(SchedulerItem item){
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
		Type type;
		if(indexed.getType() instanceof ArrayType){
			type = ((ArrayType)o.getIndexed().getType()).getElemType();
		}else{
			type = PrimitiveTypeKind.VOID;
		}
		Variable tmp = newVariable("array_access", type);
		parent.addMethodItem(new SchedulerItem(Op.ARRAY_ACCESS, new Operand[]{indexed, index}, tmp));
		result = tmp;
	}

	@Override
	public void visitAssignExpr(AssignExpr o) {
		Operand lhs = stepIn(o.getLhs());
		Operand rhs = stepIn(o.getRhs());
		parent.addMethodItem(new SchedulerItem(Op.ASSIGN, new Operand[]{rhs}, (Variable)lhs));
	}

	@Override
	public void visitAssignOp(AssignOp o) {
		Operand lhs = stepIn(o.getLhs());
		Operand rhs = stepIn(o.getRhs());
		Variable tmp = newVariable("binary_expr", lhs.getType());
		parent.addMethodItem(new SchedulerItem(Op.get(o.getOp()), new Operand[]{lhs,rhs}, tmp));
		parent.addMethodItem(new SchedulerItem(Op.ASSIGN, new Operand[]{tmp}, (Variable)lhs));
		result = lhs;
	}

	private Variable newVariable(String key, Type t){
		return new Variable(String.format("%s_%05d", key, parent.getIdGen().id()), t);
	}
	
	@Override
	public void visitBinaryExpr(BinaryExpr o) {
		Operand lhs = stepIn(o.getLhs());
		Operand rhs = stepIn(o.getRhs());
		result = newVariable("binary_expr", lhs.getType());
		parent.addMethodItem(new SchedulerItem(Op.get(o.getOp()), new Operand[]{lhs,rhs}, (Variable)result));
	}

	@Override
	public void visitFieldAccess(FieldAccess o) {
		Variable tmp = newVariable("field_access", PrimitiveTypeKind.UNDEFIEND);
		Operand v = stepIn(o.getSelected());
		parent.addMethodItem(new FieldAccessItem((Variable)v, o.getIdent().getSymbol(), null, tmp));
		result = tmp;
	}

	@Override
	public void visitIdent(Ident o) {
		result = parent.search(o.getSymbol());
	}

	@Override
	public void visitLitral(Literal o) {
		result = new Constant(o.getValueAsStr(), o.getType());
	}

	@Override
	public void visitMethodInvocation(MethodInvocation o) {
		Expr method = o.getMethod();
		Variable tmp = newVariable("method_result", o.getType());
		ArrayList<Expr> params = o.getParameters();
		Operand[] list = new Operand[params.size()];
		for(int i = 0; i < params.size(); i++){
			list[i] = stepIn(params.get(i));
		}
		if (method instanceof FieldAccess) { // calling other instance method
			FieldAccess fa = (FieldAccess)method;
			parent.addMethodItem(new MethodInvokeItem((Variable)stepIn(fa.getSelected()), o.getMethodName(), list, tmp));
		}else{ // calling this instance method
			parent.addMethodItem(new MethodInvokeItem(o.getMethodName(), list, tmp));
		}
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
		o.getExpr().accept(this);
	}

	@Override
	public void visitUnaryExpr(UnaryExpr o) {
		Operand v = stepIn(o.getArg());
		Constant c1 = new Constant("1", v.getType());
		switch(o.getOp()){
		case INC:{
			Variable tmp = newVariable("binary_expr", v.getType());
			parent.addMethodItem(new SchedulerItem(Op.ADD, new Operand[]{v, c1}, tmp));
			parent.addMethodItem(new SchedulerItem(Op.ASSIGN, new Operand[]{tmp}, (Variable)v));
			break;
		}
		case DEC:{
			Variable tmp = newVariable("binary_expr", v.getType());
			parent.addMethodItem(new SchedulerItem(Op.SUB, new Operand[]{v, c1}, tmp));
			parent.addMethodItem(new SchedulerItem(Op.ASSIGN, new Operand[]{tmp}, (Variable)v));
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