package synthesijer.hdl.sample;

import java.io.IOException;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLType;
import synthesijer.hdl.HDLUtils;

public class LED {

	public static void main(String... args) throws IOException{
		HDLModule m = new HDLModule("led", "clk", "reset");
		HDLPort q = m.newPort("q", HDLPort.DIR.OUT, HDLType.genBitType());
		
		// q <= counter(24)
		HDLSignal sig = q.getSrcSignal();
		HDLSignal counter = m.newSignal("counter", HDLType.genSignedType(32));
		sig.setAssign(null, m.newExpr(HDLOp.REF, counter, 24));
		
		// at main state, counter <= counter + 1
		HDLSequencer seq = m.newSequencer("main");
		HDLSequencer.SequencerState ss = seq.getIdleState();
		counter.setAssign(ss, m.newExpr(HDLOp.ADD, counter, 1));
		
		HDLUtils.generate(m, HDLUtils.VHDL);
		HDLUtils.generate(m, HDLUtils.Verilog);
	}
	
}
