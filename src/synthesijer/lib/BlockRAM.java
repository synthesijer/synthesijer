package synthesijer.lib;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class BlockRAM extends HDLModule{
	
	public BlockRAM(int width){
		super("simple_dualportram", "clk", "reset");
		newParameter("WIDTH", HDLPrimitiveType.genIntegerType(), String.valueOf(32), String.valueOf(width));
		newParameter("DEPTH", HDLPrimitiveType.genIntegerType(), String.valueOf(10), String.valueOf(16));
		newParameter("WORDS", HDLPrimitiveType.genIntegerType(), String.valueOf(1024), String.valueOf(65536));
		newPort("length",   DIR.OUT, HDLPrimitiveType.genSignedType(32));
		newPort("waddress", DIR.IN,  HDLPrimitiveType.genSignedType(32));
		newPort("raddress", DIR.IN,  HDLPrimitiveType.genSignedType(32));
		newPort("din",      DIR.IN,  HDLPrimitiveType.genSignedType(width));
		newPort("dout",     DIR.OUT, HDLPrimitiveType.genSignedType(width));
		newPort("we",       DIR.IN,  HDLPrimitiveType.genBitType());
	}

}
