package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;

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
	
}
