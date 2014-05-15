package synthesijer.model;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLSequencer;

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

	public HDLSequencer genHDLSequencer(HDLModule hm){
		HDLSequencer hs = hm.newSequencer(getKey());
		Hashtable<State, HDLSequencer.SequencerState> map = new Hashtable<State, HDLSequencer.SequencerState>();
		for(State s: states){
			map.put(s, hs.addSequencerState(s.getId()));
		}
		for(State s: states){
			HDLSequencer.SequencerState ss = map.get(s);
			for(Transition c: s.transitions){
				ss.addStateTransit(map.get(c.getDestination()));
			}
			if(s.isTerminate()){
				ss.addStateTransit(hs.getIdleState());
			}
		}
		return hs;
	}


}
