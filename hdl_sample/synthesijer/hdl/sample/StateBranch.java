package synthesijer.hdl.sample;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLConstant;

public class StateBranch extends HDLModule{
	
	public StateBranch(){
		super("state_branch", "clk", "reset");
		HDLPort sel = newPort("sel", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		HDLPort q = newPort("q", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		HDLSequencer s = newSequencer("main");
		HDLSequencer.SequencerState idle = s.getIdleState();
		HDLSequencer.SequencerState s0 = s.addSequencerState("s0");
		idle.addStateTransit(s0);
		HDLSequencer.SequencerState s1 = s.addSequencerState("s1");
		HDLSequencer.SequencerState s2 = s.addSequencerState("s2");
		s0.addStateTransit(newExpr(HDLOp.EQ, sel.getSignal(), HDLConstant.HIGH), s1);
		s0.addStateTransit(newExpr(HDLOp.EQ, sel.getSignal(), HDLConstant.LOW), s2);
		q.getSignal().setAssign(idle, HDLConstant.LOW);
		q.getSignal().setAssign(s1, HDLConstant.HIGH);
		q.getSignal().setAssign(s2, HDLConstant.LOW);
	}
	
	public static void main(String... args){
		StateBranch m = new StateBranch();
		BasicSim sim = new BasicSim(m, m.getName() + "_sim");
		HDLUtils.generate(m, HDLUtils.VHDL);
		HDLUtils.generate(sim, HDLUtils.VHDL);
		HDLUtils.generate(m, HDLUtils.Verilog);
		HDLUtils.generate(sim, HDLUtils.Verilog);
	}

}
