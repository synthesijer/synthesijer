package synthesijer.hdl;

import synthesijer.hdl.expr.HDLPreDefinedConstant;

/**
 * In order to make an instance of this class, use builder method.
 *   
 * @author miyo
  */
public class HDLPrimitiveType implements HDLTree, HDLType{

	private final KIND kind;
	private final int width;
	private final String begin;
	private final String end;

	private HDLPrimitiveType(KIND kind, int width, String begin, String end) {
		this.kind = kind;
		this.width = width;
		this.begin = begin;
		this.end = end;
	}

	private HDLPrimitiveType(KIND kind, int width) {
		this.kind = kind;
		this.width = width;
		if(width > 0){
			begin = String.valueOf(width-1);
		}else{
			begin = String.valueOf(0);
		}
		end = String.valueOf(0);
	}
	
	@Override
	public boolean isEqual(HDLType t) {
		if(!(t instanceof HDLPrimitiveType)) return false;
		HDLPrimitiveType t0 = (HDLPrimitiveType)t;
		if(kind != t0.kind) return false;
		if(width != t0.width) return false;
		return true;
	};
	
	public KIND getKind(){
		return kind;
	}

	public boolean isBit(){
		return kind == KIND.BIT;
	}
	
	public boolean isSigned(){
		return kind == KIND.SIGNED;
	}
	
	public boolean isVector(){
		return kind == KIND.VECTOR;
	}
	
	public boolean isInteger(){
		return kind == KIND.INTEGER;
	}
	
	public int getWidth(){
		return width;
	}
	
	public boolean isEqualKind(HDLPrimitiveType t){
		return kind == t.kind;
	}
	
	public static HDLPrimitiveType genBitType(){
		return new HDLPrimitiveType(KIND.BIT, 1);
	}

	public static HDLPrimitiveType genSignedType(int width){
		return new HDLPrimitiveType(KIND.SIGNED, width);
	}

	public static HDLPrimitiveType genSignedType(int width, String begin, String end){
		return new HDLPrimitiveType(KIND.SIGNED, width, begin, end);
	}

	public static HDLPrimitiveType genIntegerType(){
		return new HDLPrimitiveType(KIND.INTEGER, 0);
	}

	public static HDLPrimitiveType genStringType(){
		return new HDLPrimitiveType(KIND.STRING, 0);
	}

	public static HDLPrimitiveType genVectorType(int width){
		return new HDLPrimitiveType(KIND.VECTOR, width);
	}
		
	public static HDLPrimitiveType genVectorType(int width, String begin, String end){
		return new HDLPrimitiveType(KIND.VECTOR, width, begin, end);
	}

	public static HDLPrimitiveType genUnknowType(){
		return new HDLPrimitiveType(KIND.UNKNOWN, 0);
	}

	public HDLLiteral getDefaultValue(){
		if(kind == KIND.VECTOR || kind == KIND.SIGNED){
			return HDLPreDefinedConstant.VECTOR_ZERO;
		}else if(kind == KIND.BIT){
			return HDLPreDefinedConstant.LOW;
		}else if(kind == KIND.INTEGER){
			return HDLPreDefinedConstant.INTEGER_ZERO;
		}else{
			return null;
		}
	}
	
	public String getVHDL(boolean paramFlag){
		if(paramFlag == false) return getVHDL();
		switch(kind){
		case VECTOR: return String.format("std_logic_vector(%s downto %s)", begin, end);
		case BIT:    return String.format("std_logic");
		case SIGNED: return String.format("signed(%s downto %s)", begin, end);
		case INTEGER: return "integer";
		default: return "UNKNOWN";
		}
	}

	public String getVHDL(){
		switch(kind){
		case VECTOR: return String.format("std_logic_vector(%d-1 downto 0)", width);
		case BIT:    return String.format("std_logic");
		case SIGNED: return String.format("signed(%d-1 downto 0)", width);
		case INTEGER: return "integer";
		default: return "UNKNOWN";
		}
	}

	public String getVerilogHDL(){
		switch(kind){
		case VECTOR: return String.format("[%d-1 : 0]", width);
		case BIT:    return String.format("");
		case SIGNED: return String.format("signed [%d-1 : 0]", width, end);
		case INTEGER: return "";
		default: return "UNKNOWN";
		}
	}

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLType(this);
	}
	
	public String toString(){
		return "HDLPrimitiveType:" + kind;
	}

}
