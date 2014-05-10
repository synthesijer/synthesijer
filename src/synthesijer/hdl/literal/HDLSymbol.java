package synthesijer.hdl.literal;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLLiteral;
import synthesijer.hdl.HDLTreeVisitor;

public class HDLSymbol implements HDLLiteral{
	
	private final String sym;
	
	public HDLSymbol(String s){
		this.sym = s;
	}
	
	public String getSymbol(){
		return sym;
	}

	@Override
	public String getVHDL() {
		return sym;
	}

	@Override
	public String getVerilogHDL() {
		return sym;
	}

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLLitral(this);
	}
	
	@Override
	public HDLExpr getResultExpr() {
		return this;
	}

}
