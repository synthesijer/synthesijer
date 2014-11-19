package synthesijer.lib;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class FCONV_F2D extends HDLModule{
	
	public float a;
	public double result;
	public boolean valid;
	public boolean nd;
	
	public FCONV_F2D(){
		super("synthesijer_fconv_d2f", "clk", "reset");
		newPort("a",      DIR.IN, HDLPrimitiveType.genVectorType(32));
		newPort("nd",     DIR.IN, HDLPrimitiveType.genBitType());
		newPort("result", DIR.OUT, HDLPrimitiveType.genVectorType(64));
		newPort("valid",  DIR.OUT, HDLPrimitiveType.genBitType());
	}

}
