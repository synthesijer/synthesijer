package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Op;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
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
	
	public Expr getArg(){
		return this.arg;
	}
	
	public Op getOp(){
		return this.op;
	}
	
	public void makeCallGraph(){
		arg.makeCallGraph();
	}
	
	public HDLExpr getHDLExprResult(){
		HDLIdent id = new HDLIdent("binaryexpr_result_" + this.hashCode());
		return id;
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitUnaryExpr(this);
	}
}
