package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;

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
		return false; // TODO
	}
}
