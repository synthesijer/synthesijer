package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLModule;

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

	public HDLExpr getHDLExprResult(HDLModule m){
		return expr.getHDLExprResult(m);
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitTypeCast(this);
	}

}
