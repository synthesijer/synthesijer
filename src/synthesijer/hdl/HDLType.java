package synthesijer.hdl;

public interface HDLType {
	
    public HDLExpr getDefaultValue();
	
    public String getVHDL();
    public String getVerilogHDL();
	
    public boolean isEqual(HDLType t);
	
    public enum KIND {
	VECTOR, BIT, SIGNED, USERDEF, INTEGER, STRING, DIGIT, UNKNOWN;
		
	public boolean hasWidth(){
	    switch (this) {
	    case VECTOR:
	    case SIGNED:
		return true;
	    default:
		return false;
	    }
	}
		
	public boolean isPrimitive(){
	    switch (this) {
	    case VECTOR:
	    case BIT:
	    case SIGNED:
	    case INTEGER:
	    case DIGIT:
	    case STRING:
		return true;
	    default:
		return false;
	    }
	}

    }

    public KIND getKind();

    public boolean isBit();
    public boolean isVector();
    public boolean isSigned();
    public boolean isInteger();
    public boolean isDigit();

    public boolean hasWidth();
    public int getWidth();
}
