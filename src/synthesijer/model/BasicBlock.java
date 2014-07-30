package synthesijer.model;


import java.util.ArrayList;

public class BasicBlock {

	private final ArrayList<State> states = new ArrayList<>();
	private final ArrayList<BasicBlock> successors = new ArrayList<>();
	private final DataFlowGraph dfg = new DataFlowGraph();
	
	public void addState(State s){
		states.add(s);
		DataFlowNode n = new DataFlowNode(s, s.getBody());
		dfg.toSuccessor(n);
		dfg.addNode(n);
	}
	
	public void addNextBlock(BasicBlock b){
		successors.add(b);
	}
	
	public DataFlowGraph getDataFlowGraph(){
		return dfg;
	}
	
	public void printAll(){
		for(State s: states){
			System.out.println(s + ":" + s.getBody());
		}
	}
	
	public int getSize(){
		return states.size();
	}
	
	public State getEntryState(){
		if(states.size() > 0)
			return states.get(0);
		else
			return null;
	}
	
	public State getExitState(){
		if(states.size() > 0)
			return states.get(states.size()-1);
		else
			return null;
	}
	
}
