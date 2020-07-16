package synthesijer.scheduler.opt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
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
import synthesijer.ast.type.*;

public class DataFlowGraph{

	final DataFlowNode root = new DataFlowNode(null);
	
	public DataFlowGraph(ControlFlowGraphBB bb){
		if(bb.getItems().size() == 0) return;
		
		HashMap<Operand, DataFlowNode> cur = new HashMap<>();
		HashMap<SchedulerSlot, DataFlowNode> map = new HashMap<>();
		for(SchedulerSlot slot: bb.getSlots()){
			buildNode(cur, slot, map);
		}
		SchedulerSlot exitSlot = bb.getLastNode().slot;
		DataFlowNode exitNode = map.get(exitSlot);
		for(SchedulerSlot slot: bb.getSlots()){
			if(slot != exitSlot){
				map.get(slot).addChild(exitNode);
			}
		}
	}

	private DataFlowNode buildNode(HashMap<Operand, DataFlowNode> cur, SchedulerSlot slot, HashMap<SchedulerSlot, DataFlowNode> map){
		DataFlowNode n = new DataFlowNode(slot);
		map.put(slot, n);
		for(Operand src: slot.getSrcOperands()){
			if(cur.get(src) != null){ // update in some previous slot
				cur.get(src).addChild(n); // to avoid RAW hazard
			}
			if(src.getType() instanceof ArrayType){
				cur.put(src, n); // set this node as the last updater
			}
		}
		for(Operand dst: slot.getDestOperands()){
			if(cur.get(dst) != null){
				if(cur.get(dst).slot != n.slot){ // remove chained or packed slots
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

	public boolean doneAll(HashMap<DataFlowNode, String> trace, DataFlowNode node){
		for(DataFlowNode n: node.parents){
			if(trace.containsKey(n) == false)
				return false;
		}
		return true;
	}

	private int id = 0;
	public void dumpDot(PrintStream out, int base){
		HashMap<DataFlowNode, String> trace = new HashMap<>();
		String label = "N_"+base+"_"+(id++);
		out.printf("%s [shape = box, label =\"%s\"];\n", label, label);
		trace.put(root, label);
		dumpDot(out, trace, root, base);
	}
	
	private void dumpDot(PrintStream out, HashMap<DataFlowNode, String> trace, DataFlowNode node, int base){
		for(DataFlowNode n: node.children){
			if(trace.get(n) == null && doneAll(trace, n)){
				String label = "N_" + base + "_" + (id++);
				String s = "";
				for(SchedulerItem item : n.slot.getItems()){
					s += item.info() + "\\l";
				}
				out.printf("%s [shape = box, label =\"%s\"];", label, s);
				out.println();
				for(DataFlowNode parent: n.parents){
					out.printf("%s -> %s;\n", trace.get(parent), label);
				}
				trace.put(n, label);
			}
		}
		for(DataFlowNode n: node.children){
			if(doneAll(trace, n)){
				dumpDot(out, trace, n, base);
			}
		}
	}

}

class DataFlowNode{

	SchedulerSlot slot;
	HashSet<DataFlowNode> children = new HashSet<>();
	HashSet<DataFlowNode> parents = new HashSet<>();

	public DataFlowNode(SchedulerSlot slot){
		this.slot = slot;
	}

	public void addChild(DataFlowNode n){
		this.children.add(n);
		n.parents.add(this);
	}

}
