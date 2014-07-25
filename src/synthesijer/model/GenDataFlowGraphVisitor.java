package synthesijer.model;

import synthesijer.ast.Expr;
import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.SynthesijerMethodVisitor;
import synthesijer.ast.SynthesijerModuleVisitor;
import synthesijer.ast.Variable;

public class GenDataFlowGraphVisitor implements SynthesijerModuleVisitor, SynthesijerMethodVisitor, StatemachineVisitor{

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
		System.out.println(o);
		if(o.getBody() != null){
			Expr expr = o.getBody().getExpr();
			System.out.println("::" + expr);
			for(Variable v: expr.getSrcVariables()){
				System.out.println("   <- " + v);
			}
			for(Variable v: expr.getDestVariables()){
				System.out.println("   -> " + v);
			}
		}
		for(Transition t: o.getTransitions()){
			System.out.println("=>" + t);
		}
	}

}
