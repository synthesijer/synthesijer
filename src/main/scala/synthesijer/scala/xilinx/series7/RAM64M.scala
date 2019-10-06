package synthesijer.scala.xilinx.series7

/**
 * @author miyo
 */

import synthesijer.scala._
import synthesijer.scala.xilinx._

import synthesijer.hdl.HDLSignal

class RAM64M extends CombinationLogic("RAM64M") {
  
  val DOA = outP("DOA")
  val DOB = outP("DOB")
  val DOC = outP("DOC")
  val DOD = outP("DOD")
  
  val DIA = inP("DIA")
  val DIB = inP("DIB")
  val DIC = inP("DIC")
  val DID = inP("DID")

  val ADDRA = inP("ADDRA", 6)
  val ADDRB = inP("ADDRB", 6)
  val ADDRC = inP("ADDRC", 6)
  val ADDRD = inP("ADDRD", 6)

  val WE   = inP("WE")
  val WCLK = inP("WCLK")
  
}

class RAM64M_test() extends Module("RAM64M_test", "clk", "reset") {
  val m = new RAM64M()
  val u = instance(m)
  u.signalFor(m.WCLK) := sysClk

  val DOA = outP("DOA"); DOA := u.signalFor(m.DOA);
  val DOB = outP("DOB"); DOB := u.signalFor(m.DOB);
  val DOC = outP("DOC"); DOC := u.signalFor(m.DOC);
  val DOD = outP("DOD"); DOD := u.signalFor(m.DOD);
  
  val DIA = outP("DIA"); u.signalFor(m.DIA) := DIA;
  val DIB = outP("DIB"); u.signalFor(m.DIB) := DIB;
  val DIC = outP("DIC"); u.signalFor(m.DIC) := DIC;
  val DID = outP("DID"); u.signalFor(m.DID) := DID;

  val ADDRA = inP("ADDRA", 6); u.signalFor(m.ADDRA) := ADDRA;
  val ADDRB = inP("ADDRB", 6); u.signalFor(m.ADDRB) := ADDRB;
  val ADDRC = inP("ADDRC", 6); u.signalFor(m.ADDRC) := ADDRC;
  val ADDRD = inP("ADDRD", 6); u.signalFor(m.ADDRD) := ADDRD;

  val WE   = inP("WE"); u.signalFor(m.WE) := WE;
}

object RAM64M {

  private val tmpl = new RAM64M()
  
  def main(args:Array[String]) = {
    val test = new RAM64M_test()
    Xilinx.init(test)
    test.genVHDL()
  }
  
}
