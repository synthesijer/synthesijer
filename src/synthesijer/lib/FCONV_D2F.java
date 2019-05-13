package synthesijer.lib;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class FCONV_D2F extends HDLModule{

	public double a;
	public float result;
	public boolean valid;
	public boolean nd;

	public FCONV_D2F(){
		super("synthesijer_fconv_d2f", "clk", "reset");
		newPort("a",      DIR.IN, HDLPrimitiveType.genVectorType(64));
		newPort("nd",     DIR.IN, HDLPrimitiveType.genBitType());
		newPort("result", DIR.OUT, HDLPrimitiveType.genVectorType(32));
		newPort("valid",  DIR.OUT, HDLPrimitiveType.genBitType());
	}

}
