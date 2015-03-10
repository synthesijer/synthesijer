package synthesijer.lib;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class SimpleBlockRAM extends HDLModule{
	
	public SimpleBlockRAM(int width, int depth, int length){
		super("singleportram", "clk", "reset");
		
		newParameter("WIDTH", HDLPrimitiveType.genIntegerType(), String.valueOf(width));
		newParameter("DEPTH", HDLPrimitiveType.genIntegerType(), String.valueOf(depth));
		newParameter("WORDS", HDLPrimitiveType.genIntegerType(), String.valueOf(length));
		
		newPort("length",  DIR.OUT, HDLPrimitiveType.genSignedType(32));
		newPort("address_b", DIR.IN,  HDLPrimitiveType.genSignedType(32));
		newPort("din_b",     DIR.IN,  HDLPrimitiveType.genSignedType(width, "WIDTH-1", "0"));
		newPort("dout_b",    DIR.OUT, HDLPrimitiveType.genSignedType(width, "WIDTH-1", "0"));
		newPort("we_b",      DIR.IN,  HDLPrimitiveType.genBitType());
		newPort("oe_b",      DIR.IN,  HDLPrimitiveType.genBitType());
	}

}
