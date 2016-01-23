package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Op;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;

public class UnaryExpr extends Expr{
	
	private Op op;
	private Expr arg;
	private boolean postfixFlag = false;
	
	public UnaryExpr(Scope scope){
		super(scope);
	}
	
	public void setArg(Expr arg){
		this.arg = arg;
	}
	
	public void setOp(Op op){
		this.op = op;
	}

	public void setPostfix(boolean f){
		postfixFlag = f;
	}
	
	public Expr getArg(){
		return this.arg;
	}
	
	public Op getOp(){
		return this.op;
	}

	public boolean isPostfix(){
		return postfixFlag;
	}
	
	public void accept(SynthesijerExprVisitor v){
		v.visitUnaryExpr(this);
	}

	@Override
	public boolean isConstant() {
		return arg.isConstant();
	}
	
	@Override
	public boolean isVariable() {
		return arg.isVariable();
	}

	public Type getType(){
		return arg.getType();
	}
	
	public String toString(){
		return String.format("UnaryExpr::(%s %s)", op, arg); 
	}
	
	@Override
	public Variable[] getSrcVariables(){
		return arg.getSrcVariables();
	}

	@Override
	public Variable[] getDestVariables(){
		if(op == Op.INC || op == Op.DEC){
			return arg.getDestVariables();
		}else{
			return new Variable[]{};
		}
	}
	
	@Override
	public boolean hasMethodInvocation() {
		return arg.hasMethodInvocation();
	}
}
