package synthesijer.lib;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class FCONV_L2D extends HDLModule{
	
	public long a;
	public double result;
	public boolean valid;
	public boolean nd;
	
	public FCONV_L2D(){
		super("synthesijer_fconv_i2f", "clk", "reset");
		newPort("a",      DIR.IN, HDLPrimitiveType.genSignedType(64));
		newPort("nd",     DIR.IN, HDLPrimitiveType.genBitType());
		newPort("result", DIR.OUT, HDLPrimitiveType.genVectorType(64));
		newPort("valid",  DIR.OUT, HDLPrimitiveType.genBitType());
	}

}
