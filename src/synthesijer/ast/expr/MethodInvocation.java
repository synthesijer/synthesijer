package synthesijer.ast.expr;

import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLIdent;

public class MethodInvocation extends Expr{
	
	private Expr method;
	private ArrayList<Expr> params = new ArrayList<Expr>();
	
	public MethodInvocation(Scope scope){
		super(scope);
	}
	
	public void setMethod(Expr expr){
		method = expr;
	}
	
	public void addParameter(Expr expr){
		params.add(expr);		
	}
	
	public Expr getMethod(){
		return method;
	}
	
	public ArrayList<Expr> getParameters(){
		return params;
	}
	
	public String getMethodName(){
		if(method instanceof Ident){
			return ((Ident)method).getSymbol();
		}else if(method instanceof FieldAccess){
			return ((FieldAccess)method).getIdent().getSymbol();
		}
		return method.toString();
	}

	public void makeCallGraph(){
		System.out.println("MethodInvocation::makeCallGraph");
		if(method instanceof Ident){
			System.out.println("  class   :" + getScope().getModule().getName());
			System.out.println("  instance:" + "this");
			System.out.println("  method  :" + ((Ident)method).getSymbol());
		}else{
			method.makeCallGraph();
		}
	}
	
	public HDLExpr getHDLExprResult(){
		return new HDLIdent(getMethodName() + "_return_value");
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitMethodInvocation(this);
	}

}
