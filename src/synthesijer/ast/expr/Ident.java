package synthesijer.ast.expr;

import java.io.PrintWriter;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
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
	
	public String getIdent(){
		return symbol;
	}
	
	public void makeCallGraph(){
		// nothing to do
	}

	public void dumpAsXML(PrintWriter dest){
		dest.printf("<expr kind=\"%s\" value=\"%s\"/>", "Ident", symbol);
	}

	public HDLExpr getHDLExprResult(){
		return new HDLIdent(symbol);
	}

}
