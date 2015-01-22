package synthesijer.lib;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class OUTPUT64 extends HDLModule{
	
	public int value;
	
	public OUTPUT64(){
		super("outputport64", "clk", "reset");
		newParameter("WIDTH", HDLPrimitiveType.genIntegerType(), String.valueOf(64));
		newPort("value",  DIR.IN, HDLPrimitiveType.genSignedType(64));
		newPort("dout",  DIR.OUT, HDLPrimitiveType.genVectorType(64), EnumSet.of(HDLPort.OPTION.EXPORT));
	}

}
