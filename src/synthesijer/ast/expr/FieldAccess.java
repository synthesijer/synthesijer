package synthesijer.ast.expr;

import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;

public class FieldAccess extends Expr{
	
	Expr selected;
	Ident ident;
	
	public FieldAccess(Scope scope){
		super(scope);
	}
	
	public void setSelected(Expr selected){
		this.selected = selected;
	}

	public Expr getSelected(){
		return this.selected;
	}

	public void setIdent(Ident ident){
		this.ident = ident;
	}
	
	public Ident getIdent(){
		return ident;
	}
	
	public void accept(SynthesijerExprVisitor v){
		v.visitFieldAccess(this);
	}

	@Override
	public boolean isConstant() {
		return false;
	}
	
	public String toString(){
		return "FieldAccess: " + ident;
	}
	
	@Override
	public boolean isVariable() {
		//return false;
		return true;
	}

	@Override
	public Type getType(){
		if(selected instanceof Ident){
			Variable v = getScope().search(((Ident)selected).getSymbol());
			return v.getType();
		}else{
			throw new RuntimeException("Unsupported multi- field access");
		}
	}

	@Override
	public Variable[] getSrcVariables(){
		ArrayList<Variable> list = new ArrayList<>();
		for(Variable var: selected.getSrcVariables()) list.add(var);
		for(Variable var: ident.getSrcVariables()) list.add(var);
		return list.toArray(new Variable[]{});
	}

	@Override
	public Variable[] getDestVariables(){
		ArrayList<Variable> list = new ArrayList<>();
		for(Variable var: selected.getSrcVariables()) list.add(var);
		return list.toArray(new Variable[]{});
	}
	
	@Override
	public boolean hasMethodInvocation() {
		return selected.hasMethodInvocation();
	}

}
