package synthesijer.ast.expr;

import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Op;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;

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
	
	@Override
	public boolean isVariable() {
		return false;
	}

	@Override
	public Type getType(){
		return lhs.getType();
	}
	
	@Override
	public Variable[] getSrcVariables(){
		ArrayList<Variable> list = new ArrayList<>();
		for(Variable var: lhs.getSrcVariables()) list.add(var);
		for(Variable var: rhs.getSrcVariables()) list.add(var);
		return list.toArray(new Variable[]{});
	}

	@Override
	public Variable[] getDestVariables(){
		ArrayList<Variable> list = new ArrayList<>();
		for(Variable var: lhs.getSrcVariables()) list.add(var);
		return list.toArray(new Variable[]{});
	}
}
