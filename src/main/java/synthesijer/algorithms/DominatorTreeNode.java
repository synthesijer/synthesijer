package synthesijer.algorithms;

import java.util.ArrayList;

public class DominatorTreeNode<T>{

	final ArrayList<DominatorTreeNode<T>> pred = new ArrayList<>();
	final ArrayList<DominatorTreeNode<T>> succ = new ArrayList<>();

	final T node;

	int dfsId = -1;
	
	DominatorTreeNode<T> idom = null;
	DominatorTreeNode<T> sdom = null;
	DominatorTreeNode<T> parent = null;
	DominatorTreeNode<T> ancestor = null;
	ArrayList<DominatorTreeNode<T>> bucket = new ArrayList<>();
		
	public DominatorTreeNode(T node){
		this.node = node;
	}

	public void addSucc(DominatorTreeNode<T> obj){
		this.succ.add(obj);
		obj.pred.add(this);
	}

	public T get(){
		return node;
	}
		
}
