package synthesijer.hdl.tools;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.sequencer.SequencerState;

public class ResourceUsageTable {

	private final HDLModule module;
	private ArrayList<HDLSignal> signals = new ArrayList<>();
	private Hashtable<SequencerState, ArrayList<HDLSignal>> writeTable = new Hashtable<>();
	private Hashtable<SequencerState, ArrayList<HDLSignal>> readTable = new Hashtable<>();
	private Hashtable<HDLSignal, Integer> index = new Hashtable<>();

	public ResourceUsageTable(HDLModule m){
		this.module = m;
		generate();
	}

	private void addEntry(HDLSignal s, int i){
		index.put(s, i);
		signals.add(s);
		for(HDLSignal.AssignmentCondition a: s.getConditions()){
			addWriteEntry(a.getSequencerState(), s);
			if(a.getValue() instanceof HDLSignal){
				addReadEntry(a.getSequencerState(), (HDLSignal)a.getValue());
			}
			addReadEntries(a.getSequencerState(), a.getValue().getSrcSignals());
		}
	}

	private void addWriteEntry(SequencerState state, HDLSignal sig){
		ArrayList<HDLSignal> l = writeTable.get(state);
		if(l == null) l = new ArrayList<>();
		l.add(sig);
		writeTable.put(state, l);
	}

	private void addReadEntries(SequencerState state, HDLSignal[] sig){
		if(sig == null) return;
		for(HDLSignal s: sig){
			addReadEntry(state, s);
		}
	}

	private void addReadEntry(SequencerState state, HDLSignal s){
		ArrayList<HDLSignal> l = readTable.get(state);
		if(l == null) l = new ArrayList<>();
		l.add(s);
		readTable.put(state, l);
	}

	private void generate(){
		int i = 0;
		for(HDLSignal s: module.getSignals()){
			addEntry(s, i++);
		}

		for(HDLPort p: module.getPorts()){
			addEntry(p.getSignal(), i++);
		}
	}

	public void generate(PrintWriter dest){
		dest.println("<html>");
		dest.println("<head>");
		dest.printf("<title>%s</title>", module.getName());
		dest.println("</head>");
		dest.println("<body>");
		dest.println("<table border=\"1\">");
		generate_signals(dest);
		generate_entries(dest);
		dest.println("</table>");
		dest.println("</body>");
		dest.println("</html>");
	}

	private void generate_signals(PrintWriter dest){
		dest.println("<tr>");
		dest.print("<th></th>");
		for(HDLSignal s: signals){
			dest.printf("<th>%s</th>", s.getName());
		}
		dest.println();
		dest.println("</tr>");
	}

	private void generate_entries(PrintWriter dest){

		for(HDLSequencer seq: module.getSequencers()){
			for(SequencerState s: seq.getStates()){
				dest.println("<tr>");
				generate_entry(dest, s);
				dest.println("</tr>");
			}
		}
	}

	private boolean[] getBitVector(ArrayList<HDLSignal> list){
		boolean[] v = new boolean[signals.size()];
		for(int i = 0; i < v.length; i++) v[i] = false;
		if(list == null) return v;
		for(HDLSignal sig: list){
			int id = index.get(sig).intValue();
			v[id] = true;
		}
		return v;
	}

	private void generate_entry(PrintWriter dest, SequencerState s){
		boolean[] writeVec, readVec;
		writeVec = getBitVector(writeTable.get(s));
		readVec = getBitVector(readTable.get(s));
		//System.out.println(s.getStateId().getValue());
		dest.printf("<td>%s</td>", s.getStateId().getValue());
		for(int i = 0; i < signals.size(); i++){
			String label = "";
			if(writeVec[i]) label = "***";
			if(readVec[i]) label = "+++";
			dest.printf("<td>%s</td>", label);
		}
		dest.println();
	}

}
