package synthesijer.hdl.sample;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLSimModule;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLConstant;

class BasicSim extends HDLSimModule{
	
	public BasicSim(HDLModule target, String name) {
		super(name);
	
		HDLSignal clk = newSignal("clk", HDLPrimitiveType.genBitType());
		HDLSignal reset = newSignal("reset", HDLPrimitiveType.genBitType());
		HDLSignal counter = newSignal("counter", HDLPrimitiveType.genSignedType(32));
	
		HDLSequencer seq = newSequencer("main");
		seq.setTransitionTime(10);
		
		HDLSequencer.SequencerState ss = seq.getIdleState();
		HDLSequencer.SequencerState s0 = seq.addSequencerState("S0");
		ss.addStateTransit(s0);
		s0.addStateTransit(ss);
		
		clk.setAssign(ss, HDLConstant.LOW);
		clk.setAssign(s0, HDLConstant.HIGH);
		
		HDLExpr expr = newExpr(HDLOp.ADD, counter, 1);
		counter.setAssign(ss, expr);
		counter.setAssign(s0, expr);
		
		reset.setResetValue(HDLConstant.LOW);
		reset.setAssign(ss, newExpr(HDLOp.IF, newExpr(HDLOp.AND, newExpr(HDLOp.GT, counter, 3), newExpr(HDLOp.LT, counter, 8)), HDLConstant.HIGH, HDLConstant.LOW));
		
		if(target != null){
			HDLInstance inst = newModuleInstance(target, "U");
			inst.getSignalForPort("clk").setAssign(null, clk);
			inst.getSignalForPort("reset").setAssign(null, reset);
		}
		
		HDLUtils.generate(this, HDLUtils.VHDL);
		HDLUtils.generate(this, HDLUtils.Verilog);
	}
	
	public static void main(String[] args){
		new BasicSim(null, "sim");
	}
	
			
}
