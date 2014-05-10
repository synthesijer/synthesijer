package synthesijer.hdl;

public interface HDLType {
	
	public HDLExpr getDefaultValue();
	
	public String getVHDL();
	public String getVerilogHDL();
	
	public enum KIND {
		VECTOR, BIT, SIGNED, USERDEF, INTEGER, STRING, UNKNOWN;
		
		public boolean hasWdith(){
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
			case STRING:
				return true;
			default:
				return false;
			}
		}

	}

	public KIND getKind();
	
}
