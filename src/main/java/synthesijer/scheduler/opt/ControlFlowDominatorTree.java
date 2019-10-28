package synthesijer.scheduler.opt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Optional;

import synthesijer.SynthesijerUtils;
import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;

import synthesijer.algorithms.DominatorTree;
import synthesijer.algorithms.DominatorTreeNode;

public class ControlFlowDominatorTree{

	private String base;

	private ControlFlowGraph cfg;
	private DominatorTreeNode<ControlFlowGraphBB> root;
	private ArrayList<DominatorTreeNode<ControlFlowGraphBB>> nodes = new ArrayList<>();
	private DominatorTree<ControlFlowGraphBB> dt;
	private Hashtable<ControlFlowGraphBB, DominatorTreeNode<ControlFlowGraphBB>> T = new Hashtable<>();
		
	public ControlFlowDominatorTree(ControlFlowGraph cfg, String base){
		this.cfg = cfg;
		for(ControlFlowGraphBB bb: cfg.getBasicBlocks()){
			var n = new DominatorTreeNode<ControlFlowGraphBB>(bb);
			nodes.add(n);
			T.put(bb, n);
			if(bb == cfg.root){
				root = n;
			}
		}
		for(ControlFlowGraphBB bb: cfg.getBasicBlocks()){
			var n = T.get(bb);
			for(ControlFlowGraphBB succ: bb.succ){
				n.addSucc(T.get(succ));
			}
		}
		this.dt = new DominatorTree<ControlFlowGraphBB>(root);
		if(synthesijer.Options.INSTANCE.debug){
			dumpAsDot(base);
		}
	}

	public Optional<ControlFlowGraphBB> dominatorOf(ControlFlowGraphBB v){
		Optional<DominatorTreeNode<ControlFlowGraphBB>> n = dt.dominatorOf(T.get(v));
		if(n.isEmpty()) return Optional.empty();
		return Optional.of(n.get().get());
	}

	public ArrayList<ControlFlowGraphBB> dominanceFrontierOf(ControlFlowGraphBB v){
		ArrayList<ControlFlowGraphBB> ret = new ArrayList<>();
		var n = T.get(v);
		if(n == null){
			return ret;
		}
		var l = dt.dominanceFrontierOf(n);
		for(var i: l){
			ret.add(i.get());
		}
		return ret;
	}

	public ArrayList<ControlFlowGraphBB> getChildren(ControlFlowGraphBB v){
		ArrayList<ControlFlowGraphBB> ret = new ArrayList<>();
		var n = T.get(v);
		if(n == null){
			return ret;
		}
		var l = dt.getChildren(n);
		for(var i: l){
			ret.add(i.get());
		}
		return ret;
	}

	private void dumpAsDot(String key){
		try (BufferedWriter out =
			 Files.newBufferedWriter(Paths.get(key + "_dom.dot"), StandardCharsets.UTF_8)) {
			out.write("digraph{"); out.newLine();
			for(ControlFlowGraphBB n: cfg.getBasicBlocks()) {
				out.write(n.label);
				out.newLine();
			}
			for(ControlFlowGraphBB n: cfg.getBasicBlocks()) {
				if(dominatorOf(n).isEmpty() == false){
					out.write(dominatorOf(n).get().label + " -> " + n.label + ";");
					out.newLine();
				}
			}
			out.write("}"); out.newLine();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
