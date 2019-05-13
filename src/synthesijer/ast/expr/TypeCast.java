package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;

public class TypeCast extends Expr{

	private Expr expr;
	private Type targetType;

	public TypeCast(Scope scope){
		super(scope);
	}

	public void setExpr(Expr expr){
		this.expr = expr;
	}

	public Expr getExpr(){
		return expr;
	}

	public void setTargetType(Type t){
		this.targetType = t;
	}

	public void accept(SynthesijerExprVisitor v){
		v.visitTypeCast(this);
	}

	@Override
	public boolean isConstant() {
		return expr.isConstant();
	}

	@Override
	public boolean isVariable() {
		return expr.isVariable();
	}

	public Type getType(){
		//return expr.getType();
		return targetType;
	}

	public String toString(){
		return String.format("(CAST %s::(%s)", targetType, expr);
	}

	@Override
	public Variable[] getSrcVariables(){
		return expr.getSrcVariables();
	}

	@Override
	public Variable[] getDestVariables(){
		return expr.getDestVariables();
	}

	@Override
	public boolean hasMethodInvocation() {
		return expr.hasMethodInvocation();
	}
}
