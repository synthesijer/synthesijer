package synthesijer.ast.statement;

import java.io.PrintWriter;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.hdl.HDLModule;
import synthesijer.model.State;
import synthesijer.model.StateMachine;

public class ExprStatement extends ExprContainStatement{
	
	private final Expr expr;
	
	public ExprStatement(Scope scope, Expr expr){
		super(scope);
		this.expr = expr;
	}
	
	public Expr getExpr(){
		return expr;
	}

	public void dumpAsXML(PrintWriter dest){
		dest.printf("<statement type=\"expr\">\n");
		expr.dumpAsXML(dest);
		dest.printf("</statement>\n");
	}

	public void makeCallGraph(){
		expr.makeCallGraph();
	}
	
	public State genStateMachine(StateMachine m, State dest, State terminal, State loopout, State loopCont){
		State s = m.newState("expr");
		s.setBody(this);
		s.addTransition(dest);
		return s;
	}

	public void generateHDL(HDLModule m) {
		
	}
}
