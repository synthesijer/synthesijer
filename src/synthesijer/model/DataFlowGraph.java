package synthesijer.model;

import java.util.ArrayList;

import synthesijer.ast.Variable;

public class DataFlowGraph {
	
	private ArrayList<DataFlowNode> nodes = new ArrayList<>();
	
	private static final boolean DUMP = false;
	
	public DataFlowNode[] getNodes(){
		return nodes.toArray(new DataFlowNode[0]);
	}
	
	public void addNode(DataFlowNode node){
		nodes.add(node);
	}
	
	public void addNodes(DataFlowGraph g){
		nodes.addAll(g.nodes);
	}
	
	public DataFlowNode contains(State s){
		for(DataFlowNode n: nodes){
			if(s == n.state) return n;
		}
		return null;
	}

	private void connectEdge(DataFlowNode pred, DataFlowNode succ){
		if(succ.stmt == null || pred.stmt == null) return;
		for(Variable v0: succ.stmt.getSrcVariables()){
			for(Variable v1: pred.stmt.getDestVariables()){
				if(v0 == v1){
					succ.addPred(pred);
					pred.addSucc(succ);
					if(DUMP) System.out.println("**con** " + pred.stmt + " => " + succ.stmt);
				}
			}
		}
	}
	
	public void apply(DataFlowNode node){
		for(DataFlowNode n: nodes){
			connectEdge(node, n);
		}
	}
	
}
