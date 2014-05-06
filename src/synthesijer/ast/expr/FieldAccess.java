package synthesijer.ast.expr;

import java.io.PrintWriter;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
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
	
	public void setSelecteExpr(Expr selected){
		this.selected = selected;
	}
	
	public void setIdent(Ident ident){
		this.ident = ident;
	}
	
	public String getIdent(){
		return ident.getIdent();
	}
	
	public void makeCallGraph(){
		System.out.println("FieldAccess::makeCallGraph");
		if(selected instanceof Ident){
			String name = ((Ident)selected).getIdent();
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
		System.out.println("  method  :" + ident.getIdent());
	}

	public void dumpAsXML(PrintWriter dest){
		dest.printf("<expr kind=\"%s\" ident=\"%s\">", "FieldAccess", ident.getIdent());
		selected.dumpAsXML(dest);
		dest.printf("</expr>");
	}
	
	public HDLExpr getHDLExprResult(){
		return new HDLIdent(String.format("%s_%s", selected, ident));
	}
	
	
}
