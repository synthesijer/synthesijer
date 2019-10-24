package synthesijer.scheduler.opt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import synthesijer.SynthesijerUtils;
import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;

public class DominatorTree{

	private String base;

	DominatorTreeNode root;
	DominatorTreeNode[] nodes;

	public DominatorTree(ControlFlowGraph cfg, String base){
		this.base = base;
		this.root = new DominatorTreeNode(cfg.root);
		this.nodes = genDominatorTree(root);
		if(synthesijer.Options.INSTANCE.debug){
			dumpAsDot(base);
		}
	}

	private DominatorTreeNode[] genDominatorTree(DominatorTreeNode root){
		ArrayList<DominatorTreeNode> nodes = new ArrayList<>();
		Hashtable<ControlFlowGraphBB, Boolean> table = new Hashtable<>();
		nodes.add(root);
		genDominatorTree(root, nodes, table);
		return nodes.toArray(new DominatorTreeNode[]{});
	}

	private void genDominatorTree(DominatorTreeNode n, ArrayList<DominatorTreeNode> nodes, Hashtable<ControlFlowGraphBB, Boolean> table){
		return;
	}

	private void dumpAsDot(String key){
		try (BufferedWriter out =
			 Files.newBufferedWriter(Paths.get(key + "_dom.dot"), StandardCharsets.UTF_8)) {
			out.write("digraph{"); out.newLine();
			for(DominatorTreeNode n: nodes) {
				out.write(n.label);
				out.newLine();
			}
			for(DominatorTreeNode n: nodes) {
				for(DominatorTreeNode succ: n.succ){
					out.write(n.label + " -> " + succ.label + ";");
					out.newLine();
				}
			}
			out.write("}"); out.newLine();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/*
	  int df // depth-first search number
      
	  procedure dfs(v, vertex[])
        dfnum(v) <- df
        vertex[df] <- v
        sdom(v) <- v
        ancestor(v) <- null
        df <- df + 1
        
        for each w in succ(v) do
          if(sdom(w) = null){
            parent(w) <- v
            dfs(w)
          }
      
      function eval(v)
        vertex u
        // Find ancestor with least sdom
        u <- v
        while(ancestor(v != nil) do
          if(dfnum(sdom(v)) < dfnum(sdom(u)))
            u <- v
          v <- ancestor(v)
        return u
      
      procedure link(v, w)
        ancestor(w) <- v
      
      procedure dominators(V, s)
        int i
        int n = |V|
        vertex vertex[n]
        
        // Step 1.
        for each w in V do
          sdom(w) <- nil
          bucket(w) <- {}
        
        df <- 0
        dfs(s)
        
        for(i <- n - 1; i > 0; i <- i - 1) do {
          // Step 2.
          w <- vertex[i]
          for each v in pred(w) do {
            u <- eval(v)
            if(dfnum(sdom(u)) < dfnum(sdom(w)))
              sdom(w) <- sdom(u)
          }
          add w to bucket(sdom(w))
          
          link(parent(w), w)
          
          // Step 3.
          for each v in bucket(parent(w)) do {
            remove v from bucket(parent(w))
            u <- eval(v)
            if(dfnum(sdom(u)) < dfnum(sdom(v)))
              idom(v) <- u
            else
              idom(v) <- prent(w)
          }
        }

        // Step 4.
        for(i <- 1; i < n; i <- i + 1){
          w <- vertex[i]
          if(idom(w) != sdom(w))
            idom(w) <- idom(idom(w))
        }
        
        idom(s) <- -1

	 */
	
}

class DominatorTreeNode{

	DominatorTreeNode pred = null;
	
	final ArrayList<DominatorTreeNode> succ = new ArrayList<>();

	final ControlFlowGraphBB node;

	final String label;
		
	public DominatorTreeNode(ControlFlowGraphBB node){
		this.node = node;
		this.label = node.label;
	}


}
