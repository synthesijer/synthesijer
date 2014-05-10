package synthesijer.hdl.literal;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLLiteral;
import synthesijer.hdl.HDLTreeVisitor;

public class HDLValue implements HDLLiteral{
	
	public enum Type{
		VECTOR, SIGNED, BIT, INTEGER, UNKNOWN
	}
	
	private final int width;
	private final Type type;
	private final String value;
	
	public HDLValue(String value, Type type, int width){
		this.value = value;
		this.width = width;
		this.type = type;
	}

	@Override
	public String getVHDL() {
		switch(type){
		case VECTOR:
		case SIGNED:
			String v = String.format("%064x", Long.parseLong(value));
			return String.format("X\"%s\"", v.substring(v.length()-1-width/4, v.length()-1));
		case BIT :
			if(value.equals("true")){
				return "'1'";
			}else{
				return "'0'";
			}
		case INTEGER:
			return String.valueOf(value);
		default:
			return "UNKNWON(" + value + ")";
		}
	}

	@Override
	public String getVerilogHDL() {
		switch(type){
		case VECTOR:
		case SIGNED:
			String v = String.format("%064x", Long.parseLong(value));
			return String.format("%d'h%s", width, v.substring(v.length()-1-width/4, v.length()-1));
		case BIT:
			if(value.equals("true")){
				return "1'b1";
			}else{
				return "1'b0";
			}
		case INTEGER:
			return String.valueOf(value);
		default:
			return "UNKNWON(" + value + ")";
		}
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
