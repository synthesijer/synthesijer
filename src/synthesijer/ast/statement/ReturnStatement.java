package synthesijer.ast.statement;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLSignal;
import synthesijer.model.State;
import synthesijer.model.StateMachine;

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
	
	public State genStateMachine(StateMachine m, State dest, State terminal, State loopout, State loopCont){
		state = m.newState("return");
		state.setBody(this);
		state.addTransition(terminal);
		return state;
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitReturnStatement(this);
	}
	
}
