package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;

public class TypeCast extends Expr{
	
	private Expr expr;
	
	public TypeCast(Scope scope){
		super(scope);
	}
	
	public void setExpr(Expr expr){
		this.expr = expr;
	}

	public Expr getExpr(){
		return expr;
	}

	public void accept(SynthesijerExprVisitor v){
		v.visitTypeCast(this);
	}

	@Override
	public boolean isConstant() {
		return expr.isConstant();
	}
	
	@Override
	public boolean isVariable() {
		return expr.isVariable();
	}
	
	public Type getType(){
		return expr.getType();
	}

}
