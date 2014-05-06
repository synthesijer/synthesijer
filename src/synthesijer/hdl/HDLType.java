package synthesijer.hdl;

import synthesijer.hdl.literal.HDLConstant;

public class HDLType {

	enum KIND {
		VECTOR, BIT, SIGNED, UNKNOWN
	}

	public final KIND kind;
	public final int width;

	public HDLType(KIND kind, int width) {
		this.kind = kind;
		this.width = width;
	}

	public HDLType(KIND kind) {
		this.kind = kind;
		this.width = 1;
	}
	
	public static HDLType genBitType(){
		return new HDLType(KIND.BIT, 1);
	}

	public static HDLType genSignedType(int width){
		return new HDLType(KIND.SIGNED, width);
	}
	
	public static HDLType genVectorType(int width){
		return new HDLType(KIND.VECTOR, width);
	}
	
	public static HDLType genUnkonwType(){
		return new HDLType(KIND.UNKNOWN, 0);
	}

	public HDLLiteral getDefaultValue(){
		if(kind == KIND.VECTOR || kind == KIND.SIGNED){
			return HDLConstant.INTEGER_ZERO;
		}else if(kind == KIND.BIT){
			return HDLConstant.BOOLEAN_FALSE;
		}else{
			return null;
		}
	}
	
	public String getVHDL(){
		switch(kind){
		case VECTOR: return String.format("std_logic_vector(%d-1 downto 0)", width);
		case BIT:    return String.format("std_logic");
		case SIGNED: return String.format("signed(%d-1 downto 0)", width);
		default: return "UNKNOWN";
		}
	}

	public String getVerilogHDL(){
		switch(kind){
		case VECTOR: return String.format("[%d-1 : 0]", width);
		case BIT:    return String.format("");
		case SIGNED: return String.format("signed [%d-1 : 0]", width);
		default: return "UNKNOWN";
		}
	}

}
