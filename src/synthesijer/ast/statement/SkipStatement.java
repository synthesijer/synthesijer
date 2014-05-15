package synthesijer.ast.statement;

import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.model.State;
import synthesijer.model.Statemachine;

public class SkipStatement extends Statement{
	
	public SkipStatement(Scope scope){
		super(scope);
	}

	public State genStateMachine(Statemachine m, State dest, State terminal, State loopout, State loopCont){
		return dest;
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitSkipStatement(this);
	}
	
}
