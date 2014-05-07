package synthesijer.ast;

import java.io.PrintWriter;

import synthesijer.hdl.HDLExpr;

public abstract class Expr implements SynthesijerAstTree{
	
	public enum TERM {LEFT, RIGHT};
	
	private final Scope scope;
	
	public Expr(Scope scope){
		this.scope = scope;
	}
	
	public Scope getScope(){
		return scope;
	}
	
	abstract public void makeCallGraph();
	
	abstract public HDLExpr getHDLExprResult();
	
}
