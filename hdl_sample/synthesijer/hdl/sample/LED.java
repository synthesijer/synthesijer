package synthesijer.hdl.sample;

import java.io.IOException;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLSimModule;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLConstant;

public class LED {
	
	private static HDLModule genLED(){
		HDLModule m = new HDLModule("led", "clk", "reset");
		HDLPort q = m.newPort("q", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		
		// q <= counter(24)
		HDLSignal sig = q.getSrcSignal();
		HDLSignal counter = m.newSignal("counter", HDLPrimitiveType.genSignedType(32));
		sig.setAssign(null, m.newExpr(HDLOp.REF, counter, 5));
		
		// at main state, counter <= counter + 1
		HDLSequencer seq = m.newSequencer("main");
		HDLSequencer.SequencerState ss = seq.getIdleState();
		counter.setAssign(ss, m.newExpr(HDLOp.ADD, counter, 1));
		
		HDLUtils.generate(m, HDLUtils.VHDL);
		HDLUtils.generate(m, HDLUtils.Verilog);
		return m;
	}

	public static void main(String... args) throws IOException{
		
		HDLModule led = genLED();
		
		HDLSimModule m = new HDLSimModule("led_sim");
		HDLInstance inst = m.newModuleInstance(led, "U");
		HDLSignal clk = m.newSignal("clk", HDLPrimitiveType.genBitType());
		HDLSignal reset = m.newSignal("reset", HDLPrimitiveType.genBitType());
		HDLSignal counter = m.newSignal("counter", HDLPrimitiveType.genSignedType(32));
		
		HDLSequencer seq = m.newSequencer("main");
		seq.setTransitionTime(10);

		HDLSequencer.SequencerState ss = seq.getIdleState();
		HDLSequencer.SequencerState s0 = seq.addSequencerState("S0");
		ss.addStateTransit(s0);
		s0.addStateTransit(ss);
		
		clk.setAssign(ss, HDLConstant.LOW);
		clk.setAssign(s0, HDLConstant.HIGH);
		
		HDLExpr expr = m.newExpr(HDLOp.ADD, counter, 1);
		counter.setAssign(ss, expr);
		counter.setAssign(s0, expr);
		
		reset.setResetValue(HDLConstant.LOW);
		reset.setAssign(ss, m.newExpr(HDLOp.IF, m.newExpr(HDLOp.AND, m.newExpr(HDLOp.GT, counter, 3), m.newExpr(HDLOp.LT, counter, 8)), HDLConstant.HIGH, HDLConstant.LOW));
		
		inst.getSignalForPort("clk").setAssign(null, clk);
		inst.getSignalForPort("reset").setAssign(null, reset);
		
		HDLUtils.generate(m, HDLUtils.VHDL);
		HDLUtils.generate(m, HDLUtils.Verilog);
				
	}
	
}
