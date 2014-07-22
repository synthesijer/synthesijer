import synthesijer.hdl.HDLModule;

import java.util.EnumSet;

import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.expr.HDLPreDefinedConstant;

public class RS232C_TX extends HDLModule{
	
	public boolean wr;
	public boolean ready;
	public byte din;
	
	public RS232C_TX(String... args){
		super("rs232c_tx", "clk", "reset");
		newParameter("sys_clk", HDLPrimitiveType.genIntegerType(), String.valueOf(25000000));
		newParameter("rate", HDLPrimitiveType.genIntegerType(), String.valueOf(9600));
		newPort("dout", DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
		newPort("wr", DIR.IN,  HDLPrimitiveType.genBitType());
		newPort("din", DIR.IN,  HDLPrimitiveType.genVectorType(8));
		newPort("ready", DIR.OUT,  HDLPrimitiveType.genBitType());
	}
	

}
