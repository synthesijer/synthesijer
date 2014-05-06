package synthesijer.ast.statement;

import java.io.PrintWriter;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.hdl.HDLModule;
import synthesijer.model.State;
import synthesijer.model.StateMachine;

public class WhileStatement extends Statement{
	
	private Expr condition;
	private Statement body;
	
	public WhileStatement(Scope scope){
		super(scope);
	}
	
	public void setCondition(Expr expr){
		this.condition = expr;
	}
	
	public void setBody(Statement body){
		this.body = body;
	}

	public void makeCallGraph(){
		condition.makeCallGraph();
		body.makeCallGraph();
	}

	public State genStateMachine(StateMachine m, State dest, State terminal, State loopout, State loopCont){
		State s = m.newState("while_cond");
		State d = body.genStateMachine(m, s, terminal, dest, s);
		s.addTransition(d, condition, true);
		s.addTransition(dest, condition, false); // exit from this loop
		return s;
	}

	public void dumpAsXML(PrintWriter dest){
		dest.printf("<statement type=\"while\">\n");
		dest.printf("<condition>\n");
		condition.dumpAsXML(dest);
		dest.printf("</condition>\n");
		dest.printf("<body>\n");
		body.dumpAsXML(dest);
		dest.printf("</body>\n");
		dest.printf("</statement>\n");
	}

	@Override
	public void generateHDL(HDLModule m) {
		body.generateHDL(m);
	}
}
