package synthesijer.ast.expr;

import java.io.PrintWriter;
import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.hdl.HDLExpr;

public class NewClassExpr extends Expr{
	
	private String clazz;
	private ArrayList<Expr> params = new ArrayList<Expr>();
	
	public NewClassExpr(Scope scope){
		super(scope);
	}
	
	public void setClassName(String str){
		clazz = str;
	}
	
	public void addParam(Expr expr){
		params.add(expr);
	}

	public void makeCallGraph(){
		System.out.println("NewClassExpr::makeCallGraph");
		System.out.println(" class:" + clazz);
		System.out.println(" method:" + "<init>");
	}

	public void dumpAsXML(PrintWriter dest){
		dest.printf("<expr kind=\"%s\" class=\"%s\">", "NewClass", clazz);
		dest.printf("<params>");
		for(Expr expr: params){
			expr.dumpAsXML(dest);
		}
		dest.printf("</params>");
		dest.printf("</expr>");
	}

	public HDLExpr getHDLExprResult(){
		return null;
	}

}
