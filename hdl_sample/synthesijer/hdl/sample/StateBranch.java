package synthesijer.hdl.sample;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLPreDefinedConstant;

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
		s0.addStateTransit(newExpr(HDLOp.EQ, sel.getSignal(), HDLPreDefinedConstant.HIGH), s1);
		s0.addStateTransit(newExpr(HDLOp.EQ, sel.getSignal(), HDLPreDefinedConstant.LOW), s2);
		q.getSignal().setAssign(idle, HDLPreDefinedConstant.LOW);
		q.getSignal().setAssign(s1, HDLPreDefinedConstant.HIGH);
		q.getSignal().setAssign(s2, HDLPreDefinedConstant.LOW);
	}
	
	public static void main(String... args){
		StateBranch m = new StateBranch();
		BasicSim sim = new BasicSim(m, m.getName() + "_sim");
		
		HDLSignal sig = sim.getModuleInstances()[0].getSignalForPort("sel");
		sig.setAssign(null, HDLPreDefinedConstant.HIGH);
		
		HDLUtils.generate(m, HDLUtils.VHDL);
		HDLUtils.generate(sim, HDLUtils.VHDL);
		HDLUtils.generate(m, HDLUtils.Verilog);
		HDLUtils.generate(sim, HDLUtils.Verilog);
	}

}
