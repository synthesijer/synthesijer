package synthesijer.ast.statement;

import java.io.PrintWriter;

import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.hdl.HDLModule;
import synthesijer.model.State;
import synthesijer.model.StateMachine;

public class SkipStatement extends Statement{
	
	public SkipStatement(Scope scope){
		super(scope);
	}

	public void makeCallGraph(){
	}

	public void dumpAsXML(PrintWriter dest){
		dest.printf("<statement type=\"skip\"/>\n");
	}
	
	public State genStateMachine(StateMachine m, State dest, State terminal, State loopout, State loopCont){
		return dest;
	}

	@Override
	public void generateHDL(HDLModule m) {
	}
}
