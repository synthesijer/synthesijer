package synthesijer.lib;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.expr.HDLValue;

public class INPUT64 extends HDLModule{
	
	public long value;
	
	public INPUT64(){
		super("inputport64", "clk", "reset");
		newParameter("WIDTH", HDLPrimitiveType.genIntegerType(), new HDLValue(64));
		newPort("value",  DIR.OUT, HDLPrimitiveType.genSignedType(64));
		newPort("din",  DIR.IN, HDLPrimitiveType.genVectorType(64), EnumSet.of(HDLPort.OPTION.EXPORT));
	}

}
