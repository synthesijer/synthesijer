package synthesijer.model;

import java.util.ArrayList;

import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.SynthesijerMethodVisitor;
import synthesijer.ast.SynthesijerModuleVisitor;


//// TODO めそっどれべるじゃなくてBasicBlock単位で処理できるようにする
public class StatemachineOptimizer implements SynthesijerModuleVisitor, SynthesijerMethodVisitor{

	private final Module module;
	
	public StatemachineOptimizer(Module m) {
		this.module  = m;
	}
	
	@Override
	public void visitMethod(Method o) {
		System.out.println("== " + o.getName());
		GenDataFlowGraphVisitor v = new GenDataFlowGraphVisitor();
		o.getStateMachine().accept(v);
		DataFlowGraph dfg = v.getDataFlowGraph();
		State pred = null;
		State state = null;
		while((state = schedule(dfg)) != null){
			state.clearTransition();
			if(pred != null){
				pred.addTransition(state);
			}else{
				o.getStateMachine().getEntryState().clearTransition();
				o.getStateMachine().getEntryState().addTransition(state);
			}
			pred = state;
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
	
	public State schedule(DataFlowGraph dfg){
		System.out.println("-- schedule");
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
			System.out.println(n.state + ":" + n.stmt);
			n.setScheduled();
			if(s == null){
				s = n.state;
			}else{
				n.stmt.setState(s);
			}
		}
		if(fire.size() == 0 && rest.size() > 0){
			System.out.println("// last");
			for(DataFlowNode n: rest){
				System.out.println(n.state + ":" + n.stmt);
				n.setScheduled();
				if(s == null){
					s = n.state;
				}else{
					n.stmt.setState(s);
				}
			}
		}
		System.out.println("--");
		return s;
	}

}
