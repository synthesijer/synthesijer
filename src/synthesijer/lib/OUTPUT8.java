package synthesijer.lib;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.expr.HDLValue;

public class OUTPUT8 extends HDLModule{

	public short value;

	public OUTPUT8(){
		super("outputport8", "clk", "reset");
		newParameter("WIDTH", HDLPrimitiveType.genIntegerType(), new HDLValue(8));
		newPort("value",  DIR.IN, HDLPrimitiveType.genSignedType(8));
		newPort("dout",  DIR.OUT, HDLPrimitiveType.genVectorType(8), EnumSet.of(HDLPort.OPTION.EXPORT));
	}

}
