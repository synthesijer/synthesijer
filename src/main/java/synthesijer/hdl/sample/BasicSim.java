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
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.hdl.sequencer.SequencerState;

public class BasicSim extends HDLSimModule{

	protected HDLInstance inst;
	protected HDLSignal counter;
	protected SequencerState ss;

	public BasicSim(HDLModule target, String name) {
		super(name);

		HDLSignal clk = newSignal("clk", HDLPrimitiveType.genBitType());
		HDLSignal reset = newSignal("reset", HDLPrimitiveType.genBitType());
		counter = newSignal("counter", HDLPrimitiveType.genSignedType(32));
		setSysClk(clk);
		setSysReset(reset);

		HDLSequencer seq = newSequencer("main");
		seq.setTransitionTime(10);

		ss = seq.getIdleState();
		SequencerState s0 = seq.addSequencerState("S0");
		ss.addStateTransit(s0);
		s0.addStateTransit(ss);

		clk.setAssign(ss, HDLPreDefinedConstant.LOW);
		clk.setAssign(s0, HDLPreDefinedConstant.HIGH);

		HDLExpr expr = newExpr(HDLOp.ADD, counter, 1);
		//counter.setAssign(ss, expr);
		counter.setResetValue(HDLPreDefinedConstant.VECTOR_ZERO);
		counter.setAssign(s0, expr);

		reset.setResetValue(HDLPreDefinedConstant.LOW);
		reset.setAssign(ss, newExpr(HDLOp.IF, during(0, 8), HDLPreDefinedConstant.HIGH, HDLPreDefinedConstant.LOW));

		if(target != null){
			inst = newModuleInstance(target, "U");
			inst.getSignalForPort(target.getSysClkName()).setAssign(null, clk);
			inst.getSignalForPort(target.getSysResetName()).setAssign(null, reset);
		}

	}

	protected HDLExpr after(int value){
		return newExpr(HDLOp.GT, counter, value);
	}

	protected HDLExpr at(int value){
		return newExpr(HDLOp.EQ, counter, value);
	}

	protected HDLExpr during(int value0, int value1){
		return newExpr(HDLOp.AND, newExpr(HDLOp.GEQ, counter, value0), newExpr(HDLOp.LEQ, counter, value1));
	}

	protected HDLExpr delayPulse(SequencerState s, String name, HDLSignal flag, int value){
		HDLSignal sig = newSignal(name, HDLPrimitiveType.genSignedType(32));
		sig.setAssign(s, newExpr(HDLOp.IF, newExpr(HDLOp.AND, flag, newExpr(HDLOp.EQ, sig, 0)),
				new HDLValue(String.valueOf(value), HDLPrimitiveType.genSignedType(32)),
				newExpr(HDLOp.IF, newExpr(HDLOp.GT, sig, 0), newExpr(HDLOp.SUB, sig, 1), HDLPreDefinedConstant.VECTOR_ZERO)));
		return newExpr(HDLOp.GT, sig, 0);
	}


	public static void main(String... args){
		BasicSim sim = new BasicSim(null, "sim");
		HDLUtils.generate(sim, HDLUtils.VHDL);
		HDLUtils.generate(sim, HDLUtils.Verilog);
	}


}
