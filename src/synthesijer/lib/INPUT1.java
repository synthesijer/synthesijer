package synthesijer.lib;

import java.util.EnumSet;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;

public class INPUT1 extends HDLModule{

	public boolean flag;

	public INPUT1(){
		super("inputflag", "clk", "reset");
		newPort("flag",  DIR.OUT, HDLPrimitiveType.genBitType());
		newPort("din",  DIR.IN, HDLPrimitiveType.genBitType(), EnumSet.of(HDLPort.OPTION.EXPORT));
	}

}
