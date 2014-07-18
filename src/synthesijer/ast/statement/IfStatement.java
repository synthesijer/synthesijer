package synthesijer.ast.statement;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.model.State;
import synthesijer.model.Statemachine;

public class IfStatement extends Statement{
	
	private Expr condition;
	private BlockStatement thenPart, elsePart;
	
	public IfStatement(Scope scope){
		super(scope);
	}
	
	public void setCondition(Expr expr){
		condition = expr;
	}
	
	public void setThenPart(BlockStatement s){
		thenPart = s;
	}
	
	public void setElsePart(BlockStatement s){
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
			s.addTransition(e, condition, false);
		}else{
			s.addTransition(dest, condition, false);
		}
		return s;
	}
	
	public void accept(SynthesijerAstVisitor v){
		v.visitIfStatement(this);
	}


}
