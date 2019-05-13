package synthesijer.scheduler.opt;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;

public class ControlFlowGraph{

	private ControlFlowGraphNode top;

	private ControlFlowGraphNode[] nodes;

	public ControlFlowGraph(SchedulerBoard board){
		SchedulerSlot[] slots = board.getSlots();
		if(!(slots.length > 0)) return;
		ArrayList<ControlFlowGraphNode> nodes = new ArrayList<>();
		for(SchedulerSlot s: slots){
			nodes.add(new ControlFlowGraphNode(s));
		}
		this.nodes = nodes.toArray(new ControlFlowGraphNode[]{});
		this.top = this.nodes[0];
		buildAll();
	}

	private void buildAll(){
		for(ControlFlowGraphNode n: nodes){
			build(n);
		}
	}

	private void build(ControlFlowGraphNode target){
		for(ControlFlowGraphNode n : nodes){
			for(int id : n.slot.getNextStep()){
				if(target.slot.getStepId() == id){
					target.pred.add(n);
					n.succ.add(target);
				}
			}
		}
	}

	public ControlFlowGraphBB[] getBasicBlocks(){
		ArrayList<ControlFlowGraphBB> list = new ArrayList<>();
		ControlFlowGraphBB bb = new ControlFlowGraphBB();
		Hashtable<ControlFlowGraphNode,Boolean> table = new Hashtable<>();
		list.add(bb);
		getBasicBlocks(list, table, top, bb);
		return list.toArray(new ControlFlowGraphBB[]{});
	}

	public void getBasicBlocks(ArrayList<ControlFlowGraphBB> list,
							   Hashtable<ControlFlowGraphNode, Boolean> table,
							   ControlFlowGraphNode node,
							   ControlFlowGraphBB bb){
		if(table.containsKey(node)){
			return; // already treated.
		}

		table.put(node, true); // make the node treated
		if(node.pred.size() == 1){
			bb.nodes.add(node);
			if(node.succ.size() == 1){
				getBasicBlocks(list, table, node.succ.get(0), bb);
			}else if(node.succ.size() > 1){
				for(int i = 0; i < node.succ.size(); i++){
					ControlFlowGraphBB bb0 = new ControlFlowGraphBB(); // new basic block
					list.add(bb0);
					getBasicBlocks(list, table, node.succ.get(i), bb0);
				}
			}
		}else{ // fork node
			ControlFlowGraphBB bb0 = (list.size() == 0) ? bb : new ControlFlowGraphBB();
			list.add(bb0);
			bb0.nodes.add(node);
			if(node.succ.size() == 1){
				getBasicBlocks(list, table, node.succ.get(0), bb0);
			}else if(node.succ.size() > 1){
				for(int i = 0; i < node.succ.size(); i++){
					ControlFlowGraphBB bb1 = new ControlFlowGraphBB(); // new basic block
					list.add(bb1);
					getBasicBlocks(list, table, node.succ.get(i), bb1);
				}
			}
		}
	}


}

class ControlFlowGraphNode{

	final ArrayList<ControlFlowGraphNode> pred = new ArrayList<>();

	final ArrayList<ControlFlowGraphNode> succ = new ArrayList<>();

	final SchedulerSlot slot;

	public ControlFlowGraphNode(SchedulerSlot slot){
		this.slot = slot;
	}

}

class ControlFlowGraphBB{

	ArrayList<ControlFlowGraphNode> nodes = new ArrayList<>();

	public ControlFlowGraphBB(){
	}
}
