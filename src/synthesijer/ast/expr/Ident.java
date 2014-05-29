package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;

public class Ident extends Expr{
	
	private String symbol;
	
	public Ident(Scope scope){
		super(scope);
	}
	
	public void setIdent(String value){
		symbol = value;
	}
	
	public String getSymbol(){
		return symbol;
	}
	
	public void accept(SynthesijerExprVisitor v){
		v.visitIdent(this);
	}

	public String toString(){
		return String.format("Ident::(%s)", symbol); 
	}
}
