package synthesijer.lib;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class OUTPUT1 extends HDLModule{

	public boolean flag;

	public OUTPUT1(){
		super("outputflag", "clk", "reset");
		newPort("flag",  DIR.IN, HDLPrimitiveType.genBitType());
		newPort("dout",  DIR.OUT, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
	}

}
