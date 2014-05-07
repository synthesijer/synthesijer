package synthesijer.ast.statement;

import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLModule;
import synthesijer.model.State;
import synthesijer.model.StateMachine;

public class BreakStatement extends Statement{
	
	public BreakStatement(Scope scope){
		super(scope);
	}
	
	public State genStateMachine(StateMachine m, State dest, State terminal, State loopout, State loopCont){
		State s = m.newState("break");
		s.addTransition(loopout);
		return s;
	}
	
	public void generateHDL(HDLModule m) {
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitBreakStatement(this);
	}
}
