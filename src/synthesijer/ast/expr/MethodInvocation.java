package synthesijer.ast.expr;

import java.util.ArrayList;

import synthesijer.Manager;
import synthesijer.ast.Expr;
import synthesijer.ast.Method;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;
import synthesijer.ast.type.ComponentType;

public class MethodInvocation extends Expr{
	
	private Expr method;
	private ArrayList<Expr> params = new ArrayList<>();
	
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
	
	public void setParameter(int index, Expr expr){
		params.set(index, expr);
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
	
	public String toString(){
		String str = "MethodInvocation::(" + method;
		for(Expr expr: params){
			str += " " + expr; 
		}
		str += ")";
		return str; 
	}
	
	@Override
	public boolean isVariable() {
		return false;
	}
	
	public Method getTargetMethod(){
		if(method instanceof FieldAccess){
			ComponentType type = (ComponentType)(method.getType());
			return Manager.INSTANCE.searchModule(type.getName()).searchMethod(getMethodName());
		}else{
			return getScope().getModule().searchMethod(getMethodName());
		}
	}

	@Override
	public Type getType() {
		if(method instanceof Ident){ // local method
			return method.getType();
		}else{
			ComponentType type = (ComponentType)(method.getType());
			Method m = Manager.INSTANCE.searchModule(type.getName()).searchMethod(getMethodName());
			return m.getType();
		}
	}
	
	@Override
	public Variable[] getSrcVariables(){
		ArrayList<Variable> list = new ArrayList<>();
		for(Variable var: method.getSrcVariables()) list.add(var);
		for(Expr expr: params){
			for(Variable var: expr.getSrcVariables()) list.add(var);
		}
		return list.toArray(new Variable[]{});
	}

	@Override
	public Variable[] getDestVariables(){
		ArrayList<Variable> list = new ArrayList<>();
		for(Variable var: method.getSrcVariables()) list.add(var);
		return list.toArray(new Variable[]{});
	}
}
