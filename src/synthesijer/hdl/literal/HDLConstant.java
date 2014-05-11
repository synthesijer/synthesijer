package synthesijer.hdl.literal;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLLiteral;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLTreeVisitor;
import synthesijer.hdl.HDLPrimitiveType;

public enum HDLConstant implements HDLLiteral{
	
	INTEGER_ZERO("(others => '0')", "0"),
	BOOLEAN_TRUE("true", "1'b1"),
	BOOLEAN_FALSE("false", "1'b0"),
	LOW("'0'", "1'b0"),
	HIGH("'1'", "1'b1");
	
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
	
	public HDLPrimitiveType getType(){
		switch(this){
		case BOOLEAN_FALSE:
		case BOOLEAN_TRUE:
			return HDLPrimitiveType.genBitType();
		case HIGH:
		case LOW:
			return HDLPrimitiveType.genBitType();
		case INTEGER_ZERO:
			return HDLPrimitiveType.genVectorType(-1);
		default: return HDLPrimitiveType.genUnkonwType();
		}
	}

	@Override
	public HDLSignal[] getSrcSignals() {
		return null;
	}

}
