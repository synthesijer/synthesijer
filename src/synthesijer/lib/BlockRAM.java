package synthesijer.lib;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class BlockRAM extends HDLModule{
	
	public BlockRAM(){
		super("simple_dualportram", "clk", "reset");
		newPort("length",   DIR.OUT, HDLPrimitiveType.genSignedType(32));
		newPort("waddress", DIR.IN,  HDLPrimitiveType.genSignedType(32));
		newPort("raddress", DIR.IN,  HDLPrimitiveType.genSignedType(32));
		newPort("din",      DIR.IN,  HDLPrimitiveType.genSignedType(32));
		newPort("dout",     DIR.OUT, HDLPrimitiveType.genSignedType(32));
		newPort("we",       DIR.IN,  HDLPrimitiveType.genBitType());
	}

}
