package synthesijer.ast;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLModule;

public abstract class Expr implements SynthesijerAstTree{
	
	public enum TERM {LEFT, RIGHT};
	
	private final Scope scope;
	
	public Expr(Scope scope){
		this.scope = scope;
	}
	
	public Scope getScope(){
		return scope;
	}
	
	abstract public HDLExpr getHDLExprResult(HDLModule m);
	
}
