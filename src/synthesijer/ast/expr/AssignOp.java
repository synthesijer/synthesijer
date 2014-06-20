package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Op;
import synthesijer.ast.Scope;

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
	
	public Expr getLhs(){
		return this.lhs;
	}
	
	public Expr getRhs(){
		return this.rhs;
	}

	public void setOp(Op op){
		this.op = op;
	}
	
	public Op getOp(){
		return op;
	}
	
	public void accept(SynthesijerExprVisitor v){
		v.visitAssignOp(this);
	}

	@Override
	public boolean isConstant() {
		return false;
	}
}
