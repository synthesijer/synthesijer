package synthesijer;

import synthesijer.ast.Expr;
import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Variable;
import synthesijer.ast.expr.ArrayAccess;
import synthesijer.ast.expr.AssignExpr;
import synthesijer.ast.expr.AssignOp;
import synthesijer.ast.expr.BinaryExpr;
import synthesijer.ast.expr.CondExpr;
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
import synthesijer.ast.statement.SwitchStatement.Elem;
import synthesijer.ast.statement.SynchronizedBlock;
import synthesijer.ast.statement.TryStatement;
import synthesijer.ast.statement.VariableDecl;
import synthesijer.ast.statement.WhileStatement;
import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.ComponentType;

public class MakeCallFlowVisitor implements SynthesijerAstVisitor, SynthesijerExprVisitor{

	@Override
	public void visitMethod(Method o) {
		o.getBody().accept(this);
	}

	@Override
	public void visitModule(Module o) {
		for(VariableDecl v: o.getVariableDecls()){
			v.accept(this);
		}
		for(Method m: o.getMethods()){
			m.accept(this);
		}
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
		System.out.println("FieldAccess::makeCallGraph");
		if(o.getSelected() instanceof Ident){
			String name = ((Ident)o.getSelected()).getSymbol();
			Variable v = o.getScope().search(name);
			String clazz = "";
			String inst = "";
			if(v != null){
				if(v.getType() instanceof ComponentType){
					clazz = ((ComponentType)v.getType()).getName();
					inst = name;
				}else if(v.getType() instanceof ArrayType){
					clazz = "(Array)";
					inst = name;
				}else{
					clazz = v.getType() + " *** Unexpected";
					inst = name;
				}
			}else{
				clazz = name;
				inst = "** static method **";
			}
			System.out.println("  class   :" + clazz);
			System.out.println("  instance:" + inst);
		}else{
			System.out.println(" ==>");
			o.getSelected().accept(this);
		}
		System.out.println("  method  :" + o.getIdent().getSymbol());
	}

	@Override
	public void visitIdent(Ident o) {
		// nothing to do 
	}

	@Override
	public void visitLitral(Literal o) {
		// nothing to do
	}

	@Override
	public void visitMethodInvocation(MethodInvocation o) {
		System.out.println("MethodInvocation::makeCallGraph");
		if(o.getMethod() instanceof Ident){
			System.out.println("  class   :" + o.getScope().getModule().getName());
			System.out.println("  instance:" + "this");
			System.out.println("  method  :" + ((Ident)o.getMethod()).getSymbol());
		}else{
			o.getMethod().accept(this);
		}
	}

	@Override
	public void visitNewArray(NewArray o) {
		for(Expr expr: o.getDimExpr()){
			expr.accept(this);
		}
	}

	@Override
	public void visitNewClassExpr(NewClassExpr o) {
		System.out.println("NewClassExpr::makeCallGraph");
		System.out.println(" class:" + o.getClassName());
		System.out.println(" method:" + "<init>");
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

	@Override
	public void visitCondExpr(CondExpr o) {
		o.getCond().accept(this);
		o.getTruePart().accept(this);
		o.getFalsePart().accept(this);
	}

	@Override
	public void visitBlockStatement(BlockStatement o) {
		for(Statement s: o.getStatements()){
			s.accept(this);
		}
	}

	@Override
	public void visitBreakStatement(BreakStatement o) {
		// nothing to do
	}

	@Override
	public void visitContinueStatement(ContinueStatement o) {
		// nothing to do
	}

	@Override
	public void visitExprStatement(ExprStatement o) {
		o.getExpr().accept(this);
	}

	@Override
	public void visitForStatement(ForStatement o) {
		for(Statement s: o.getInitializations()){
			s.accept(this);
		}
		o.getCondition().accept(this);
		for(Statement s: o.getUpdates()){
			s.accept(this);
		}
		o.getBody().accept(this);
	}

	@Override
	public void visitIfStatement(IfStatement o) {
		o.getCondition().accept(this);
		o.getThenPart().accept(this);
		if(o.getElsePart() != null) o.getElsePart().accept(this);
	}

	@Override
	public void visitReturnStatement(ReturnStatement o) {
		if(o.getExpr() != null) o.getExpr().accept(this);
	}

	@Override
	public void visitSkipStatement(SkipStatement o) {
		// nothing to do
	}

	@Override
	public void visitSwitchStatement(SwitchStatement o) {
		o.getSelector().accept(this);
		for(SwitchStatement.Elem elem: o.getElements()){
			elem.accept(this);
		}
	}

	@Override
	public void visitSwitchCaseElement(Elem o) {
		o.getPattern().accept(this);
		for(Statement s: o.getStatements()){
			s.accept(this);
		}
	}

	@Override
	public void visitSynchronizedBlock(SynchronizedBlock o) {
		// nothing to do
	}

	@Override
	public void visitTryStatement(TryStatement o) {
		o.getBody().accept(this);
	}

	@Override
	public void visitVariableDecl(VariableDecl o) {
		if(o.getInitExpr() != null) o.getInitExpr().accept(this);
	}

	@Override
	public void visitWhileStatement(WhileStatement o) {
		o.getCondition().accept(this);
		o.getBody().accept(this);
	}

}
