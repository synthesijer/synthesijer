package synthesijer.ast.statement;

import java.io.PrintWriter;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.hdl.HDLModule;
import synthesijer.model.State;
import synthesijer.model.StateMachine;

public class IfStatement extends Statement{
	
	private Expr condition;
	private Statement thenPart, elsePart;
	
	public IfStatement(Scope scope){
		super(scope);
	}
	
	public void setCondition(Expr expr){
		condition = expr;
	}
	
	public void setThenPart(Statement s){
		thenPart = s;
	}
	
	public void setElsePart(Statement s){
		elsePart = s;
	}

	public void makeCallGraph(){
		condition.makeCallGraph();
		thenPart.makeCallGraph();
		if(elsePart != null) elsePart.makeCallGraph();
	}
	
	public State genStateMachine(StateMachine m, State dest, State terminal, State loopout, State loopCont){
		State t = thenPart.genStateMachine(m, dest, terminal, loopout, loopCont);
		State s = m.newState("if_cond");
		s.addTransition(t, condition, true);
		if(elsePart != null){
			State e =  elsePart.genStateMachine(m, dest, terminal, loopout, loopCont);
			e.addTransition(t, condition, false);
		}
		return s;
	}
	
	public void dumpAsXML(PrintWriter dest){
		dest.printf("<statement type=\"if\">\n");
		dest.printf("<condition>\n");
		condition.dumpAsXML(dest);
		dest.printf("</condition>\n");
		dest.printf("<then>\n");
		thenPart.dumpAsXML(dest);
		dest.printf("</then>\n");
		if(elsePart != null){
			dest.printf("<else>\n");
			elsePart.dumpAsXML(dest);
			dest.printf("</else>\n");
		}
		dest.printf("</statement>\n");
	}

	public void generateHDL(HDLModule m) {
		thenPart.generateHDL(m);
		if(elsePart != null) elsePart.generateHDL(m);
	}

}
