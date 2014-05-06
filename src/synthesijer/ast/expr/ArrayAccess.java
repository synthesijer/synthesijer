package synthesijer.ast.expr;

import java.io.PrintWriter;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLIdent;

public class ArrayAccess extends Expr{
	
	private Expr indexed, index;
	
	public ArrayAccess(Scope scope){
		super(scope);
	}
	
	public void setIndexed(Expr expr){
		indexed = expr;
	}
	
	public void setIndex(Expr expr){
		index = expr;
	}
	
	public void makeCallGraph(){
		indexed.makeCallGraph();
		index.makeCallGraph();
	}

	public void dumpAsXML(PrintWriter dest){
		dest.printf("<expr kind=\"%s\">", "ArrayAccess");
		dest.printf("<indexed>");
		indexed.dumpAsXML(dest);
		dest.printf("</indexed>");
		dest.printf("<index>");
		index.dumpAsXML(dest);
		dest.printf("</index>");
		dest.printf("</expr>", "ArrayAccess");
	}
	

	@Override
	public HDLExpr getHDLExprResult() {
		if(indexed instanceof Ident){
			String rdata = ((Ident)indexed).getIdent() + "_rdata";
			HDLIdent id = new HDLIdent(rdata);
			return id;
		}else{
			throw new RuntimeException(String.format("%s(%s) cannot convert to HDL.", indexed, indexed.getClass()));
		}
	}
	
}
