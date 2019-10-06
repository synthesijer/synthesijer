/**
 * @author miyo
 */

import synthesijer.scala._
import synthesijer.scala.xilinx._
import synthesijer.scala.xilinx.series7._

class PlaElem extends Module("PlaElem", "clk", "reset"){

  Xilinx.init(this)

  private val ram64m_tmpl = new RAM64M()
  private val carry4_tmpl = new CARRY4()

  private val u_ram64m = instance(ram64m_tmpl)
  private val u_carry4 = instance(carry4_tmpl)

  u_ram64m.signalFor(ram64m_tmpl.WCLK) := sysClk
  val WE = inP("WE")
  u_ram64m.signalFor(ram64m_tmpl.WE) := WE

  u_carry4.signalFor(carry4_tmpl.DI) := value(0, 4)
  u_carry4.signalFor(carry4_tmpl.S) := u_ram64m.signalFor(ram64m_tmpl.DOD) &
                                       u_ram64m.signalFor(ram64m_tmpl.DOC) &
                                       u_ram64m.signalFor(ram64m_tmpl.DOB) &
                                       u_ram64m.signalFor(ram64m_tmpl.DOA)

  val DI = IndexedSeq(inP("DIA"), inP("DIB"), inP("DIC"), inP("DID"))
  u_ram64m.signalFor(ram64m_tmpl.DIA) := DI(0)
  u_ram64m.signalFor(ram64m_tmpl.DIB) := DI(1)
  u_ram64m.signalFor(ram64m_tmpl.DIC) := DI(2)
  u_ram64m.signalFor(ram64m_tmpl.DID) := DI(3)

  val ADDR = IndexedSeq(inP("ADDRA", 6), inP("ADDRB", 6), inP("ADDRC", 6), inP("ADDRD", 6))
  u_ram64m.signalFor(ram64m_tmpl.ADDRA) := ADDR(0);
  u_ram64m.signalFor(ram64m_tmpl.ADDRB) := ADDR(1);
  u_ram64m.signalFor(ram64m_tmpl.ADDRC) := ADDR(2);
  u_ram64m.signalFor(ram64m_tmpl.ADDRD) := ADDR(3);

  val O = outP("O", 4); O := u_carry4.signalFor(carry4_tmpl.O)
  val CO = outP("CO", 4); CO := u_carry4.signalFor(carry4_tmpl.CO)

  val CI = inP("CI"); u_carry4.signalFor(carry4_tmpl.CI) := CI
  val CYINIT = inP("CYINIT"); u_carry4.signalFor(carry4_tmpl.CYINIT) := CYINIT

}

object PlaElem{

  def main(args:Array[String]) = {
    val m = new PlaElem()
    m.genVHDL()
  }

}
