package synthesijer.lib;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.expr.HDLValue;

public class FIFO extends HDLModule{
	
	public FIFO(int width, int depth){
		super("simple_fifo", "clk", "reset");
		
		newParameter("WIDTH", HDLPrimitiveType.genIntegerType(), new HDLValue(width));
		newParameter("DEPTH", HDLPrimitiveType.genIntegerType(), new HDLValue(depth));
		
		newPort("din",     DIR.IN,  HDLPrimitiveType.genSignedType(width, "WIDTH-1", "0"));
		newPort("dout",    DIR.OUT, HDLPrimitiveType.genSignedType(width, "WIDTH-1", "0"));
		newPort("we",      DIR.IN,  HDLPrimitiveType.genBitType());
		newPort("oe",      DIR.IN,  HDLPrimitiveType.genBitType());
		newPort("empty",   DIR.OUT,  HDLPrimitiveType.genBitType());
		newPort("full",    DIR.OUT,  HDLPrimitiveType.genBitType());
		
	}

}
