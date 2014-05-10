package synthesijer.model;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLSequencer;

public class StateMachine {
	
	private ArrayList<State> stateList = new ArrayList<State>();
	
	private int stateIdCounter;
	private final String base;
	
	public StateMachine(String base){
		this.base = base;
		stateIdCounter = 1;
	}
	
	public String getKey(){
		return "S_" + base;
	}
	
	public State newState(String desc){
		State s = new State(this, stateIdCounter, desc, false);
		stateIdCounter++;
		stateList.add(s);
		return s;
	}

	public State newState(String desc, boolean flag){
		State s = new State(this, stateIdCounter, desc, flag);
		stateIdCounter++;
		stateList.add(s);
		return s;
	}

	public void dumpAsDot(PrintWriter dest){
		for(State s: stateList){
			s.dumpAsDot(dest);
		}
	}

	public HDLSequencer genHDLSequencer(HDLModule hm){
		HDLSequencer hs = hm.newSequencer(getKey());
		Hashtable<State, HDLSequencer.SequencerState> map = new Hashtable<State, HDLSequencer.SequencerState>();
		for(State s: stateList){
			map.put(s, hs.addSequencerState(s.getId()));
		}
		for(State s: stateList){
			HDLSequencer.SequencerState ss = map.get(s);
			for(Transition c: s.transitions){
				ss.addStateTransit(map.get(c.getDestination()), null, null, null, null);
			}
			if(s.isTerminate()){
				ss.addStateTransit(hs.getIdleState(), null, null, null, null);
			}
		}
		return hs;
	}


}
