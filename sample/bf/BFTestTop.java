import java.io.IOException;

import java.util.EnumSet;

import synthesijer.hdl.*;
import synthesijer.hdl.expr.*;

public class BFTestTop {

	public static void main(String... args) throws IOException {
		HDLModule top = new HDLModule("top", "clk", "reset");
		HDLPort top_rx_din = top.newPort("rx_din", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		HDLPort top_tx_dout = top.newPort("tx_dout", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());

		HDLModule target = new HDLModule("HW", "clk", "reset");
		HDLPort main_req = target.newPort("main_req", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		HDLPort main_busy = target.newPort("main_busy", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		HDLPort rx_din = target.newPort("b_io_obj_rx_din", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		HDLPort tx_dout = target.newPort("b_io_obj_tx_dout", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		HDLInstance inst = top.newModuleInstance(target, "U");

		inst.getSignalForPort("clk").setAssign(null, top.getSysClk().getSignal());
		inst.getSignalForPort("reset").setAssign(null, top.newExpr(HDLOp.NOT, top.getSysReset().getSignal()));
		inst.getSignalForPort("main_req").setAssign(null, HDLPreDefinedConstant.HIGH); // always high to start immediately
		inst.getSignalForPort(rx_din.getName()).setAssign(null, top_rx_din.getSignal());
		top_tx_dout.getSignal().setAssign(null, inst.getSignalForPort(tx_dout.getName()));

		HDLUtils.generate(top, HDLUtils.VHDL);
		HDLUtils.generate(top, HDLUtils.Verilog);
	}
}
