package synthesijer.scheduler.opt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Enumeration;

import synthesijer.SynthesijerUtils;
import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;

public class ControlFlowGraph{

	private ControlFlowGraphBB[] blocks;
	
	ControlFlowGraphBB root;
	ControlFlowDominatorTree dominatorTree;
	
	private String base;

	public ControlFlowGraph(SchedulerBoard board, String key){
		SchedulerSlot[] slots = board.getSlots();
		if(!(slots.length > 0)) return;
		base = board.getName();
		this.blocks = buildAll(slots);
		this.dominatorTree = new ControlFlowDominatorTree(this, key + "_" + board.getName());
		if(synthesijer.Options.INSTANCE.debug){
			dumpAsDot(base, key);
		}
	}

	public ControlFlowGraphBB[] getBasicBlocks(){
		return blocks;
	}

	public Optional<ControlFlowGraphBB> dominatorOf(ControlFlowGraphBB v){
		return dominatorTree.dominatorOf(v);
	}
	
	public ArrayList<ControlFlowGraphBB> dominanceFrontierOf(ControlFlowGraphBB v){
		return dominatorTree.dominanceFrontierOf(v);
	}

	public ArrayList<ControlFlowGraphBB> getChildren(ControlFlowGraphBB n){
		return dominatorTree.getChildren(n);
	}

	private void dumpAsDot(String name, String key){
		try (BufferedWriter out =
			 Files.newBufferedWriter(Paths.get(key + "_" + name + "_cfg.dot"), StandardCharsets.UTF_8)) {
			out.write("digraph{"); out.newLine();
			for(ControlFlowGraphBB bb: blocks) {
				out.write(bb.toString());
				out.newLine();
			}
			for(ControlFlowGraphBB bb: blocks) {
				for(ControlFlowGraphBB succ: bb.succ){
					out.write(bb.label + " -> " + succ.label + "[headport=n, tailport=s];");
					out.newLine();
				}
			}
			out.write("}"); out.newLine();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private ControlFlowGraphBB[] buildAll(SchedulerSlot[] slots){
		ArrayList<ControlFlowGraphNode> nodes = new ArrayList<>();
		for(SchedulerSlot s: slots){
			nodes.add(new ControlFlowGraphNode(s));
		}
		for(ControlFlowGraphNode n: nodes){
			genRelationship(n, nodes);
		}

		return genBasicBlocks(nodes);
	}

	private void genRelationship(ControlFlowGraphNode target, ArrayList<ControlFlowGraphNode> nodes){
		for(ControlFlowGraphNode n : nodes){
			for(int id : n.slot.getNextStep()){
				if(target.slot.getStepId() == id){
					target.pred.add(n);
					n.succ.add(target);
				}
			}
		}
	}

	int id = 0;
	private String id(){
		String s = "bb_" + base + "_" + id;
		id++;
		return s;
	}

	private ControlFlowGraphBB[] genBasicBlocks(ArrayList<ControlFlowGraphNode> nodes){
		ArrayList<ControlFlowGraphBB> list = new ArrayList<>();

		ControlFlowGraphNode n;

		ControlFlowGraphBB exit_bb;
		exit_bb = new ControlFlowGraphBB(id());
		list.add(exit_bb);
		n = nodes.get(0);
		if(n.isMethodExit() == false){
			SynthesijerUtils.error("expected METHOD_EXIT, but " + n);
		}
		n.bb = exit_bb;
		exit_bb.nodes.add(n);

		ControlFlowGraphBB entry_bb;
		entry_bb = new ControlFlowGraphBB(id());
		list.add(entry_bb);
		n = nodes.get(1);
		if(n.isMethodEntry() == false){
			SynthesijerUtils.error("expected METHOD_ENTRY, but " + n);
		}
		n.bb = entry_bb;
		entry_bb.nodes.add(n);

		if(nodes.size() > 2){
			ControlFlowGraphBB bb = new ControlFlowGraphBB(id());
			list.add(bb);
			entry_bb.succ.add(bb);
			bb.pred.add(entry_bb);
			n = nodes.get(2);
			genBasicBlocks(list, n, bb);
		}else{
			entry_bb.succ.add(exit_bb);
			exit_bb.pred.add(entry_bb);
		}
		this.root = entry_bb;
		return list.toArray(new ControlFlowGraphBB[]{});
	}

	private void genBasicBlocks(ArrayList<ControlFlowGraphBB> list,
								ControlFlowGraphNode node,
								ControlFlowGraphBB bb){
		if(node.bb != null){
			if(node.bb != bb || node.isBranchSlot()){
				bb.succ.add(node.bb);
				node.bb.pred.add(bb);
			}
			return;
		}

		// registration
		bb.nodes.add(node);
		node.bb = bb;

		if(node.succ.size() == 1 && node.succ.get(0).pred.size() < 2){
			// just trace the successor
			genBasicBlocks(list, node.succ.get(0), bb);
		}else{
			// make new BB
			for(ControlFlowGraphNode n : node.succ){
				if(n.bb != null){ // already registrated
					if(n.bb != bb || node.isBranchSlot()){
						n.bb.pred.add(bb);
						bb.succ.add(n.bb);
					}
				}else{
					ControlFlowGraphBB bb0 = new ControlFlowGraphBB(id());
					list.add(bb0);
					bb0.pred.add(bb);
					bb.succ.add(bb0);
					genBasicBlocks(list, n, bb0);
				}
			}
		}

	}

}

class ControlFlowGraphNode{

	final ArrayList<ControlFlowGraphNode> pred = new ArrayList<>();

	final ArrayList<ControlFlowGraphNode> succ = new ArrayList<>();

	final SchedulerSlot slot;

	ControlFlowGraphBB bb;

	public ControlFlowGraphNode(SchedulerSlot slot){
		this.slot = slot;
	}

	public boolean isMethodExit(){
		return slot.hasMethodExit();
	}
	
	public boolean isMethodEntry(){
		return slot.hasMethodEntry();
	}

	public boolean isBranchSlot() {
		return slot.hasBranchOp();
	}

	public boolean hasDefinitionOf(Operand v){
		return slot.hasDefinitionOf(v);
	}

}

class ControlFlowGraphBB{

	final ArrayList<ControlFlowGraphNode> nodes = new ArrayList<>();
	final String label;

	final ArrayList<ControlFlowGraphBB> succ = new ArrayList<>();
	final ArrayList<ControlFlowGraphBB> pred = new ArrayList<>();

	public ControlFlowGraphBB(String label){
		this.label = label;
	}

	public void addSucc(ControlFlowGraphBB bb){
		succ.add(bb);
		bb.pred.add(this);
	}

	public String toString(){
		String s = label;
		s += "[";
		s += "shape = box, ";
		s += "label = \"";
		s += label + ":\\l";
		String sep = "";
		for(ControlFlowGraphNode n: nodes){
			s += sep + n.slot.toInfo();
			sep = ",\\l";
		}
		s += "\\l";
		s += "\"";
		s += "];";
		return s;
	}

	public boolean hasDefinitionOf(Operand v){
		for(ControlFlowGraphNode n: nodes){
			if(n.hasDefinitionOf(v)){
				return true;
			}
		}
		return false;
	}

	public ArrayList<SchedulerItem> getItems(){
		ArrayList<SchedulerItem> items = new ArrayList<>();
		for(var n: nodes){
			for(var i: n.slot.getItems()){
				items.add(i);
			}
		}
		return items;
	}

	public int getPredIndex(ControlFlowGraphBB bb){
		for(int i = 0; i < pred.size(); i++){
			if(pred.get(i) == bb) return i;
		}
		return -1;
	}
	
}
