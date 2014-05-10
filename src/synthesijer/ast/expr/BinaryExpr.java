package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Op;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLType;

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

	public Expr getLhs(){
		return this.lhs;
	}
	
	public Expr getRhs(){
		return this.rhs;
	}
	
	public Op getOp(){
		return this.op;
	}
		
	public HDLExpr getHDLExprResult(HDLModule m){
		HDLSignal id = m.newSignal("binaryexpr_result_" + this.hashCode(), HDLType.genVectorType(32));
		return id;
	}
	
	public void accept(SynthesijerAstVisitor v){
		v.visitBinaryExpr(this);
	}
}
