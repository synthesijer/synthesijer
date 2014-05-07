package synthesijer.ast;

import java.io.PrintWriter;

import synthesijer.hdl.HDLModule;
import synthesijer.model.State;
import synthesijer.model.StateMachine;


public abstract class Statement implements SynthsijerAstTree{
	
	private final Scope scope; 
	
	public Statement(Scope scope){
		this.scope = scope;
	}
	
	public Scope getScope(){
		return scope;
	}
	
	abstract public void dumpAsXML(PrintWriter dest);
	
	abstract public void makeCallGraph();
	
	abstract public State genStateMachine(StateMachine m, State dest, State funcOut, State loopOut, State loopCont);
	
	abstract public void generateHDL(HDLModule m);

}
