package synthesijer.ast.expr;

import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;

public class CondExpr extends Expr {
	
	private Expr cond, truePart, falsePart;

	public CondExpr(Scope scope) {
		super(scope);
	}
	
	public void setCond(Expr cond){
		this.cond = cond;
	}
	
	public Expr getCond(){
		return this.cond;
	}

	public void setTruePart(Expr cond){
		this.truePart = cond;
	}
	
	public Expr getTruePart(){
		return this.truePart;
	}

	public void setFalsePart(Expr cond){
		this.falsePart = cond;
	}

	public Expr getFalsePart(){
		return this.falsePart;
	}

	@Override
	public void accept(SynthesijerExprVisitor v) {
		v.visitCondExpr(this);
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean isVariable() {
		return false;
	}

	@Override
	public Type getType() {
		return truePart.getType();
	}

	@Override
	public Variable[] getSrcVariables() {
		ArrayList<Variable> list = new ArrayList<Variable>();
		for(Variable var: cond.getSrcVariables()) list.add(var);
		for(Variable var: truePart.getSrcVariables()) list.add(var);
		for(Variable var: falsePart.getSrcVariables()) list.add(var);
		return list.toArray(new Variable[]{});
	}

	@Override
	public Variable[] getDestVariables() {
		return new Variable[]{};
	}

	@Override
	public boolean hasMethodInvocation() {
		return cond.hasMethodInvocation() | truePart.hasMethodInvocation() | falsePart.hasMethodInvocation();
	}

}
