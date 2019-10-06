package synthesijer.lib;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.expr.HDLValue;

public class INPUT8 extends HDLModule {

	public short value;

	public INPUT8() {
		super("inputport8", "clk", "reset");
		newParameter("WIDTH", HDLPrimitiveType.genIntegerType(), new HDLValue(8));
		newPort("value", DIR.OUT, HDLPrimitiveType.genSignedType(8));
		newPort("din", DIR.IN, HDLPrimitiveType.genVectorType(8), EnumSet.of(HDLPort.OPTION.EXPORT));
	}

}
