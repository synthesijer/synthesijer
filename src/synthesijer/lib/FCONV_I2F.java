package synthesijer.lib;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class FCONV_I2F extends HDLModule{

	public int a;
	public float result;
	public boolean valid;
	public boolean nd;

	public FCONV_I2F(){
		super("synthesijer_fconv_i2f", "clk", "reset");
		newPort("a",      DIR.IN, HDLPrimitiveType.genSignedType(32));
		newPort("nd",     DIR.IN, HDLPrimitiveType.genBitType());
		newPort("result", DIR.OUT, HDLPrimitiveType.genVectorType(32));
		newPort("valid",  DIR.OUT, HDLPrimitiveType.genBitType());
	}

}
