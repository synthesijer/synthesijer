package synthesijer.lib;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class FCOMP32 extends HDLModule{

	public float a;
	public float b;
	public float result;
	public boolean valid;
	public boolean nd;

	public static final int UNORDERED = 0x04; // 000100
	public static final int LT        = 0x0C; // 001100
	public static final int EQ        = 0x14; // 010100
	public static final int LEQ       = 0x1C; // 011100
	public static final int GT        = 0x24; // 100100
	public static final int NEQ       = 0x2C; // 101100
	public static final int GEQ       = 0x34; // 110100

	public FCOMP32(){
		super("synthesijer_fcomp32", "clk", "reset");
		newPort("a",      DIR.IN,  HDLPrimitiveType.genVectorType(32));
		newPort("b",      DIR.IN,  HDLPrimitiveType.genVectorType(32));
		newPort("opcode", DIR.IN,  HDLPrimitiveType.genVectorType(8));
		newPort("nd",     DIR.IN,  HDLPrimitiveType.genBitType());
		newPort("result", DIR.OUT, HDLPrimitiveType.genBitType());
		newPort("valid",  DIR.OUT, HDLPrimitiveType.genBitType());
	}

}
