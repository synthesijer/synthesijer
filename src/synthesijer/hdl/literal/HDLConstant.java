package synthesijer.hdl.literal;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLLiteral;
import synthesijer.hdl.HDLTreeVisitor;

public enum HDLConstant implements HDLLiteral{
	
	INTEGER_ZERO("(ohters => '0')", "0"),
	BOOLEAN_TRUE("true", "1'b1"),
	BOOLEAN_FALSE("false", "1'b0");
	
	private final String vhdl, verilog;
	
	private HDLConstant(String vhdl, String verilog){
		this.vhdl = vhdl;
		this.verilog = verilog;
	}
	
	public String getVHDL(){
		return vhdl;
	}

	public String getVerilogHDL(){
		return verilog;
	}

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLLitral(this);
	}
	
	public HDLExpr getResultExpr(){
		return this;
	}

}
