package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;

public class ParenExpr extends Expr{
	
	private Expr expr;
	
	public ParenExpr(Scope scope){
		super(scope);
	}

	public void setExpr(Expr expr){
		this.expr = expr;
	}
	
	public Expr getExpr(){
		return this.expr;
	}

	public void accept(SynthesijerExprVisitor v){
		v.visitParenExpr(this);
	}

	@Override
	public boolean isConstant() {
		return expr.isConstant();
	}
	
	@Override
	public boolean isVariable() {
		return expr.isVariable();
	}
	
	@Override
	public Type getType() {
		return expr.getType();
	}
	
	public String toString(){
		return "ParenExpr(" + expr + ")";
	}
	
	@Override
	public Variable[] getSrcVariables(){
		return expr.getSrcVariables();
	}
	
	@Override
	public Variable[] getDestVariables(){
		return expr.getDestVariables();
	}
}
