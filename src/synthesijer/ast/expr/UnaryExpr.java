package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Op;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLType;

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
	
	public HDLExpr getHDLExprResult(HDLModule m){
		HDLSignal id = m.newSignal("binaryexpr_result_" + this.hashCode(), HDLType.genVectorType(32));
		return id;
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitUnaryExpr(this);
	}
}
