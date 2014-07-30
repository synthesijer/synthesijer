package synthesijer.model;

import java.util.ArrayList;

import synthesijer.ast.Variable;
import synthesijer.ast.statement.ExprContainStatement;

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
	
	private void conectEdge(DataFlowNode pred, DataFlowNode succ, ExprContainStatement pred_expr, ExprContainStatement succ_expr){
		for(Variable v0: succ_expr.getSrcVariables()){
			for(Variable v1: pred_expr.getDestVariables()){
				if(v0 == v1){
					succ.addPred(pred);
					pred.addSucc(succ);
					if(DUMP) System.out.println("**con** " + pred.stmt + " => " + succ.stmt);
				}
			}
		}
	}

	private void connectEdge(DataFlowNode pred, DataFlowNode succ){
		if(succ.stmt == null || pred.stmt == null) return;
		for(ExprContainStatement succ_expr: succ.stmt){
			for(ExprContainStatement pred_expr: pred.stmt){
				conectEdge(pred, succ, pred_expr, succ_expr);
			}
		}
	}
	
	// append as predecessors for all
	public void toPredeccessor(DataFlowNode node){
		for(DataFlowNode n: nodes){
			connectEdge(node, n);
		}
	}

	// append as successors for all
	public void toSuccessor(DataFlowNode node){
		for(DataFlowNode n: nodes){
			connectEdge(n, node);
		}
	}

}
