package synthesijer.scala.xilinx.series7

/**
 * @author miyo
 */

import synthesijer.scala._
import synthesijer.scala.xilinx._

import synthesijer.hdl.HDLSignal

class RAM64X1S extends CombinationLogic("RAM64X1S") {
  
  val O = outP("O")
  val A = for(i <- 0 to 5) yield inP("A" + i)
  
  val D    = inP("D")
  val WCLK = inP("WCLK")
  val WE   = inP("WE")
  
}

class RAM64X1S_test() extends Module("RAM64X1S_test", "clk", "reset") {
  val m = new RAM64X1S()
  val u = instance(m)
  u.signalFor(m.WCLK) := sysClk

  val O = outP("O")
  val A    = for(i <- 0 to 5) yield inP("A" + i)
  
  val D    = inP("D")
  val WE   = inP("WE")

  O := u.signalFor(m.O)

  u.signalFor(m.WE) := WE
  u.signalFor(m.D) := D
  for(i <- 0 to 5) {
    u.signalFor(m.A(i)) := A(i)
  }

}

object RAM64X1S {

  private val tmpl = new RAM64X1S()
  
  def main(args:Array[String]) = {
    val test = new RAM64X1S_test()
    Xilinx.init(test)
    test.genVHDL()
  }
  
}
