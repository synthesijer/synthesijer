package synthesijer.ast.expr;

import java.io.PrintWriter;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.hdl.HDLExpr;

public class ParenExpr extends Expr{
	
	private Expr expr;
	
	public ParenExpr(Scope scope){
		super(scope);
	}

	public void setExpr(Expr expr){
		this.expr = expr;
	}
	
	public void makeCallGraph(){
		expr.makeCallGraph();
	}
	
	public void dumpAsXML(PrintWriter dest){
		dest.printf("<expr kind=\"%s\">", "ParenExpr");
		expr.dumpAsXML(dest);
		dest.printf("</expr>");
	}
	
	public HDLExpr getHDLExprResult(){
		return expr.getHDLExprResult();
	}

}
