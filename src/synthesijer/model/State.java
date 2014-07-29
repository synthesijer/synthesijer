package synthesijer.model;

import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.statement.ExprContainStatement;

public class State {
	
	private final int id;
	private final String desc;
	private final Statemachine machine;
	private final boolean terminate;
	
	private ExprContainStatement body;
	
	private ArrayList<Transition> transitions = new ArrayList<>();
	private ArrayList<State> predecesors = new ArrayList<>();
	
	State(Statemachine m, int id, String desc, boolean terminate){
		this.machine = m;
		this.id = id;
		this.desc = desc;
		this.terminate = terminate;
	}
	
	public void setBody(ExprContainStatement s){
		this.body = s;
	}
	
	public ExprContainStatement getBody(){
		return body;
	}
	
	public void clearTransition(){
		transitions.clear();
	}
	
	private void addTransition(State s, Transition t){
		transitions.add(t);
		s.addPredecesors(this);
	}
	
	public void addTransition(State s){
		addTransition(s, new Transition(s, null, true));
	}
		
	public void addTransition(State s, Expr cond, boolean flag){
		addTransition(s, new Transition(s, cond, flag));
	}

	public void addTransition(State s, Expr cond, Expr pat){
		addTransition(s, new Transition(s, cond, pat));
	}
	
	public void addPredecesors(State s){
		predecesors.add(s);
	}

	public Transition[] getTransitions(){
		return transitions.toArray(new Transition[0]);
	}
	
	public State[] getPredecesors(){
		return predecesors.toArray(new State[0]);
	}
	
	public String getId(){
		return String.format("%s_%04d", getBase(), id);
	}

	public String getDescription(){
		return desc;
	}

	public String getBase(){
		return machine.getKey();
	}
	
	public boolean isTerminate(){
		return terminate;
	}
	
	public void accept(StatemachineVisitor v){
		v.visitState(this);
	}
		
	public String toString(){
		return String.format("State: id=%d, desc=%s, machine=%s", id, desc, machine.getKey());
	}

}
