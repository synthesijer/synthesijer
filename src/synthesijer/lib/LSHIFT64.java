package synthesijer.lib;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class LSHIFT64 extends HDLModule{

	public long a;
	public long b;
	public long result;
	public boolean valid;
	public boolean nd;

	public LSHIFT64(){
		super("synthesijer_lshift64", "clk", "reset");
		newPort("a",      DIR.IN,  HDLPrimitiveType.genSignedType(64));
		newPort("b",      DIR.IN,  HDLPrimitiveType.genSignedType(64));
		newPort("nd",     DIR.IN,  HDLPrimitiveType.genBitType());
		newPort("result", DIR.OUT, HDLPrimitiveType.genSignedType(64));
		newPort("valid",  DIR.OUT, HDLPrimitiveType.genBitType());
	}

}
