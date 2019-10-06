package synthesijer.lib;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class LOGIC_RSHIFT32 extends HDLModule{

	public int a;
	public int b;
	public int result;
	public boolean valid;
	public boolean nd;

	public LOGIC_RSHIFT32(){
		super("synthesijer_logic_rshift32", "clk", "reset");
		newPort("a",      DIR.IN,  HDLPrimitiveType.genSignedType(32));
		newPort("b",      DIR.IN,  HDLPrimitiveType.genSignedType(32));
		newPort("nd",     DIR.IN,  HDLPrimitiveType.genBitType());
		newPort("result", DIR.OUT, HDLPrimitiveType.genSignedType(32));
		newPort("valid",  DIR.OUT, HDLPrimitiveType.genBitType());
	}

}
