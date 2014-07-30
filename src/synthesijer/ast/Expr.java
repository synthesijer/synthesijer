package synthesijer.ast;

import synthesijer.ast.expr.SynthesijerExprVisitor;


public abstract class Expr{
	
	public enum TERM {LEFT, RIGHT};
	
	private final Scope scope;
	
	public Expr(Scope scope){
		this.scope = scope;
	}
	
	public Scope getScope(){
		return scope;
	}
	
	abstract public void accept(SynthesijerExprVisitor v);
	
	abstract public boolean isConstant();
	
	abstract public boolean isVariable();
	
	abstract public Type getType();
	
	abstract public Variable[] getSrcVariables();
	
	abstract public Variable[] getDestVariables();
	
	abstract public boolean hasMethodInvocation();
}
