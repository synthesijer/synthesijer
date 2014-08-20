package synthesijer.lib;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class SimpleBlockRAM extends HDLModule{
	
	public SimpleBlockRAM(int width, int depth, int length){
		super("simpleportram", "clk", "reset");
		
		newParameter("WIDTH", HDLPrimitiveType.genIntegerType(), String.valueOf(width));
		newParameter("DEPTH", HDLPrimitiveType.genIntegerType(), String.valueOf(depth));
		newParameter("WORDS", HDLPrimitiveType.genIntegerType(), String.valueOf(length));
		
		newPort("address_b", DIR.IN,  HDLPrimitiveType.genSignedType(32));
		newPort("din_b",     DIR.IN,  HDLPrimitiveType.genSignedType(width));
		newPort("dout_b",    DIR.OUT, HDLPrimitiveType.genSignedType(width));
		newPort("we_b",      DIR.IN,  HDLPrimitiveType.genBitType());
		newPort("oe_b",      DIR.IN,  HDLPrimitiveType.genBitType());
	}

}
