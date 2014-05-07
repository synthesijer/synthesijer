package synthesijer.hdl;

import synthesijer.hdl.literal.HDLConstant;
import synthesijer.hdl.literal.HDLSymbol;

/**
 * In order to make an instance of this class, use builder method.
 *   
 * @author miyo
  */
public class HDLType implements HDLTree{

	enum KIND {
		VECTOR, BIT, SIGNED, USERDEF, UNKNOWN
	}

	public final KIND kind;
	public final int width;

	HDLType(KIND kind, int width) {
		this.kind = kind;
		this.width = width;
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
	
	public static HDLType genUserDefType(String base, HDLSymbol[] suffixes, int defaultValueIndex){
		return new HDLUserDefinedType(base, suffixes, defaultValueIndex);
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

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLType(this);
	}

}
