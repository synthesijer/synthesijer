package synthesijer.ast.expr;

import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;

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
	
	public void accept(SynthesijerExprVisitor v){
		v.visitMethodInvocation(this);
	}

	@Override
	public boolean isConstant() {
		return false;
	}
}
