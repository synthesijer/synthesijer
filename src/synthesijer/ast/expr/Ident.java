package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Method;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;

public class Ident extends Expr{
	
	private String symbol;
	
	public Ident(Scope scope){
		super(scope);
	}
	
	public void setIdent(String value){
		symbol = value;
	}
	
	public String getSymbol(){
		return symbol;
	}
	
	public void accept(SynthesijerExprVisitor v){
		v.visitIdent(this);
	}

	public String toString(){
		return String.format("Ident::(%s)", symbol); 
	}
	
	@Override
	public boolean isConstant() {
		return false;
	}
	
	@Override
	public boolean isVariable() {
		return true;
	}
	
	public Type getType(){
		Variable v = getScope().search(symbol);
		if(v != null) return v.getType();
		Method m = getScope().getModule().searchMethod(symbol);
		if(m != null) return m.getType();
		return null;
	}
	
	@Override
	public Variable[] getSrcVariables(){
		Variable var = getScope().search(symbol);
		return new Variable[]{var};
	}
	
	@Override
	public Variable[] getDestVariables(){
		Variable var = getScope().search(symbol);
		return new Variable[]{var};
	}

	@Override
	public boolean hasMethodInvocation() {
		return false;
	}
}
