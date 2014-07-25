package synthesijer.ast.expr;

import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;

public class AssignExpr extends Expr{
	
	private Expr lhs, rhs;
	
	public AssignExpr(Scope scope){
		super(scope);
	}
	
	public void setLhs(Expr expr){
		lhs = expr;
	}
	
	public void setRhs(Expr expr){
		rhs = expr;
	}

	public Expr getLhs(){
		return lhs;
	}
	
	public Expr getRhs(){
		return rhs;
	}
		
	public void accept(SynthesijerExprVisitor v){
		v.visitAssignExpr(this);
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
	
	public String toString(){
		return String.format("Assign::(assign %s %s)", lhs, rhs); 
	}
	
	@Override
	public Variable[] getSrcVariables(){
		ArrayList<Variable> list = new ArrayList<>();
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
