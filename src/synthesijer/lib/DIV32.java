package synthesijer.lib;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class DIV32 extends HDLModule{
	
	public long a;
	public long b;
	public long result;
	public boolean valid;
	public boolean nd;
	
	public DIV32(){
		super("synthesijer_div32", "clk", "reset");
		newPort("a",      DIR.IN,  HDLPrimitiveType.genSignedType(32));
		newPort("b",      DIR.IN,  HDLPrimitiveType.genSignedType(32));
		newPort("nd",     DIR.IN,  HDLPrimitiveType.genBitType());
		newPort("result", DIR.OUT, HDLPrimitiveType.genSignedType(32));
		newPort("valid",  DIR.OUT, HDLPrimitiveType.genBitType());
	}

}
