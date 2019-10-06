package synthesijer.scala.xilinx.series7

/**
 * @author miyo
 */

import synthesijer.scala._
import synthesijer.scala.xilinx._

import synthesijer.hdl.HDLSignal

class SRLC32E extends CombinationLogic("SRLC32E") {
  
  val Q   = outP("Q")
  val Q31 = outP("Q31")

  val D = inP("D")
  val A = inP("A", 5)
  
  val CE  = inP("CE")
  val CLK = inP("CLK")
  
}

class SRLC32E_test() extends Module("SRLC32E_test", "clk", "reset") {
  val m = new SRLC32E()
  private val u = for(i <- 0 to 3) yield instance(m, "U_" + i)

  val Q0 = outP("Q0")
  val Q1 = outP("Q1")
  val Q2 = outP("Q2")
  val Q3 = outP("Q3")

  Q0 := u(0).signalFor(m.Q)
  Q1 := u(1).signalFor(m.Q)
  Q2 := u(2).signalFor(m.Q)
  Q3 := u(3).signalFor(m.Q)

  val CIN = outP("CIN")
  val COUT = outP("COUT")
  u(0).signalFor(m.D) := CIN
  u(1).signalFor(m.D) := u(0).signalFor(m.Q31)
  u(2).signalFor(m.D) := u(1).signalFor(m.Q31)
  u(3).signalFor(m.D) := u(2).signalFor(m.Q31)
  COUT := u(3).signalFor(m.Q31)

  val A0 = inP("A0", 5)
  val A1 = inP("A1", 5)
  val A2 = inP("A2", 5)
  val A3 = inP("A3", 5)

  u(0).signalFor(m.A) := A0
  u(1).signalFor(m.A) := A1
  u(2).signalFor(m.A) := A2
  u(3).signalFor(m.A) := A3

  val CE = inP("CE")
  for(i <- 0 to 3) {
    u(i).signalFor(m.CLK) := sysClk
    u(i).signalFor(m.CE) := CE
  }

}

object SRLC32E {

  private val tmpl = new SRLC32E()
  
  def main(args:Array[String]) = {
    val test = new SRLC32E_test()
    Xilinx.init(test)
    test.genVHDL()
  }
  
}
