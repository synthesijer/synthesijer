package synthesijer.ast.expr;


public interface SynthesijerExprVisitor {

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

	public void visitCondExpr(CondExpr o);

}
