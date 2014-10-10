package synthesijer.scheduler;

import synthesijer.ast.Expr;
import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstTypeVisitor;
import synthesijer.ast.SynthesijerAstVisitor;
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
public class GenSchedulerBoardVisitor implements SynthesijerAstVisitor, SynthesijerAstTypeVisitor {
	
	private final SchedulerBoard board;
	private final GenSchedulerBoardVisitor parent;
	
	public GenSchedulerBoardVisitor(SchedulerBoard board) {
		this.parent = null;
		this.board = board;
	}

	public GenSchedulerBoardVisitor(GenSchedulerBoardVisitor parent) {
		this.parent = parent;
		this.board = parent.board;
	}
	
	private Variable stepIn(Expr expr){
		GenSchedulerBoardExprVisitor v = new GenSchedulerBoardExprVisitor(this);
		expr.accept(v);
		return v.getVariable();
	}

	@Override
	public void visitModule(Module o) {
		for (VariableDecl v : o.getVariableDecls()) {
			v.accept(this);
		}
		for (Method m : o.getMethods()) {
			m.accept(this);
		}
	}

	@Override
	public void visitMethod(Method o) {
		
		SchedulerItem exit = new SchedulerItem(Op.METHOD_EXIT, null, null);
		board.addItem(exit);
		MethodEntryItem entry = new MethodEntryItem(o.getName());
		board.addItem(entry);
		
		GenSchedulerBoardVisitor child = new GenSchedulerBoardVisitor(this);
		o.getBody().accept(child);
	}

	@Override
	public void visitBlockStatement(BlockStatement o) {
		for (Statement s : o.getStatements()) {
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
		o.getVariable().getType().accept(this);
		if (o.getInitExpr() != null)
			stepIn(o.getInitExpr());
	}

	@Override
	public void visitWhileStatement(WhileStatement o) {
		stepIn(o.getCondition());
		o.getBody().accept(this);
	}

	@Override
	public void visitArrayType(ArrayType o) {
		o.getElemType().accept(this);
	}

	@Override
	public void visitComponentType(ComponentType o) {
	}

	@Override
	public void visitMySelfType(MySelfType o) {
	}

	@Override
	public void visitPrimitiveTypeKind(PrimitiveTypeKind o) {
	}

}

class GenSchedulerBoardExprVisitor implements SynthesijerExprVisitor{
	
	public GenSchedulerBoardExprVisitor(GenSchedulerBoardVisitor parent) {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * returns the last result of expression chain.
	 * @return
	 */
	public Variable getVariable(){
		// TODO
		return null;
	}

	@Override
	public void visitArrayAccess(ArrayAccess o) {
		o.getIndexed().accept(this);
		o.getIndex().accept(this);
	}

	@Override
	public void visitAssignExpr(AssignExpr o) {
		o.getLhs().accept(this);
		o.getRhs().accept(this);
	}

	@Override
	public void visitAssignOp(AssignOp o) {
		o.getLhs().accept(this);
		o.getRhs().accept(this);
	}

	@Override
	public void visitBinaryExpr(BinaryExpr o) {
		o.getLhs().accept(this);
		o.getRhs().accept(this);
	}

	@Override
	public void visitFieldAccess(FieldAccess o) {
		o.getSelected().accept(this);
	}

	@Override
	public void visitIdent(Ident o) {
	}

	@Override
	public void visitLitral(Literal o) {
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