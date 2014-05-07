package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Variable;
import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.ComponentType;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLIdent;

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
	
	public void makeCallGraph(){
		System.out.println("FieldAccess::makeCallGraph");
		if(selected instanceof Ident){
			String name = ((Ident)selected).getSymbol();
			Variable v = getScope().search(name);
			String clazz = "";
			String inst = "";
			if(v != null){
				if(v.getType() instanceof ComponentType){
					clazz = ((ComponentType)v.getType()).getName();
					inst = name;
				}else if(v.getType() instanceof ArrayType){
					clazz = "(Array)";
					inst = name;
				}else{
					clazz = v.getType() + " *** Unexpected";
					inst = name;
				}
			}else{
				clazz = name;
				inst = "** static method **";
			}
			System.out.println("  class   :" + clazz);
			System.out.println("  instance:" + inst);
		}else{
			System.out.println(" ==>");
			selected.makeCallGraph();	
		}
		System.out.println("  method  :" + ident.getSymbol());
	}
	
	public HDLExpr getHDLExprResult(){
		return new HDLIdent(String.format("%s_%s", selected, ident));
	}
	
	
	public void accept(SynthesijerAstVisitor v){
		v.visitFieldAccess(this);
	}

}
