package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLModule;

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
			
	public HDLExpr getHDLExprResult(HDLModule m){
		return expr.getHDLExprResult(m);
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitParenExpr(this);
	}

}
