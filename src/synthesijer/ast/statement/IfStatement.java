package synthesijer.ast.statement;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLModule;
import synthesijer.model.State;
import synthesijer.model.Statemachine;

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

	public Expr getCondition(){
		return condition;
	}
	
	public Statement getThenPart(){
		return thenPart;
	}
	
	public Statement getElsePart(){
		return elsePart;
	}

	public State genStateMachine(Statemachine m, State dest, State terminal, State loopout, State loopCont){
		State t = thenPart.genStateMachine(m, dest, terminal, loopout, loopCont);
		State s = m.newState("if_cond");
		s.addTransition(t, condition, true);
		if(elsePart != null){
			State e =  elsePart.genStateMachine(m, dest, terminal, loopout, loopCont);
			e.addTransition(t, condition, false);
		}
		return s;
	}
	
	public void accept(SynthesijerAstVisitor v){
		v.visitIfStatement(this);
	}


}
