package synthesijer.ast;

import synthesijer.model.State;
import synthesijer.model.Statemachine;


public abstract class Statement implements SynthesijerAstTree{
	
	private final Scope scope; 
	
	public Statement(Scope scope){
		this.scope = scope;
	}
	
	public Scope getScope(){
		return scope;
	}
	
	abstract public State genStateMachine(Statemachine m, State dest, State funcOut, State loopOut, State loopCont);

}
