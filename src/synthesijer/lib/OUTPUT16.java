package synthesijer.lib;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.expr.HDLValue;

public class OUTPUT16 extends HDLModule{
	
	public short value;
	
	public OUTPUT16(){
		super("outputport16", "clk", "reset");
		newParameter("WIDTH", HDLPrimitiveType.genIntegerType(), new HDLValue(16));
		newPort("value",  DIR.IN, HDLPrimitiveType.genSignedType(16));
		newPort("dout",  DIR.OUT, HDLPrimitiveType.genVectorType(16), EnumSet.of(HDLPort.OPTION.EXPORT));
	}

}
