package synthesijer.ast.statement;

import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLModule;
import synthesijer.model.State;
import synthesijer.model.Statemachine;

public class BreakStatement extends Statement{
	
	public BreakStatement(Scope scope){
		super(scope);
	}
	
	public State genStateMachine(Statemachine m, State dest, State terminal, State loopout, State loopCont){
		State s = m.newState("break");
		//System.out.println("Break::genStateMachine:" + loopout);
		s.addTransition(loopout);
		return s;
	}
	
	public void generateHDL(HDLModule m) {
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitBreakStatement(this);
	}
}
