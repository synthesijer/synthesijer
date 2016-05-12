package synthesijer.ast.statement;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Variable;

public class ReturnStatement extends ExprContainStatement{
	
	private Expr expr;
	
	public ReturnStatement(Scope scope){
		super(scope);
	}
	
	public Expr getExpr(){
		return expr;
	}
	
	public void setExpr(Expr expr){
		this.expr = expr;
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitReturnStatement(this);
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
