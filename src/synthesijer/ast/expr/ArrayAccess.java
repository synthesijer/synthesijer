package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLPrimitiveType;

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
	
	@Override
	public HDLExpr getHDLExprResult(HDLModule m) {
		if(indexed instanceof Ident){
			String rdata = ((Ident)indexed).getSymbol() + "_rdata";
			HDLSignal id = m.newSignal(rdata, HDLPrimitiveType.genVectorType(32));
			return id;
		}else{
			throw new RuntimeException(String.format("%s(%s) cannot convert to HDL.", indexed, indexed.getClass()));
		}
	}
	
	public void accept(SynthesijerAstVisitor v){
		v.visitArrayAccess(this);
	}

}

