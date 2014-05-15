package synthesijer.model;

import java.util.ArrayList;

public class Statemachine {
	
	private ArrayList<State> states = new ArrayList<State>();
	
	private int stateIdCounter;
	private final String base;
	
	public Statemachine(String base){
		this.base = base;
		stateIdCounter = 1;
	}
	
	public String getKey(){
		return "S_" + base;
	}
	
	public State newState(String desc){
		State s = new State(this, stateIdCounter, desc, false);
		stateIdCounter++;
		states.add(s);
		return s;
	}
	
	public State[] getStates(){
		return states.toArray(new State[]{});
	}
	
	public void accept(StatemachineVisitor v){
		v.visitStatemachine(this);
	}

	public State newState(String desc, boolean flag){
		State s = new State(this, stateIdCounter, desc, flag);
		stateIdCounter++;
		states.add(s);
		return s;
	}

}
