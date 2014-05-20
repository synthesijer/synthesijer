package synthesijer.ast;


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

public interface SynthesijerAstVisitor{

	public void visitMethod(Method o);

	public void visitModule(Module o);

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

	public void visitPrimitiveTypeKind(PrimitiveTypeKind o);

}
