package synthesijer;

import java.io.PrintWriter;

import synthesijer.ast.Method;
import synthesijer.ast.Module;
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
import synthesijer.ast.statement.SwitchStatement.Elem;
import synthesijer.ast.statement.SynchronizedBlock;
import synthesijer.ast.statement.TryStatement;
import synthesijer.ast.statement.VariableDecl;
import synthesijer.ast.statement.WhileStatement;
import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.ComponentType;
import synthesijer.ast.type.MySelfType;
import synthesijer.ast.type.PrimitiveTypeKind;
import synthesijer.model.Statemachine;

public class DumpStatemachineVisitor implements SynthesijerAstVisitor{
	
	private PrintWriter dest;
	
	public DumpStatemachineVisitor(PrintWriter dest){
		this.dest = dest;
	}

	@Override
	public void visitMethod(Method o) {
		Statemachine m = o.getStateMachine();
		
	}

	@Override
	public void visitModule(Module o) {
		dest.printf("digraph " + o.getName() + "{\n");
		for(Method m: o.getMethods()){
			m.accept(this);
		}
		dest.printf("}\n");
	}

	@Override
	public void visitArrayAccess(ArrayAccess o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitAssignExpr(AssignExpr o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitAssignOp(AssignOp o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitBinaryExpr(BinaryExpr o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitFieldAccess(FieldAccess o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitIdent(Ident o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitLitral(Literal o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitMethodInvocation(MethodInvocation o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitNewArray(NewArray o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitNewClassExpr(NewClassExpr o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitParenExpr(ParenExpr o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitTypeCast(TypeCast o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitUnaryExpr(UnaryExpr o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitBlockStatement(BlockStatement o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitBreakStatement(BreakStatement o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitContinueStatement(ContinueStatement o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitExprStatement(ExprStatement o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitForStatement(ForStatement o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitIfStatement(IfStatement o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitReturnStatement(ReturnStatement o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitSkipStatement(SkipStatement o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitSwitchStatement(SwitchStatement o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitSwitchCaseElement(Elem o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitSynchronizedBlock(SynchronizedBlock o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitTryStatement(TryStatement o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitVariableDecl(VariableDecl o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitWhileStatement(WhileStatement o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitArrayType(ArrayType o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitComponentType(ComponentType o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitMySelfType(MySelfType o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitPrimitivyTypeKind(PrimitiveTypeKind o) {
		// TODO Auto-generated method stub
		
	}

}