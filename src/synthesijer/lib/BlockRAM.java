package synthesijer.lib;

import synthesijer.hdl.HDLModule;

public class BlockRAM extends HDLModule{
	
	public BlockRAM(){
		super("dualportram", "clk", "reset");
	}

}
