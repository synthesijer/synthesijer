package synthesijer.model;

import java.util.ArrayList;

import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.SynthesijerMethodVisitor;
import synthesijer.ast.SynthesijerModuleVisitor;

public class BasicBlockStatemachineOptimizer implements SynthesijerModuleVisitor, SynthesijerMethodVisitor{

	private final Module module;
	
	private static final boolean BB_DEBUG = false; 
	
	public BasicBlockStatemachineOptimizer(Module m) {
		this.module  = m;
	}
	
	@Override
	public void visitMethod(Method o) {
		if(BB_DEBUG) System.out.println("== " + o.getName());
		GenBasicStatemachineBlockVisitor v = new GenBasicStatemachineBlockVisitor();
		o.getStateMachine().accept(v);
		if(BB_DEBUG){
			for(BasicBlock bb: v.getBasicBlockList()){
				System.out.println("--------------");
				bb.printAll();
				System.out.println(" entry=" + bb.getEntryState());
				System.out.println(" exit=" + bb.getExitState());
			}
		}
		
		for(BasicBlock bb: v.getBasicBlockList()){
			if(bb.getSize() == 0) continue;
			DataFlowGraph dfg = bb.getDataFlowGraph();
			State pred = null;
			State state = null;
			Transition[] exit_transaction = bb.getExitState().getTransitions();
			while((state = schedule(dfg)) != null){
				if(pred != null){
					pred.clearTransition();
					pred.addTransition(state);
				}
				pred = state;
			}
			if(pred != null){
				pred.clearTransition();
				pred.setTransition(exit_transaction);
			}
		}
	}

	@Override
	public void visitModule(Module o) {
		for(Method m: o.getMethods()){
			m.accept(this);
		}
	}

	public void optimize(){
		module.accept(this);
	}
	
	private void update(DataFlowNode node, State s){
		node.setScheduled();
		if(node.stmt != null && node.stmt.length > 0){
			node.stmt[0].setState(s);
			s.addBody(node.stmt[0]);
		}
	}
	
	public State schedule(DataFlowGraph dfg){
		if(BB_DEBUG) System.out.println("-- schedule");
		ArrayList<DataFlowNode> fire = new ArrayList<>();
		ArrayList<DataFlowNode> rest = new ArrayList<>();
		for(DataFlowNode node: dfg.getNodes()){
			if(node.isScheduled() == false){
				if(node.isReady()){
					fire.add(node);
				}else{
					rest.add(node);
				}
			}
		}
		State s = null;
		for(DataFlowNode n: fire){
			if(BB_DEBUG) System.out.println(n.state + ":" + n.stmt);
			if(s == null){
				s = n.state;
			}else{
				s.getStateMachine().rmState(n.state);
			}
			update(n, s);
		}
		if(fire.size() == 0 && rest.size() > 0){
			if(BB_DEBUG) System.out.println("// last");
			for(DataFlowNode n: rest){
				if(BB_DEBUG) System.out.println(n.state + ":" + n.stmt);
				if(s == null){
					s = n.state;
				}else{
					s.getStateMachine().rmState(n.state);
				}
				update(n, s);
			}
		}
		if(BB_DEBUG) System.out.println("--");
		return s;
	}

}
 