package synthesijer.scheduler.opt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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

import javax.management.RuntimeErrorException;

public class ControlFlowGraph{

	private ControlFlowGraphBB[] blocks;

	private String base;

	public ControlFlowGraph(SchedulerBoard board, String key){
		SchedulerSlot[] slots = board.getSlots();
		if(!(slots.length > 0)) return;
		base = board.getName();
		this.blocks = buildAll(slots);
		if(synthesijer.Options.INSTANCE.debug){
			dumpAsDot(board.getName(), key);
		}
	}

	public ControlFlowGraphBB[] getBasicBlocks(){
		return blocks;
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
					out.write(bb.label + " -> " + succ.label + ";");
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
		Hashtable<ControlFlowGraphNode,Boolean> table = new Hashtable<>();

		ControlFlowGraphBB bb = new ControlFlowGraphBB(id());
		list.add(bb);

		ControlFlowGraphNode n = nodes.get(0);
		genBasicBlocks(list, table, n, bb);
		return list.toArray(new ControlFlowGraphBB[]{});
	}

	private void genBasicBlocks(ArrayList<ControlFlowGraphBB> list,
								Hashtable<ControlFlowGraphNode, Boolean> table,
								ControlFlowGraphNode node,
								ControlFlowGraphBB bb){
		if(table.get(node) != null){
			if(node.bb != bb){
				bb.succ.add(node.bb);
				node.bb.pred.add(bb);
			}
			return;
		}

		// registration
		table.put(node, true);
		bb.nodes.add(node);
		node.bb = bb;

		if(node.succ.size() == 1 && node.succ.get(0).pred.size() < 2){
			// just trace the successor
			genBasicBlocks(list, table, node.succ.get(0), bb);
		}else{
			// make new BB
			for(ControlFlowGraphNode n : node.succ){
				if(table.get(n) != null){ // already registrated
					if(n.bb != bb){
						n.bb.pred.add(bb);
						bb.succ.add(n.bb);
					}
				}else{
					ControlFlowGraphBB bb0 = new ControlFlowGraphBB(id());
					list.add(bb0);
					bb0.pred.add(bb);
					bb.succ.add(bb0);
					genBasicBlocks(list, table, n, bb0);
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
}
