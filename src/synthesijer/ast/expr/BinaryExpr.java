package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Op;
import synthesijer.ast.Scope;

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
	
	public void accept(SynthesijerExprVisitor v){
		v.visitBinaryExpr(this);
	}
	
	public String toString(){
		return String.format("BinaryExpr::(%s %s %s)", op, lhs, rhs); 
	}
}
