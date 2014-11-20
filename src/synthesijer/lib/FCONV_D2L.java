package synthesijer.lib;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class FCONV_D2L extends HDLModule{
	
	public double a;
	public long result;
	public boolean valid;
	public boolean nd;
	
	public FCONV_D2L(){
		super("synthesijer_fconv_d2l", "clk", "reset");
		newPort("a",      DIR.IN, HDLPrimitiveType.genVectorType(64));
		newPort("nd",     DIR.IN, HDLPrimitiveType.genBitType());
		newPort("result", DIR.OUT, HDLPrimitiveType.genSignedType(64));
		newPort("valid",  DIR.OUT, HDLPrimitiveType.genBitType());
	}

}
