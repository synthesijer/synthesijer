package synthesijer.model;

import java.io.PrintWriter;
import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.statement.ExprContainStatement;

public class State {
	
	private final int id;
	private final String desc;
	private final StateMachine machine;
	private final boolean terminate;
	
	private ExprContainStatement body;
	
	ArrayList<Transition> transitions = new ArrayList<Transition>();
	
	State(StateMachine m, int id, String desc, boolean terminate){
		this.machine = m;
		this.id = id;
		this.desc = desc;
		this.terminate = terminate;
	}
	
	public void setBody(ExprContainStatement s){
		this.body = s;
	}
	
	public void addTransition(State s){
		transitions.add(new Transition(s, null, true));
	}
	
	public void addTransition(State s, Expr cond, boolean flag){
		transitions.add(new Transition(s, cond, flag));
	}

	public void addTransition(State s, Expr cond, Expr pat){
		transitions.add(new Transition(s, cond, pat));
	}
	
	public String getId(){
		return String.format("%s_%04d", getBase(), id);
	}
	
	public String getBase(){
		return machine.getKey();
	}
	
	public boolean isTerminate(){
		return terminate;
	}
	
	public void dumpAsDot(PrintWriter dest){
		dest.printf("%s [label=\"%s\"];\n", getId(), getId() + "\\n" + desc);
		for(Transition t: transitions){
			if(t.getDestination() != null) dest.printf("%s -> %s;\n", getId(), t.getDestination().getId());
		}
	}
	
	public String toString(){
		return String.format("State: id=%d, desc=%s, machine=%s", id, desc, machine.getKey());
	}

}
