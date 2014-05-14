import synthesijer.hdl._
import synthesijer.hdl.expr._

class led(name:String, sysClkName:String, sysRsetName:String) extends HDLModule(name, sysClkName, sysRsetName){

  def this() = this("led", "clk", "reset");

  val q = newPort("q", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());

  // q <= counter(24)
  val sig = q.getSignal();
  val counter = newSignal("counter", HDLPrimitiveType.genSignedType(32));
  sig.setAssign(null, newExpr(HDLOp.REF, counter, 5));
		
  // at main state, counter <= counter + 1
  val seq = newSequencer("main");
  val ss = seq.getIdleState();
  counter.setAssign(ss, newExpr(HDLOp.ADD, counter, 1));

}

class led_sim(led:HDLModule, name:String) extends HDLSimModule(name){

  def this(led:HDLModule) = this(led, "led_sim");
  val inst = newModuleInstance(led, "U");

  val clk = newSignal("clk", HDLPrimitiveType.genBitType());
  val reset = newSignal("reset", HDLPrimitiveType.genBitType());
  val counter = newSignal("counter", HDLPrimitiveType.genSignedType(32));

  val seq = newSequencer("main");
  seq.setTransitionTime(10);

  val ss = seq.getIdleState();
  val s0 = seq.addSequencerState("S0");
  ss.addStateTransit(s0);
  s0.addStateTransit(ss);

  clk.setAssign(ss, HDLConstant.LOW);
  clk.setAssign(s0, HDLConstant.HIGH);

  val expr = newExpr(HDLOp.ADD, counter, 1);
  counter.setAssign(ss, expr);
  counter.setAssign(s0, expr);

  reset.setResetValue(HDLConstant.LOW);
  reset.setAssign(ss, newExpr(HDLOp.IF, newExpr(HDLOp.AND, newExpr(HDLOp.GT, counter, 3), newExpr(HDLOp.LT, counter, 8)), HDLConstant.HIGH, HDLConstant.LOW));

  inst.getSignalForPort("clk").setAssign(null, clk);
  inst.getSignalForPort("reset").setAssign(null, reset);
}

object Main{
  def main(args: Array[String]) {
    val m = new led();
    HDLUtils.generate(m, HDLUtils.VHDL);
    HDLUtils.generate(m, HDLUtils.Verilog);

    val sim = new led_sim(m);
    HDLUtils.generate(sim, HDLUtils.VHDL);
    HDLUtils.generate(sim, HDLUtils.Verilog);
  }
}
