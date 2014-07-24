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

public interface SynthesijerAstVisitor extends SynthesijerModuleVisitor, SynthesijerMethodVisitor{

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

}
