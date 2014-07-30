package synthesijer.model;

import synthesijer.ast.Expr;

public class Transition {
	
	private final State destination; 
	private final Expr condition;
	private final Expr pattern;
	private final boolean flag;
	
	public Transition(State dest, Expr expr, boolean flag){
		this(dest, expr, flag, null);
	}

	public Transition(State dest, Expr expr, Expr pat){
		this(dest, expr, false, pat);
	}

	public Transition(State dest, Expr expr, boolean flag, Expr pat){
		this.destination = dest;
		this.condition = expr;
		this.flag = flag;
		this.pattern = pat;
	}
	
	public State getDestination(){
		return destination;
	}
	
	public Expr getCondition(){
		return condition;
	}
	
	public boolean hasCondition(){
		return (condition != null && pattern != null); 
	}
	
	public Expr getPattern(){
		return pattern;
	}
	
	public boolean getFlag(){
		return flag;
	}

	public String toString(){
		return String.format("Transion: dest=%s, condition=%s, flag=%s, pattern=%s", destination, condition, flag, pattern);
	}

}
