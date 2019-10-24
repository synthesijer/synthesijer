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

	private DominatorTreeNode<ControlFlowGraphBB> root;
	private ArrayList<DominatorTreeNode<ControlFlowGraphBB>> nodes = new ArrayList<>();
	private DominatorTreeGenerator<ControlFlowGraphBB> dtg;
	private Hashtable<ControlFlowGraphBB, DominatorTreeNode<ControlFlowGraphBB>> T = new Hashtable<>();
		
	public DominatorTree(ControlFlowGraph cfg, String base){
		for(ControlFlowGraphBB bb: cfg.getBasicBlocks()){
			var n = new DominatorTreeNode<ControlFlowGraphBB>(bb, bb.label);
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
		this.dtg = new DominatorTreeGenerator<ControlFlowGraphBB>(root);
		if(synthesijer.Options.INSTANCE.debug){
			dumpAsDot(base);
		}
	}

	public ControlFlowGraphBB dominatorOf(ControlFlowGraphBB v){
		return dtg.dominatorOf(T.get(v)).get();
	}

	private void dumpAsDot(String key){
		try (BufferedWriter out =
			 Files.newBufferedWriter(Paths.get(key + "_dom.dot"), StandardCharsets.UTF_8)) {
			out.write("digraph{"); out.newLine();
			for(DominatorTreeNode<ControlFlowGraphBB> n: nodes) {
				out.write(n.label);
				out.newLine();
			}
			for(DominatorTreeNode<ControlFlowGraphBB> n: nodes) {
				if(n.idom != null){
					out.write(n.idom.label + " -> " + n.label + ";");
					out.newLine();
				}
			}
			out.write("}"); out.newLine();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}

class DominatorTreeGenerator<T>{

	ArrayList<DominatorTreeNode<T>> V = new ArrayList<>();
	DominatorTreeNode<T> r;

	/** depth-first search number */
	int df = 0;
	
	public DominatorTreeGenerator(DominatorTreeNode<T> r){
		this.r = r;
		df = 0;
		
		// Step 1.
		traceDfs(r);
		
		int n = V.size();
		
		for(int i = n - 1; i > 0; i--){
			// Step 2.
			DominatorTreeNode<T> w = V.get(i);
			for(DominatorTreeNode<T> v : w.pred){
				DominatorTreeNode<T> u = eval(v);
				if(u.sdom.dfsId < w.sdom.dfsId){
					w.sdom = u.sdom;
				}
			}
			w.sdom.bucket.add(w);
          
			link(w.parent, w);
          
			// Step 3.
			for(DominatorTreeNode<T> v: w.parent.bucket){
				//w.parent.bucket.remove(v);
				DominatorTreeNode<T> u = eval(v);
				if(u.sdom.dfsId < v.sdom.dfsId){
					v.idom = u;
				}else{
					v.idom = w.parent;
				}
			}
		}

		for(int i = 1; i < n; i++){
			DominatorTreeNode<T> w = V.get(i);
			if(w.idom != w.sdom){
				w.idom = w.idom.idom;
			}
		}

		r.idom = null;
	}

	public int getDfsNum(DominatorTreeNode<T> v){
		return v.dfsId;
	}

	public DominatorTreeNode<T> dominatorOf(DominatorTreeNode<T> v){
		return v.idom;
	}
      
	private void traceDfs(DominatorTreeNode<T> v){
        v.dfsId = df;
        V.add(df, v);
        v.sdom = v;
        v.ancestor = null;
        df = df + 1;
        
        for(DominatorTreeNode<T> w: v.succ){
			if(w.sdom == null){
				w.parent = v;
				traceDfs(w);
			}
		}
	}

	private DominatorTreeNode<T> eval(DominatorTreeNode<T> v){
        DominatorTreeNode<T> u;
        // Find ancestor with least sdom
        u = v;
		while(v.ancestor != null){
			if(v.sdom.dfsId < u.sdom.dfsId){
				u = v;
			}
			v = v.ancestor;
		}
        return u;
	}

	private void link(DominatorTreeNode<T> v, DominatorTreeNode<T> w){
        w.ancestor = v;
	}

}

class DominatorTreeNode<T>{

	final ArrayList<DominatorTreeNode<T>> pred = new ArrayList<>();
	final ArrayList<DominatorTreeNode<T>> succ = new ArrayList<>();

	final T node;

	final String label;

	int dfsId = -1;
	
	DominatorTreeNode<T> idom = null;
	DominatorTreeNode<T> sdom = null;
	DominatorTreeNode<T> parent = null;
	DominatorTreeNode<T> ancestor = null;
	ArrayList<DominatorTreeNode<T>> bucket = new ArrayList<>();
		
	public DominatorTreeNode(T node, String label){
		this.node = node;
		this.label = label;
	}

	public void addSucc(DominatorTreeNode<T> obj){
		this.succ.add(obj);
		obj.pred.add(this);
	}

	public T get(){
		return node;
	}
		
}
