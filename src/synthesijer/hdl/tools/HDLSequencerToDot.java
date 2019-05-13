package synthesijer.hdl.tools;

import java.io.PrintWriter;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.sequencer.SequencerState;
import synthesijer.hdl.sequencer.StateTransitCondition;

public class HDLSequencerToDot {

	private final HDLModule module;

	public HDLSequencerToDot(HDLModule m){
		this.module = m;
	}

	public void generate(PrintWriter dest){
		for(HDLSequencer seq: module.getSequencers()){
			generate(dest, seq);
		}
	}

	private void generate(PrintWriter dest, HDLSequencer seq){
		String name = module.getName() + "_" + seq.getStateKey().getName();
		dest.printf("digraph " + name + "{\n");
		for(SequencerState s: seq.getStates()){
			generate_node(dest, s);
		}
		for(SequencerState s: seq.getStates()){
			generate_edge(dest, s);
		}
		dest.printf("}\n");
	}

	private String getLabel(SequencerState s){
		return s.getStateId().getValue();
	}

	private String getCondNode(StateTransitCondition t){
		return "\"" + t.toLabel() + "\"";
	}

	private void generate_node(PrintWriter dest, SequencerState s){
//		dest.println(getLabel(s) + ";");
	}

	private void generate_edge(PrintWriter dest, SequencerState s){
		for(StateTransitCondition t: s.getTransitions()){
			if(t.hasCondition() == false){
				dest.printf("%s -> %s;\n", getLabel(s), getLabel(t.getDestState()));
			}else{
				dest.printf("%s -> %s [label = %s];\n", getLabel(s), getLabel(t.getDestState()), getCondNode(t));
			}
		}
	}

}
