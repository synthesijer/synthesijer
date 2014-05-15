package synthesijer.ast.statement;

import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLModule;
import synthesijer.model.State;
import synthesijer.model.Statemachine;

public class ContinueStatement extends Statement{
	
	public ContinueStatement(Scope parent){
		super(parent);
	}

	// @TODO
	public State genStateMachine(Statemachine m, State dest, State terminal, State loopout, State loopCont){
		State s = m.newState("continue");
		s.addTransition(loopCont);
		return s;
	}

	public void generateHDL(HDLModule m) {
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitContinueStatement(this);
	}
}
