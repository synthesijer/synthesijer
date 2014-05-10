package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLType;

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
		
	public HDLExpr getHDLExprResult(HDLModule m){
		return m.newSignal(String.format("%s_%s", selected, ident), HDLType.genVectorType(32));
	}
	
	
	public void accept(SynthesijerAstVisitor v){
		v.visitFieldAccess(this);
	}

}
