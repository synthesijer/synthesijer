package synthesijer.ast.statement;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Variable;

public class ExprStatement extends ExprContainStatement{

	private final Expr expr;

	public ExprStatement(Scope scope, Expr expr){
		super(scope);
		this.expr = expr;
	}

	public Expr getExpr(){
		return expr;
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitExprStatement(this);
	}

	public String toString(){
		return "ExprStatement:" + expr;
	}

	@Override
	public Variable[] getSrcVariables(){
		return getExpr().getSrcVariables();
	}

	@Override
	public Variable[] getDestVariables(){
		return getExpr().getDestVariables();
	}

}
