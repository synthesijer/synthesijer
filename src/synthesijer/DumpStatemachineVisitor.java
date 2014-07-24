package synthesijer;

import java.io.PrintWriter;

import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.SynthesijerMethodVisitor;
import synthesijer.ast.SynthesijerModuleVisitor;
import synthesijer.model.State;
import synthesijer.model.Statemachine;
import synthesijer.model.StatemachineVisitor;
import synthesijer.model.Transition;

public class DumpStatemachineVisitor implements SynthesijerModuleVisitor, SynthesijerMethodVisitor, StatemachineVisitor{
	
	private PrintWriter dest;
	
	public DumpStatemachineVisitor(PrintWriter dest){
		this.dest = dest;
	}

	@Override
	public void visitMethod(Method o) {
		o.getStateMachine().accept(this);
	}

	@Override
	public void visitModule(Module o) {
		dest.printf("digraph " + o.getName() + "{\n");
		for(Method m: o.getMethods()){
			m.accept(this);
		}
		dest.printf("}\n");
	}

	@Override
	public void visitStatemachine(Statemachine o) {
		for(State s: o.getStates()){
			s.accept(this);
		}
	}

	@Override
	public void visitState(State o) {
		dest.printf("%s [label=\"%s\"];\n", o.getId(), o.getId() + "\\n" + o.getDescription());
		for(Transition t: o.getTransitions()){
			if(t.getDestination() != null) dest.printf("%s -> %s;\n", o.getId(), t.getDestination().getId());
		}
	}

}
