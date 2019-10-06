package synthesijer.scala.xilinx.series7

import synthesijer.scala._

/**
 * size: 36 or 18
 */
class BRAM_SDP(size:Int, width:Int) extends CombinationLogic("BRAM_SDP_MACRO"){
  
	component_required(false)
  
	val (depth, we_width) = 
			((s:Int, w:Int) => {
				s match {
				case 36 =>
				w match {
				  case x if x == 1 => (15, 1)
				  case x if x == 2 => (14, 1)
				  case x if x == 3 || x == 4 => (13, 1)
				  case x if 5 <= x && x <= 9 => (12, 1)
          case x if 10 <= x && x <= 18 => (11, 2)
          case x if 19 <= x && x <= 36 => (10, 4)
          case x if 37 <= x && x <= 72 => (9, 8)
          case _ => (0, 0)
        }
        case 18 =>
        width match {
          case x if x == 1 => (14, 1)
          case x if x == 2 => (13, 1)
          case x if x == 3 || x == 4 => (12, 1)
          case x if 5 <= x && x <= 9 => (11, 1)
          case x if 10 <= x && x <= 18 => (10, 2)
          case x if 19 <= x && x <= 36 => (9, 2)
          case _ => (0, 0)
        }
        case _ => (0, 0)
				}
			})(size, width)
    
  def is_valid = depth > 0
  
	val DO = outP("DO", width)
  val DI = inP("DI", width)
  
  val WADDR = inP("WADDR", depth)
  val RADDR = inP("RADDR", depth)
  
  val WE = inP("WE", we_width)
  
  val WREN = inP("WREN")
  val RDEN = inP("RDEN")
  
  val SSR = inP("SSR")
  
  val REGCE = inP("REGCE")
  
  val WRCLK = inP("WRCLK")
  val RDCLK = inP("RDCLK")
  
  parameter("BRAM_SIZE", if(size == 36){"36Kb"}else{"18Kb"})
  parameter("DEVICE", "7SERIES")
  parameter("DO_REG", 0)
  parameter("INIT", value(0, 72))
  parameter("READ_WIDTH", width)
  parameter("READ_WIDTH", width)
  parameter("SIM_COLLISION_CHECK", "ALL")
  parameter("SRVAL", value(0, 72))
    
}

object BRAM_SDP{

	def mk(m:Module, name:String, size:Int, width:Int):Option[Instance] = {
    m.add_library("UNISIM", "UNISIM.vcomponents.all")
    m.add_library("UNIMACRO", "unimacro.Vcomponents.all")
  
    val u = new BRAM_SDP(size, width)
    if(u.is_valid){ 
    	Some(m.instance(u, name))
    }else{
      None
    }
  }
  
  def main(args:Array[String]) = {
    val m = new Module("bram_sdp_test", "clk", "reset")
    mk(m, "U", 36, 32)
    m.genVHDL()
    m.genVerilog()
  }
  
}
