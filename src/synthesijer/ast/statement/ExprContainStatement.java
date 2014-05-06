package synthesijer.ast.statement;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;

public abstract class ExprContainStatement extends Statement{
	
	public ExprContainStatement(Scope scope){
		super(scope);
	}
	
	abstract public Expr getExpr();

}
