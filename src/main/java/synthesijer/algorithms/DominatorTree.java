package synthesijer.algorithms;

import java.util.ArrayList;
import java.util.Optional;

/**
 * DominatorTree
 *
 * T.Lengauer and R.E.Tarjan, "A fast algorithm for finding dominators in a flowgraph"
 * https://dl.acm.org/citation.cfm?id=357071
 *
 * cf. http://fileadmin.cs.lth.se/cs/education/eda230/f2.pdf
 *
 */
public class DominatorTree<T>{

	ArrayList<DominatorTreeNode<T>> V = new ArrayList<>();
	DominatorTreeNode<T> r;

	/** depth-first search number */
	int df = 0;
	
	public DominatorTree(DominatorTreeNode<T> r){
		this.r = r;
		buildTree(r);
		buildDominanceFrontier(r);
	}

	public int getDfsNum(DominatorTreeNode<T> v){
		return v.dfsId;
	}

	public Optional<DominatorTreeNode<T>> dominatorOf(DominatorTreeNode<T> v){
		return Optional.ofNullable(v.idom);
	}
      
	public ArrayList<DominatorTreeNode<T>> dominanceFrontierOf(DominatorTreeNode<T> v){
		return v.df;
	}

	private void buildTree(DominatorTreeNode<T> r){
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

		for(int i = 1; i < n; i++){
			DominatorTreeNode<T> w = V.get(i);
			w.idom.children.add(w);
		}
	}

	private void buildDominanceFrontier(DominatorTreeNode<T> x){
		// depth first
		for(DominatorTreeNode<T> c: x.children){
			buildDominanceFrontier(c);
		}
		if(x == this.r) return;
		// after building Dominance-Frontier of all children
		x.df.clear();
		for(DominatorTreeNode<T> y : x.succ){
			if(y.idom != x){
				x.df.add(y);
			}
		}
		for(DominatorTreeNode<T> z : x.children){
			for(DominatorTreeNode<T> y : z.df){
                if(y.idom != x){
					x.df.add(y);
				}
			}
		}
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
