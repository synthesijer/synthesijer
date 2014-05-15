package synthesijer.ast.statement;

import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.model.State;
import synthesijer.model.Statemachine;

public class TryStatement extends Statement{
	
	private Statement body;
	
	public TryStatement(Scope scope){
		super(scope);
	}
	
	public void setBody(Statement s){
		this.body = s;
	}

	public Statement getBody(){
		return this.body;
	}
	
	public State genStateMachine(Statemachine m, State dest, State terminal, State loopout, State loopCont){
		return body.genStateMachine(m, dest, terminal, loopCont, loopCont);
	}
		
	public void accept(SynthesijerAstVisitor v){
		v.visitTryStatement(this);
	}

}
