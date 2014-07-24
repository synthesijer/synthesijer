package synthesijer.model;

import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.SynthesijerMethodVisitor;
import synthesijer.ast.SynthesijerModuleVisitor;

public class SimpleASAPStateCombineVisitor implements SynthesijerModuleVisitor, SynthesijerMethodVisitor, StatemachineVisitor{

	@Override
	public void visitMethod(Method o) {
		o.getStateMachine().accept(this);
	}

	@Override
	public void visitModule(Module o) {
		for(Method m: o.getMethods()){
			m.accept(this);
		}
	}

	@Override
	public void visitStatemachine(Statemachine o) {
		for(State s: o.getStates()){
			s.accept(this);
		}
	}

	@Override
	public void visitState(State o) {
		for(Transition t: o.getTransitions()){
			if(t.getDestination() != null){
				System.out.println(t);
			}
		}
	}

}
