package synthesijer.ast.expr;

import java.io.PrintWriter;

import synthesijer.ast.Expr;
import synthesijer.ast.Op;
import synthesijer.ast.Scope;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLIdent;

public class UnaryExpr extends Expr{
	
	private Op op;
	private Expr arg;
	
	public UnaryExpr(Scope scope){
		super(scope);
	}
	
	public void setArg(Expr arg){
		this.arg = arg;
	}
	
	public void setOp(Op op){
		this.op = op;
	}
	
	public void makeCallGraph(){
		arg.makeCallGraph();
	}

	public void dumpAsXML(PrintWriter dest){
		dest.printf("<expr kind=\"%s\" op=\"%s\">", "UnaryExpr", op);
		dest.printf("<arg>\n");
		arg.dumpAsXML(dest);
		dest.printf("</arg>\n");
		dest.printf("</expr>\n");
	}
	
	public HDLExpr getHDLExprResult(){
		HDLIdent id = new HDLIdent("binaryexpr_result_" + this.hashCode());
		return id;
	}

}
