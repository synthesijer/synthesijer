package synthesijer.lib;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class OUTPUT32 extends HDLModule{
	
	public int value;
	
	public OUTPUT32(){
		super("outputport32", "clk", "reset");
		newParameter("WIDTH", HDLPrimitiveType.genIntegerType(), String.valueOf(32));
		newPort("value",  DIR.IN, HDLPrimitiveType.genSignedType(32));
		newPort("dout",  DIR.OUT, HDLPrimitiveType.genVectorType(32), EnumSet.of(HDLPort.OPTION.EXPORT));
	}

}
