package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLIdent;

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
	
	public void makeCallGraph(){
		// nothing to do
	}

	public HDLExpr getHDLExprResult(){
		return new HDLIdent(symbol);
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitIdent(this);
	}

}
