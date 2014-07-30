package synthesijer.ast.statement;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Variable;
import synthesijer.model.State;
import synthesijer.model.Statemachine;

public class ReturnStatement extends ExprContainStatement{
	
	private Expr expr;
	
	public ReturnStatement(Scope scope){
		super(scope);
	}
	
	public Expr getExpr(){
		return expr;
	}
	
	public void setExpr(Expr expr){
		this.expr = expr;
	}

	private State state;
	
	public State genStateMachine(Statemachine m, State dest, State terminal, State loopout, State loopCont){
		state = m.newState("return");
		state.addBody(this);
		//System.out.println("Return::genStateMachine:" + dest);
		state.addTransition(terminal);
		return state;
	}
	
	public void setState(State s){
		this.state = s;
	}
	
	public State getState(){
		return state;
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitReturnStatement(this);
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
