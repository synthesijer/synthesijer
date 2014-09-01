package synthesijer.ast.statement;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.Variable;
import synthesijer.model.State;

public abstract class ExprContainStatement extends Statement{
	
	public ExprContainStatement(Scope scope){
		super(scope);
	}
	
	abstract public Expr getExpr();
	
	abstract public State getState();
	
	abstract public void setState(State s);

	abstract public Variable[] getSrcVariables();
	
	abstract public Variable[] getDestVariables();
	
	public boolean hasMethodInvocation(){
		Expr expr = getExpr();
		if(expr == null) return false;
		return expr.hasMethodInvocation();
	}
	
}
