package synthesijer.lib;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class INPUT16 extends HDLModule{
	
	short value;
	
	public INPUT16(){
		super("inputport", "clk", "reset");
		newParameter("WIDTH", HDLPrimitiveType.genIntegerType(), String.valueOf(16));
		newPort("value",  DIR.OUT, HDLPrimitiveType.genSignedType(16));
		newPort("din",  DIR.IN, HDLPrimitiveType.genVectorType(16), EnumSet.of(HDLPort.OPTION.EXPORT));
	}

}
