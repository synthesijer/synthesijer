
import java.io.IOException;

import synthesijer.hdl.*;
import synthesijer.hdl.expr.*;

public class led_top{
	
	private static void addThreadPort(HDLModule m){
		m.newPort("start_req", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		m.newPort("start_busy", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		m.newPort("join_req", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		m.newPort("join_busy", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		m.newPort("yield_req", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		m.newPort("yield_busy", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
	}
	
	public static void main(String... args) throws IOException{
		HDLModule led_top = new HDLModule("led_top", "clk", "reset");
		HDLPort q0 = led_top.newPort("q0", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		HDLPort q1 = led_top.newPort("q1", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());

		HDLModule led = new HDLModule("led", "clk", "reset");
		HDLPort led_run_req = led.newPort("run_req", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		led.newPort("run_busy", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		HDLPort led_out = led.newPort("flag_out", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		led.newPort("flag_in", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		led.newPort("flag_we", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		addThreadPort(led);
		HDLInstance inst_led = led_top.newModuleInstance(led, "U0");
		inst_led.getSignalForPort("clk").setAssign(null, led_top.getSysClk().getSignal());
		//inst_led.getSignalForPort("reset").setAssign(null, led_top.newExpr(HDLOp.NOT, led_top.getSysReset().getSignal()));
		inst_led.getSignalForPort("reset").setAssign(null, led_top.getSysReset().getSignal());
		inst_led.getSignalForPort(led_run_req.getName()).setAssign(null, HDLPreDefinedConstant.HIGH); // always high to start immediately

		HDLModule firefly = new HDLModule("FireFly", "clk", "reset");
		HDLPort ff_run_req = firefly.newPort("run_req", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		firefly.newPort("run_busy", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		HDLPort ff_out = firefly.newPort("flag_out", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		firefly.newPort("flag_in", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		firefly.newPort("flag_we", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		addThreadPort(firefly);
		HDLInstance inst_ff = led_top.newModuleInstance(firefly, "U1");
		inst_ff.getSignalForPort("clk").setAssign(null, led_top.getSysClk().getSignal());
		//inst_ff.getSignalForPort("reset").setAssign(null, led_top.newExpr(HDLOp.NOT, led_top.getSysReset().getSignal()));
		inst_ff.getSignalForPort("reset").setAssign(null, led_top.getSysReset().getSignal());
		inst_ff.getSignalForPort(ff_run_req.getName()).setAssign(null, HDLPreDefinedConstant.HIGH); // always high to start immediately

		q0.getSignal().setAssign(null, inst_led.getSignalForPort(led_out.getName()));
		q1.getSignal().setAssign(null, inst_ff.getSignalForPort(ff_out.getName()));
		
		HDLUtils.generate(led_top, HDLUtils.VHDL);
		HDLUtils.generate(led_top, HDLUtils.Verilog);
	}
}

