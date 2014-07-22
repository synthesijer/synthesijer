import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class RS232C_RX extends HDLModule{
	
	public boolean rd;
	public byte dout;
	
	public RS232C_RX(String... args){
		super("rs232c_rx", "clk", "reset");
		newParameter("sys_clk", HDLPrimitiveType.genIntegerType(), String.valueOf(25000000));
		newParameter("rate", HDLPrimitiveType.genIntegerType(), String.valueOf(9600));
		newPort("din",   DIR.IN, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
		newPort("rd", DIR.OUT,  HDLPrimitiveType.genBitType());
		newPort("dout", DIR.OUT,  HDLPrimitiveType.genVectorType(8));
	}

}
