package synthesijer.ast.expr;

import java.io.PrintWriter;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.hdl.HDLExpr;

public class AssignExpr extends Expr{
	
	private Expr lhs, rhs;
	
	public AssignExpr(Scope scope){
		super(scope);
	}
	
	public void setLhs(Expr expr){
		lhs = expr;
	}
	
	public void setRhs(Expr expr){
		rhs = expr;
	}
	
	public void makeCallGraph(){
		lhs.makeCallGraph();
		rhs.makeCallGraph();
	}

	public void dumpAsXML(PrintWriter dest){
		dest.printf("<expr kind=\"%s\">", "AssignExpr");
		dest.printf("<lhs>");
		lhs.dumpAsXML(dest);
		dest.printf("</lhs>");
		dest.printf("<rhs>");
		rhs.dumpAsXML(dest);
		dest.printf("</rhs>");
		dest.printf("</expr>\n");
	}
	
	public HDLExpr getHDLExprResult(){
		return lhs.getHDLExprResult();
	}
	
	
}
