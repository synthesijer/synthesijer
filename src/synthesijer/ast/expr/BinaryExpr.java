package synthesijer.ast.expr;

import java.io.PrintWriter;

import synthesijer.ast.Expr;
import synthesijer.ast.Op;
import synthesijer.ast.Scope;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLIdent;

public class BinaryExpr extends Expr{
	
	private Expr lhs, rhs;
	private Op op;
	
	public BinaryExpr(Scope scope){
		super(scope);
	}
	
	public void setLhs(Expr expr){
		this.lhs = expr;
	}
	
	public void setRhs(Expr expr){
		this.rhs = expr;
	}
	
	public void setOp(Op op){
		this.op = op;
	}
	
	public void makeCallGraph(){
		lhs.makeCallGraph();
		rhs.makeCallGraph();
	}
	
	public void dumpAsXML(PrintWriter dest){
		dest.printf("<expr kind=\"%s\" op=\"%s\">", "BinaryExpr", op.toString());
		dest.printf("<lhs>\n");
		lhs.dumpAsXML(dest);
		dest.printf("</lhs>\n");
		dest.printf("<rhs>\n");
		rhs.dumpAsXML(dest);
		dest.printf("</rhs>\n");
		dest.printf("</expr>\n");
	}

	public HDLExpr getHDLExprResult(){
		HDLIdent id = new HDLIdent("binaryexpr_result_" + this.hashCode());
		return id;
	}
	
}
