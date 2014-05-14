import synthesijer.hdl._
import synthesijer.hdl.expr._

object led extends HDLmodule{

  def gen_led(): HDLModule = {
    val m = new HDLModule("led", "clk", "reset");
    val q = m.newPort("q", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());

    // q <= counter(24)
    val sig = q.getSrcSignal();
    val counter = m.newSignal("counter", HDLPrimitiveType.genSignedType(32));
    sig.setAssign(null, m.newExpr(HDLOp.REF, counter, 5));
		
    // at main state, counter <= counter + 1
    val seq = m.newSequencer("main");
    val ss = seq.getIdleState();
    counter.setAssign(ss, m.newExpr(HDLOp.ADD, counter, 1));
		
    HDLUtils.generate(m, HDLUtils.VHDL);
    HDLUtils.generate(m, HDLUtils.Verilog);

    return m;
  }

  def gen_sim(led: HDLModule): HDLModule = {
    val m = new HDLSimModule("led_sim");
    val inst = m.newModuleInstance(led, "U");

    val clk = m.newSignal("clk", HDLPrimitiveType.genBitType());
    val reset = m.newSignal("reset", HDLPrimitiveType.genBitType());
    val counter = m.newSignal("counter", HDLPrimitiveType.genSignedType(32));

    val seq = m.newSequencer("main");
    seq.setTransitionTime(10);

    val ss = seq.getIdleState();
    val s0 = seq.addSequencerState("S0");
    ss.addStateTransit(s0);
    s0.addStateTransit(ss);

    clk.setAssign(ss, HDLConstant.LOW);
    clk.setAssign(s0, HDLConstant.HIGH);

    val expr = m.newExpr(HDLOp.ADD, counter, 1);
    counter.setAssign(ss, expr);
    counter.setAssign(s0, expr);

    reset.setResetValue(HDLConstant.LOW);
    reset.setAssign(ss, m.newExpr(HDLOp.IF, m.newExpr(HDLOp.AND, m.newExpr(HDLOp.GT, counter, 3), m.newExpr(HDLOp.LT, counter, 8)), HDLConstant.HIGH, HDLConstant.LOW));

    inst.getSignalForPort("clk").setAssign(null, clk);
    inst.getSignalForPort("reset").setAssign(null, reset);

    HDLUtils.generate(m, HDLUtils.VHDL);
    HDLUtils.generate(m, HDLUtils.Verilog);

    return m;
  }

  def main(args: Array[String]) {
    val led = gen_led();
    gen_sim(led);
  }
}
