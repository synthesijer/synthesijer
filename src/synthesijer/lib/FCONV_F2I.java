package synthesijer.lib;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class FCONV_F2I extends HDLModule{

	public float a;
	public int result;
	public boolean valid;
	public boolean nd;

	public FCONV_F2I(){
		super("synthesijer_fconv_f2i", "clk", "reset");
		newPort("a",      DIR.IN, HDLPrimitiveType.genVectorType(32));
		newPort("nd",     DIR.IN, HDLPrimitiveType.genBitType());
		newPort("result", DIR.OUT, HDLPrimitiveType.genSignedType(32));
		newPort("valid",  DIR.OUT, HDLPrimitiveType.genBitType());
	}

}
