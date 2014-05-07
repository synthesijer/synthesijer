package synthesijer.ast;


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

public interface SynthesijerAstVisitor {

	public void visitMethod(Method o);

	public void visitModule(Module o);

	public void visitArrayAccess(ArrayAccess o);

	public void visitAssignExpr(AssignExpr o);

	public void visitAssignOp(AssignOp o);

	public void visitBinaryExpr(BinaryExpr o);

	public void visitFieldAccess(FieldAccess o);

	public void visitIdent(Ident o);

	public void visitLitral(Literal o);

	public void visitMethodInvocation(MethodInvocation o);

	public void visitNewArray(NewArray o);

	public void visitNewClassExpr(NewClassExpr o);

	public void visitParenExpr(ParenExpr o);

	public void visitTypeCast(TypeCast o);

	public void visitUnaryExpr(UnaryExpr o);

	public void visitBlockStatement(BlockStatement o);

	public void visitBreakStatement(BreakStatement o);

	public void visitContinueStatement(ContinueStatement o);

	public void visitExprStatement(ExprStatement o);

	public void visitForStatement(ForStatement o);

	public void visitIfStatement(IfStatement o);

	public void visitReturnStatement(ReturnStatement o);

	public void visitSkipStatement(SkipStatement o);

	public void visitSwitchStatement(SwitchStatement o);

	public void visitSwitchCaseElement(SwitchStatement.Elem o);
	
	public void visitSynchronizedBlock(SynchronizedBlock o);

	public void visitTryStatement(TryStatement o);

	public void visitVariableDecl(VariableDecl o);

	public void visitWhileStatement(WhileStatement o);

	public void visitArrayType(ArrayType o);

	public void visitComponentType(ComponentType o);

	public void visitMySelfType(MySelfType o);

	public void visitPrimitivyTypeKind(PrimitiveTypeKind o);

}
