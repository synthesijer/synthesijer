package synthesijer.ast.expr;

import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLExpr;

public class NewClassExpr extends Expr{
	
	private String clazz;
	private ArrayList<Expr> params = new ArrayList<Expr>();
	
	public NewClassExpr(Scope scope){
		super(scope);
	}
	
	public void setClassName(String str){
		clazz = str;
	}
	
	public void addParam(Expr expr){
		params.add(expr);
	}

	public String getClassName(){
		return clazz;
	}
	
	public ArrayList<Expr> getParameters(){
		return params;
	}

	public HDLExpr getHDLExprResult(){
		return null;
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitNewClassExpr(this);
	}
}
