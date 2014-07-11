
import java.io.IOException;

import synthesijer.hdl.*;
import synthesijer.hdl.expr.*;

public class led_top extends HDLModule{
	
	public led_top(){
		super("led_top", "clk", "reset");
		HDLPort q = newPort("q", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		
                HDLInstance inst = newModuleInstance(new led_skel(), "U");
                inst.getSignalForPort("clk").setAssign(null, getSysClk().getSignal());
                inst.getSignalForPort("reset").setAssign(null, newExpr(HDLOp.NOT, getSysReset().getSignal()));
                inst.getSignalForPort("run_req").setAssign(null, HDLPreDefinedConstant.HIGH);
                q.getSignal().setAssign(null, inst.getSignalForPort("field_flag_output"));

	}
	
	public static void main(String... args) throws IOException{
		led_top obj = new led_top();
		
		HDLUtils.generate(obj, HDLUtils.VHDL);
		HDLUtils.generate(obj, HDLUtils.Verilog);
	}
}

class led_skel extends HDLModule{
	public led_skel(){
		super("led", "clk", "reset");
		newPort("run_req", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		newPort("run_busy", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		newPort("field_flag_output", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
	}
}
