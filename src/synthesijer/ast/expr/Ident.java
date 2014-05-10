package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLType;

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
	
	public HDLExpr getHDLExprResult(HDLModule m){
		return m.newSignal(symbol, HDLType.genVectorType(32));
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitIdent(this);
	}

}
