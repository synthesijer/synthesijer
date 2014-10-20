package synthesijer.scheduler;

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
	
	private final Hashtable<String, Variable> varTable = new Hashtable<>();
	
	public GenSchedulerBoardVisitor(SchedulerBoard board, IdentifierGenerator idGen) {
		this.parent = null;
		this.board = board;
		this.idGen = idGen;
	}

	private GenSchedulerBoardVisitor(GenSchedulerBoardVisitor parent) {
		this.parent = parent;
		this.board = parent.board;
		this.idGen = parent.idGen;
	}
	
	public SchedulerBoard getBoard(){
		return board;
	}
	
	public IdentifierGenerator getIdGen(){
		return idGen;
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

	private SchedulerItem entry, exit;

	@Override
	public void visitModule(Module o) {
		for (VariableDecl v : o.getVariableDecls()) {
			v.accept(this);
		}
		for (Method m : o.getMethods()) {
			GenSchedulerBoardVisitor child = new GenSchedulerBoardVisitor(this);
			m.accept(child);
		}
	}

	@Override
	public void visitMethod(Method o) {
		if(o.isConstructor()) return; // skip 
		if(o.isUnsynthesizable()) return; // skip
		this.exit = new SchedulerItem(Op.METHOD_EXIT, null, null);
		board.addItem(this.exit);
		this.entry = new MethodEntryItem(o.getName());
		board.addItem(this.entry);
		this.entry.setBranchId(this.exit.getStepId()); // jump to exit temporally.
		o.getBody().accept(this);
	}

	@Override
	public void visitBlockStatement(BlockStatement o) {
		for (Statement s : o.getStatements()) {
			System.out.println(s);
			s.accept(this);
		}
	}

	@Override
	public void visitBreakStatement(BreakStatement o) {
	}

	@Override
	public void visitContinueStatement(ContinueStatement o) {
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
		stepIn(o.getCondition());
		for (Statement s : o.getUpdates())
			s.accept(this);
		o.getBody().accept(this);
	}

	@Override
	public void visitIfStatement(IfStatement o) {
		stepIn(o.getCondition());
		o.getThenPart().accept(this);
		if (o.getElsePart() != null) {
			o.getElsePart().accept(this);
		}
	}

	@Override
	public void visitReturnStatement(ReturnStatement o) {
		if (o.getExpr() != null)
			stepIn(o.getExpr());
	}

	@Override
	public void visitSkipStatement(SkipStatement o) {
	}

	@Override
	public void visitSwitchStatement(SwitchStatement o) {
		stepIn(o.getSelector());
		for (SwitchStatement.Elem elem : o.getElements()) {
			elem.accept(this);
		}
	}

	public void visitSwitchCaseElement(SwitchStatement.Elem o) {
		stepIn(o.getPattern());
		for (Statement s : o.getStatements())
			s.accept(this);
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
			board.addItem(new SchedulerItem(Op.ASSIGN, new Operand[]{src}, v));
		}
	}

	@Override
	public void visitWhileStatement(WhileStatement o) {
		stepIn(o.getCondition());
		o.getBody().accept(this);
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
		o.getIndexed().accept(this);
		o.getIndex().accept(this);
	}

	@Override
	public void visitAssignExpr(AssignExpr o) {
		Operand lhs = stepIn(o.getLhs());
		Operand rhs = stepIn(o.getRhs());
		parent.getBoard().addItem(new SchedulerItem(Op.ASSIGN, new Operand[]{rhs}, (Variable)lhs));
	}

	@Override
	public void visitAssignOp(AssignOp o) {
		o.getLhs().accept(this);
		o.getRhs().accept(this);
	}

	private Variable newVariable(String key, Type t){
		return new Variable(String.format("%s_%05d", key, parent.getIdGen().id()), t);
	}
	
	@Override
	public void visitBinaryExpr(BinaryExpr o) {
		Operand lhs = stepIn(o.getLhs());
		Operand rhs = stepIn(o.getRhs());
		result = newVariable("binary_expr", lhs.getType());
		parent.getBoard().addItem(new SchedulerItem(Op.get(o.getOp()), new Operand[]{lhs,rhs}, (Variable)result));
	}

	@Override
	public void visitFieldAccess(FieldAccess o) {
		o.getSelected().accept(this);
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
		if (method instanceof FieldAccess) {
			method.accept(this);
		}
		for (Expr expr : o.getParameters()) {
			expr.accept(this);
		}
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
		o.getArg().accept(this);
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