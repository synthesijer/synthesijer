package synthesijer.lib;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class DIV32 extends HDLModule{
	
	public int a;
	public int b;
	public int quantient;
	public int remainder;
	public boolean valid;
	public boolean nd;
	
	public DIV32(){
		super("synthesijer_div32", "clk", "reset");
		newPort("a",      DIR.IN,  HDLPrimitiveType.genSignedType(32));
		newPort("b",      DIR.IN,  HDLPrimitiveType.genSignedType(32));
		newPort("nd",     DIR.IN,  HDLPrimitiveType.genBitType());
		newPort("quantient", DIR.OUT, HDLPrimitiveType.genSignedType(32));
		newPort("remainder", DIR.OUT, HDLPrimitiveType.genSignedType(32));
		newPort("valid",  DIR.OUT, HDLPrimitiveType.genBitType());
	}

}
