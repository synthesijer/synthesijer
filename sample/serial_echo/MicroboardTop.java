
import java.io.IOException;

import synthesijer.hdl.*;
import synthesijer.hdl.expr.*;

public class MicroboardTop{
	
	public static void main(String... args) throws IOException{
		HDLModule top = new HDLModule("microboard_top", "CLOCK_Y3", "USER_RESET");
		HDLPort din = top.newPort("USB_RS232_RXD", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		HDLPort dout = top.newPort("USB_RS232_TXD", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());

		//HDLModule m = new HDLModule("EchoTest", "clk", "reset");
		HDLModule m = new HDLModule("ToUpper", "clk", "reset");
		HDLPort run_req  = m.newPort("run_req", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		HDLPort run_busy = m.newPort("run_busy", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		HDLPort tx_dout  = m.newPort("class_tx_0002_dout_exp", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		HDLPort rx_din   = m.newPort("class_rx_0000_din_exp", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		HDLInstance inst = top.newModuleInstance(m, "U");
		
		inst.getSignalForPort("clk").setAssign(null, top.getSysClk().getSignal());
		inst.getSignalForPort("reset").setAssign(null, top.getSysReset().getSignal());
		inst.getSignalForPort(run_req.getName()).setAssign(null, HDLPreDefinedConstant.HIGH); // always high to start immediately
		dout.getSignal().setAssign(null, inst.getSignalForPort(tx_dout.getName()));
		inst.getSignalForPort(rx_din.getName()).setAssign(null, din.getSignal());
		
		HDLUtils.generate(top, HDLUtils.VHDL);
		HDLUtils.generate(top, HDLUtils.Verilog);
	}
}

