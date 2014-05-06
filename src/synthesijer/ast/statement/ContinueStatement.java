package synthesijer.ast.statement;

import java.io.PrintWriter;

import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.hdl.HDLModule;
import synthesijer.model.State;
import synthesijer.model.StateMachine;

public class ContinueStatement extends Statement{
	
	public ContinueStatement(Scope parent){
		super(parent);
	}

	public void dumpAsXML(PrintWriter dest){
		dest.printf("<statement type=\"continue\"/>\n");
	}

	public void makeCallGraph(){
	}

	// @TODO
	// This is not illegal.
	public State genStateMachine(StateMachine m, State dest, State terminal, State loopout, State loopCont){
		State s = m.newState("continue");
		s.addTransition(loopCont);
		return s;
	}

	public void generateHDL(HDLModule m) {
	}

}
