package synthesijer.ast.statement;

import java.io.PrintWriter;

import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.hdl.HDLModule;
import synthesijer.model.State;
import synthesijer.model.StateMachine;

public class TryStatement extends Statement{
	
	private Statement body;
	
	public TryStatement(Scope scope){
		super(scope);
	}
	
	public void setBody(Statement s){
		this.body = s;
	}

	public void makeCallGraph(){
		body.makeCallGraph();
	}
	
	public State genStateMachine(StateMachine m, State dest, State terminal, State loopout, State loopCont){
		return body.genStateMachine(m, dest, terminal, loopCont, loopCont);
	}
	
	public void dumpAsXML(PrintWriter dest){
		dest.printf("<statement type=\"try\">");
		body.dumpAsXML(dest);
		dest.printf("</statement>");
	}

	@Override
	public void generateHDL(HDLModule m) {
		body.generateHDL(m);
	}
	
}
