package synthesijer.ast.expr;

import java.io.PrintWriter;
import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLIdent;

public class MethodInvocation extends Expr{
	
	private Expr method;
	private ArrayList<Expr> params = new ArrayList<Expr>();
	
	public MethodInvocation(Scope scope){
		super(scope);
	}
	
	public void setMethod(Expr expr){
		method = expr;
	}
	
	public void addParameter(Expr expr){
		params. add(expr);		
	}
	
	public String getMethodName(){
		if(method instanceof Ident){
			return ((Ident)method).getIdent();
		}else if(method instanceof FieldAccess){
			return ((FieldAccess)method).getIdent();
		}
		return method.toString();
	}

	public void dumpAsXML(PrintWriter dest){
		dest.printf("<expr kind=\"%s\">", "MethodInvocation");
		dest.printf("<method name=\"%s\">", getMethodName());
		if(method instanceof FieldAccess){
			method.dumpAsXML(dest);
		}
		dest.printf("</method>\n");
		dest.printf("<params>\n");
		for(Expr expr: params){
			expr.dumpAsXML(dest);
		}
		dest.printf("</params>\n");
		dest.printf("</expr>");
	}

	public void makeCallGraph(){
		System.out.println("MethodInvocation::makeCallGraph");
		if(method instanceof Ident){
			System.out.println("  class   :" + getScope().getModule().getName());
			System.out.println("  instance:" + "this");
			System.out.println("  method  :" + ((Ident)method).getIdent());
		}else{
			method.makeCallGraph();
		}
	}
	
	public HDLExpr getHDLExprResult(){
		return new HDLIdent(getMethodName() + "_return_value");
	}

}
