package synthesijer.scala.xilinx.series7

/**
 * @author miyo
 */

import synthesijer.scala._
import synthesijer.scala.xilinx._

import synthesijer.hdl.HDLSignal

class RAM64X1D extends CombinationLogic("RAM64X1D") {
  
  val SPO = outP("SPO")
  val DPO = outP("DPO")
  
  val WE   = inP("WE")
  val D    = inP("D")
  val WCLK = inP("WCLK")
  val A    = for(i <- 0 to 5) yield inP("A" + i)
  val DPRA = for(i <- 0 to 5) yield inP("DPRA" + i)
  
}

class RAM64X1D_test() extends Module("RAM64X1D_test", "clk", "reset") {
  val m = new RAM64X1D()
  val u = instance(m)
  u.signalFor(m.WCLK) := sysClk

  val SPO = outP("SPO")
  val DPO = outP("DPO")
  
  val WE   = inP("WE")
  val D    = inP("D")
  val WCLK = inP("WCLK")
  val A    = for(i <- 0 to 5) yield inP("A" + i)
  val DPRA = for(i <- 0 to 5) yield inP("DPRA" + i)

  SPO := u.signalFor(m.SPO)
  DPO := u.signalFor(m.DPO)

  u.signalFor(m.WE) := WE
  u.signalFor(m.D) := D
  for(i <- 0 to 5) {
    u.signalFor(m.A(i)) := A(i)
    u.signalFor(m.DPRA(i)) := DPRA(i)
  }

}

class RAM64X1D_sim(m:RAM64X1D) extends SimModule("RAM64X1D_sim") {

  val (clk,reset,counter) = system(10)
  val u = instance(m)

  u.signalFor(m.WCLK) := clk
  u.signalFor(m.A(0)) $ clk := ?(counter == 10, HIGH, LOW)
  u.signalFor(m.WE) $ clk   := ?(counter == 10, HIGH, LOW)
  u.signalFor(m.D) $ clk    := ?(counter == 10, HIGH, LOW)

  u.signalFor(m.DPRA(0)) := HIGH

}

object RAM64X1D {

  private val tmpl = new RAM64X1D()
  
  def main(args:Array[String]) = {
    val sim = new RAM64X1D_sim(tmpl)
    Xilinx.init(sim)
    tmpl.genVHDL()
    sim.genVHDL()
    val test = new RAM64X1D_test()
    test.genVHDL()
  }
  
}
