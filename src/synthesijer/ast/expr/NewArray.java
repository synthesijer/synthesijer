package synthesijer.ast.expr;

import java.io.PrintWriter;
import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.hdl.HDLExpr;

public class NewArray extends Expr{
	
	private ArrayList<Expr> dimExpr = new ArrayList<Expr>();
	
	public NewArray(Scope scope){
		super(scope);
	}

	public void addDimExpr(Expr e){
		dimExpr.add(e);
	}

	public void makeCallGraph(){
		for(Expr expr: dimExpr){
			expr.makeCallGraph();
		}
	}

	public void dumpAsXML(PrintWriter dest){
		dest.printf("<expr kind=\"%s\">", "NewArray");
		dest.printf("<dimension>");
		for(Expr expr: dimExpr){
			expr.dumpAsXML(dest);
		}
		dest.printf("</dimension>");
		dest.println("</expr>");
	}
	
	public HDLExpr getHDLExprResult(){
		return null;
	}

}
