package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Op;
import synthesijer.ast.Scope;

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
	
	public void accept(SynthesijerExprVisitor v){
		v.visitUnaryExpr(this);
	}

	@Override
	public boolean isConstant() {
		return arg.isConstant();
	}

}
