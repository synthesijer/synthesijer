package synthesijer.hdl.tools;

import java.io.PrintWriter;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.sequencer.SequencerState;
import synthesijer.hdl.sequencer.StateTransitCondition;

public class HDLSequencerToDot {
	
	private final HDLModule module;
	private final PrintWriter dest;
	
	public HDLSequencerToDot(HDLModule m, PrintWriter dest){
		this.module = m;
		this.dest = dest;
	}
	
	public void generate(){
		for(HDLSequencer seq: module.getSequencers()){
			generate(seq);
		}
	}
	
	private void generate(HDLSequencer seq){
		String name = module.getName() + "_" + seq.getStateKey().getName();
		dest.printf("digraph " + name + "{\n");
		for(SequencerState s: seq.getStates()){
			generate_node(s);
		}
		for(SequencerState s: seq.getStates()){
			generate_edge(s);
		}
		dest.printf("}\n");
	}
	
	private String getLabel(SequencerState s){
		return s.getStateId().getValue();
	}
	
	private void generate_node(SequencerState s){
		dest.println(getLabel(s) + ";");
	}
	
	private void generate_edge(SequencerState s){
		for(StateTransitCondition t: s.getTransitions()){
			dest.printf("%s -> %s;\n", getLabel(s), getLabel(t.getDestState()));
		}
	}

}
