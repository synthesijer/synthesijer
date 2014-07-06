package synthesijer.ast.expr;

import javax.management.RuntimeErrorException;

import synthesijer.Manager;
import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;
import synthesijer.ast.type.ComponentType;

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
		return false;
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

}
