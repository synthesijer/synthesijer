package synthesijer.scheduler.opt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Enumeration;
import java.util.HashMap;

import synthesijer.SynthesijerUtils;
import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;

public class DataFlowGraph{

	final DataFlowNode root = new DataFlowNode(null);
	
	public DataFlowGraph(ControlFlowGraphBB bb){
		if(bb.getItems().size() == 0) return;
		
		HashMap<SchedulerSlot, Boolean> trace = new HashMap<>();
		HashMap<Operand, DataFlowNode> cur = new HashMap<>();
		for(SchedulerItem item: bb.getItems()){
			SchedulerSlot slot = item.getSlot();
			if(trace.containsKey(slot)) continue; // skip
			trace.put(slot, true);
			buildNode(cur, slot);
		}
	}

	private DataFlowNode buildNode(HashMap<Operand, DataFlowNode> cur, SchedulerSlot slot){
		DataFlowNode n = new DataFlowNode(slot);
		for(Operand src: slot.getSrcOperands()){
			if(cur.get(src) != null){ // update in some previous slot
				cur.get(src).addChild(n); // to avoid RAW hazard
			}
		}
		for(Operand dst: slot.getDestOperands()){
			if(cur.get(dst) != null){
				if(cur.get(dst) != n){ // remove chained or packed slots
					cur.get(dst).addChild(n); // to avoid WAW hazard
				}
			}
			cur.put(dst, n); // set this node as the last updater
		}
		if(n.parents.size() == 0){
			this.root.addChild(n);
		}
		return n;
	}

	public boolean doneAll(HashMap<DataFlowNode, Boolean> trace, DataFlowNode node){
		for(DataFlowNode n: node.parents){
			if(trace.containsKey(n) == false)
				return false;
		}
		return true;
	}

	public void dump(){
		HashMap<DataFlowNode, Boolean> trace = new HashMap<>();
		trace.put(root, true);
		dump(trace, root, 0);
	}
	
	private void dump(HashMap<DataFlowNode, Boolean> trace, DataFlowNode node, int level){
		for(DataFlowNode n: node.children){
			if(doneAll(trace, n)){
				trace.put(n, true);
				for(SchedulerItem item : n.slot.getItems()){
					System.out.println(sep(level) + item.info());
				}
			}
		}
		for(DataFlowNode n: node.children){
			if(doneAll(trace, n)){
				dump(trace, n, level+2);
			}
		}
	}
	
	private String sep(int level){
		String s = "";
		for(int i = 0; i < level; i++) s += " ";
		return s;
	}

}

class DataFlowNode{

	SchedulerSlot slot;
	ArrayList<DataFlowNode> children = new ArrayList<>();
	ArrayList<DataFlowNode> parents = new ArrayList<>();

	public DataFlowNode(SchedulerSlot slot){
		this.slot = slot;
	}

	public void addChild(DataFlowNode n){
		children.add(n);
		n.parents.add(this);
	}

}
