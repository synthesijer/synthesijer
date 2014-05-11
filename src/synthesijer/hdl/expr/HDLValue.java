package synthesijer.hdl.expr;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLLiteral;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLTreeVisitor;

public class HDLValue implements HDLLiteral{
	
	private final HDLPrimitiveType type;
	private final String value;
	
	public HDLValue(String value, HDLPrimitiveType type){
		this.value = value;
		this.type = type;
	}
	
	public String getValue(){
		return value;
	}

	@Override
	public String getVHDL() {
		switch(type.getKind()){
		case VECTOR:
		case SIGNED:
			String v = String.format("%064x", Long.parseLong(value));
			return String.format("X\"%s\"", v.substring(v.length()-1-type.getWidth()/4, v.length()-1));
		case BIT :
			if(value.equals("true")){
				return "'1'";
			}else{
				return "'0'";
			}
		case INTEGER:
			return String.valueOf(value);
		case STRING:
			return value;
		default:
			return "UNKNWON(" + value + ")";
		}
	}

	@Override
	public String getVerilogHDL() {
		switch(type.getKind()){
		case VECTOR:
		case SIGNED:
			String v = String.format("%064x", Long.parseLong(value));
			return String.format("%d'h%s", type.getWidth(), v.substring(v.length()-1-type.getWidth()/4, v.length()-1));
		case BIT:
			if(value.equals("true")){
				return "1'b1";
			}else{
				return "1'b0";
			}
		case INTEGER:
			return String.valueOf(value);
		case STRING:
			return value;
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

	@Override
	public HDLPrimitiveType getType() {
		return type;
	}
	
	@Override
	public HDLSignal[] getSrcSignals() {
		return null;
	}

}
