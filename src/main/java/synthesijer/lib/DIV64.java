package synthesijer.lib;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class DIV64 extends HDLModule{

	public long a;
	public long b;
	public long quantient;
	public long remainder;
	public boolean valid;
	public boolean nd;

	public DIV64(){
		super("synthesijer_div64", "clk", "reset");
		newPort("a",      DIR.IN,  HDLPrimitiveType.genSignedType(64));
		newPort("b",      DIR.IN,  HDLPrimitiveType.genSignedType(64));
		newPort("nd",     DIR.IN,  HDLPrimitiveType.genBitType());
		newPort("quantient", DIR.OUT, HDLPrimitiveType.genSignedType(64));
		newPort("remainder", DIR.OUT, HDLPrimitiveType.genSignedType(64));
		newPort("valid",  DIR.OUT, HDLPrimitiveType.genBitType());
	}

}
