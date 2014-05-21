package synthesijer.ast.statement;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;
import synthesijer.model.State;
import synthesijer.model.Statemachine;

public class VariableDecl extends ExprContainStatement{
	
	private Variable var;
	private final Expr init;
	
	public VariableDecl(Scope scope, String name, Type type, Expr init){
		super(scope);
		this.init = init;
		var = new Variable(name, type, scope.getMethod());
	}
	
	public String getName(){
		return var.getName();
	}
	
	public Expr getExpr(){
		return init;
	}
	
	public Variable getVariable(){
		return var;
	}
	
	public Type getType(){
		return var.getType();
	}
	
	public boolean hasInitExpr(){
		return (init != null);
	}
	
	public Expr getInitExpr(){
		return init;
	}
	
	private State state;
	
	public State genStateMachine(Statemachine m, State dest, State terminal, State loopout, State loopCont){
		if(init != null){
			State s = m.newState("var_init");
			s.setBody(this);
			s.addTransition(dest);
			state = s;
		}else{
			state = dest;
		}
		return state;
	}
	
	public State getState(){
		return state;
	}
	
	public void accept(SynthesijerAstVisitor v){
		v.visitVariableDecl(this);
	}
	
}
