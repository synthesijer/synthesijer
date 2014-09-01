package synthesijer.ast.statement;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Variable;
import synthesijer.model.State;
import synthesijer.model.Statemachine;

public class ExprStatement extends ExprContainStatement{
	
	private final Expr expr;
	
	private State state;
	
	public ExprStatement(Scope scope, Expr expr){
		super(scope);
		this.expr = expr;
	}
	
	public Expr getExpr(){
		return expr;
	}
	
	public State genStateMachine(Statemachine m, State dest, State terminal, State loopout, State loopCont){
		State s = m.newState("expr");
		s.addBody(this);
		//System.out.println("Expr::genStateMachine:" + dest);
		s.addTransition(dest);
		state = s;
		return s;
	}
	
	public void setState(State s){
		this.state = s;
		s.addBody(this);
	}
	
	public State getState(){
		return state;
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitExprStatement(this);
	}
	
	public String toString(){
		return "ExprStatement:" + expr;
	}

	@Override
	public Variable[] getSrcVariables(){
		return getExpr().getSrcVariables();
	}
	
	@Override
	public Variable[] getDestVariables(){
		return getExpr().getDestVariables();
	}
		
}
