package synthesijer.model;

import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.ast.statement.ExprContainStatement;

public class GenBasicStatemachineBlockVisitor implements StatemachineVisitor{
	
	private final ArrayList<BasicBlock> list;
	private final BasicBlock bb;
	private final Hashtable<State, Boolean> sentinel;
	
	private GenBasicStatemachineBlockVisitor(ArrayList<BasicBlock> list, Hashtable<State, Boolean> sentinel) {
		this.list = list;
		this.sentinel = sentinel;
		this.bb = new BasicBlock();
		list.add(bb);
	}

	public GenBasicStatemachineBlockVisitor() {
		this(new ArrayList<BasicBlock>(), new Hashtable<State, Boolean>());
	}
	
	public BasicBlock getBasicBlock(){
		return bb;
	}
	
	public BasicBlock[] getBasicBlockList(){
		return list.toArray(new BasicBlock[0]);
	}
	
	private BasicBlock stepIn(State s){
		GenBasicStatemachineBlockVisitor v = new GenBasicStatemachineBlockVisitor(list, sentinel); 
		s.accept(v);
		//if(v.bb.getSize() > 0) list.add(v.bb);
		return v.getBasicBlock();
	}
	
	private BasicBlock newBB(){
		GenBasicStatemachineBlockVisitor v = new GenBasicStatemachineBlockVisitor(list, sentinel); 
		return v.getBasicBlock();
	}

	@Override
	public void visitStatemachine(Statemachine o) {
		State s = o.getEntryState();
		s.accept(this);
	}
	
	private boolean hasMethodInvocation(State state){
		if(state.getBodies() == null) return false;
		for(ExprContainStatement s: state.getBodies()){
			if(s.hasMethodInvocation()) return true;
		}
		return false;
	}
	
	private boolean isSameBasicBlock(State s){
//		return s.getTransitions().length == 1 && s.getPredecesors().length <= 1;
		if(hasMethodInvocation(s)) return false;
		if(s.getPredecesors().length > 1) return false;
		if(s.getTransitions().length > 1) return false;
		
		if(s.getTransitions() != null){
			for(Transition t: s.getTransitions()){
				if(t.hasCondition()) return false;
			}
		}
		if(s.getTransitions().length == 1) return true;
		return false;
	}

	
	@Override
	public void visitState(State o) {
		if(sentinel.containsKey(o)) return;
		sentinel.put(o, true);
		if(isSameBasicBlock(o)){ // o.getTransitions().length == 1 && o.getPredecesors().length <= 1){
			bb.addState(o);
			State s = o.getTransitions()[0].getDestination();
			if(s != null){
				s.accept(this);
			}
		}else{
			BasicBlock b = null;
			/*
			if(o.getBody() != null && o.getBody().hasMethodInvocation()){ // special treating for method invocation
				b = newBB();
			}else if(o.getPredecesors().length <= 1){ // just fork
				b = bb;
			}else{ // fork & join
				b = newBB();
			}
			*/
			b = newBB(); // naive implementation
			b.addState(o);
			for(Transition t: o.getTransitions()){
				State s = t.getDestination();
				if(s != null){
					b.addNextBlock(stepIn(s));
				}
			}
		}
	}
	

}
