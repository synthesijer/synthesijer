package synthesijer.ast.expr;

import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;

public class ArrayAccess extends Expr{
	
	private Expr indexed, index;
	
	public ArrayAccess(Scope scope){
		super(scope);
	}
	
	public void setIndexed(Expr expr){
		indexed = expr;
	}
	
	public void setIndex(Expr expr){
		index = expr;
	}

	public Expr getIndexed(){
		return indexed;
	}
	
	public Expr getIndex(){
		return index;
	}
		
	public void accept(SynthesijerExprVisitor v){
		v.visitArrayAccess(this);
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
		return indexed.getType();
	}
	
	@Override
	public Variable[] getSrcVariables(){
		ArrayList<Variable> list = new ArrayList<>();
		for(Variable var: index.getSrcVariables()) list.add(var);
		for(Variable var: indexed.getSrcVariables()) list.add(var);
		return list.toArray(new Variable[]{});
	}

	@Override
	public Variable[] getDestVariables(){
		ArrayList<Variable> list = new ArrayList<>();
		for(Variable var: indexed.getSrcVariables()) list.add(var);
		return list.toArray(new Variable[]{});
	}

	@Override
	public boolean hasMethodInvocation() {
		//return index.hasMethodInvocation() || indexed.hasMethodInvocation();
		return true;
	}
}

