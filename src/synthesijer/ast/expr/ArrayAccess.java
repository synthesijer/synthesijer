package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLIdent;

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

	public void makeCallGraph(){
		indexed.makeCallGraph();
		index.makeCallGraph();
	}

	@Override
	public HDLExpr getHDLExprResult() {
		if(indexed instanceof Ident){
			String rdata = ((Ident)indexed).getSymbol() + "_rdata";
			HDLIdent id = new HDLIdent(rdata);
			return id;
		}else{
			throw new RuntimeException(String.format("%s(%s) cannot convert to HDL.", indexed, indexed.getClass()));
		}
	}
	
	public void accept(SynthesijerAstVisitor v){
		v.visitArrayAccess(this);
	}

}

