package synthesijer.scala.xilinx

import synthesijer.scala._

object Xilinx {

  def init(m:ModuleFunc) = {
    m.add_library("UNISIM", "UNISIM.vcomponents.all")
    m.add_library("UNIMACRO", "unimacro.Vcomponents.all")
  }

}
