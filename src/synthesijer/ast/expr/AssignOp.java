package synthesijer.ast.expr;

import java.io.PrintWriter;

import synthesijer.SynthesijerUtils;
import synthesijer.ast.Expr;
import synthesijer.ast.Op;
import synthesijer.ast.Scope;
import synthesijer.hdl.HDLExpr;

public class AssignOp extends Expr{
	
	private Expr lhs, rhs;
	private Op op;
	
	public AssignOp(Scope scope){
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
		dest.printf("<expr kind=\"%s\" op=\"%s\">", "AssignOp", SynthesijerUtils.escapeXML(op.toString()));
		dest.printf("<lhs>");
		lhs.dumpAsXML(dest);
		dest.printf("</lhs>");
		dest.printf("<rhs>");
		rhs.dumpAsXML(dest);
		dest.printf("</rhs>");
		dest.printf("</expr>", "AssignOp");
	}
	
	public HDLExpr getHDLExprResult(){
		return lhs.getHDLExprResult();
	}

}
