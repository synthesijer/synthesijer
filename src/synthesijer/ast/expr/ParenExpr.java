package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;

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
		return false;
	}
}
