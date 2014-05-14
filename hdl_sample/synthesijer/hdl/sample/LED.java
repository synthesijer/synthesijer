package synthesijer.hdl.sample;

import java.io.IOException;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLUtils;

public class LED extends HDLModule{
	
	public LED(){
		super("led", "clk", "reset");
		HDLPort q = newPort("q", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		
		// q <= counter(24)
		HDLSignal sig = q.getSignal();
		HDLSignal counter = newSignal("counter", HDLPrimitiveType.genSignedType(32));
		sig.setAssign(null, newExpr(HDLOp.REF, counter, 5));
		
		// at main state, counter <= counter + 1
		HDLSequencer seq = newSequencer("main");
		HDLSequencer.SequencerState ss = seq.getIdleState();
		counter.setAssign(ss, newExpr(HDLOp.ADD, counter, 1));
		
	}
	
	public static void main(String... args) throws IOException{
		LED led = new LED();
		BasicSim sim = new BasicSim(led, "led_sim");
		
		HDLUtils.generate(led, HDLUtils.VHDL);
		HDLUtils.generate(led, HDLUtils.Verilog);
		HDLUtils.generate(sim, HDLUtils.VHDL);
		HDLUtils.generate(sim, HDLUtils.Verilog);
	}
}

