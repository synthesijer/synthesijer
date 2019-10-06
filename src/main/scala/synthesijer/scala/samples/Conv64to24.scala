package synthesijer.scala

import synthesijer.scala._

class Conv64to24(n:String, c:String, r:String) extends Module(n, c, r){
  
  def this() = this("conv64to24", "clk", "reset")
  
  val recv = new FIFO_IN(this, "fifo64", 64)
  val send = new FIFO_OUT(this, "fifo24", 24)
  
  val reg = signal(64) 
  val seq = sequencer("main")
  
  val KICK_THREASHOLD = 100
  
  val start = seq.idle -> (recv.count > KICK_THREASHOLD, seq.add())
  
  recv.re.default(LOW)
  send.we.default(LOW)
  
  // (0) 64bitFIFO.re <= '1'
  val s0 = start -> seq.add()
  recv.re <= (s0, HIGH)
  
  // (1) 64bitFIFO.re <= '0'
  val s1 = s0 -> seq.add()
  recv.re <= (s1, LOW)
  
  // (2) 24bitFIFO <- 64bitFIFO(23:0) // (23:0)
  //     reg <- 64bitFIFO(63:24)
  //     64bitFIFO.re <= '1'
  val s2 = s1 -> seq.add()
  send.we <= (s2, HIGH)
  send.dout <= (s2, range(recv.din, 23, 0))
  reg <= (s2, padding0(range(recv.din, 63, 24), 24))
  recv.re <= (s2, HIGH)
  
  // (3) 24bitFIFO <- reg(23:0) // (47:24)
  //     reg <- reg >> 24 // remained (63:48)
  //     64bitFIFO.re <= '0'
  val s3 = s2 -> seq.add()
  send.we <= (s3, HIGH)
  send.dout <= (s3, range(reg, 23, 0))
  reg <= (s3, reg >>> 24)
  recv.re <= (s3, LOW)
  
  // (4) 24bitFIFO <- 64bitFIFO(7:0)&reg(15:0) // (7:0)&(63:48)
  //     reg <- 64bitFIFO(63:8)
  val s4 = s3 -> seq.add()
  send.we <= (s4, HIGH)
  send.dout <= (s4, range(recv.din, 7, 0) & range(reg, 15, 0))
  reg <= (s4, padding0(range(recv.din, 63, 8), 8))
  
  // (5) 24bitFIFO <- reg(23:0) // (31:8)
  //     reg <- reg >> 24 // remaiend (63:32)
  //     64bitFIFO.re <= '1'
  val s5 = s4 -> seq.add()
  send.we <= (s5, HIGH)
  send.dout <= (s5, range(reg, 23, 0))
  reg <= (s5, reg >>> 24)
  recv.re <= (s5, HIGH)
  
  // (6) 24bitFIFO <- reg(23:0) // (55:32)
  //     reg <- reg >> 24 // remaind (63:56)
  //     64bitFIFO.re <= '0'
  val s6 = s5 -> seq.add()
  send.we <= (s6, HIGH)
  send.dout <= (s6, range(reg, 23, 0))
  reg <= (s6, reg >>> 24)
  recv.re <= (s6, LOW)
  
  // (7) 24bitFIFO <- 64bitFIFO(15:0)&reg(7:0) // (15:0)&(63:56)
  //     reg <- 64bitFIFO(63:16) // remained (63:16)
  val s7 = s6 -> seq.add()
  send.we <= (s7, HIGH)
  send.dout <= (s7, range(recv.din, 15, 0) & range(reg, 7, 0))
  reg <= (s7, padding0(range(recv.din, 63, 16), 16))
  
  // (8) 24bitFIFO <- reg(23:0) // (39:16)
  //     reg <- reg >> 24 // remained (63:40)
  //     64bitFIFO.re <= '1'
  val s8 = s7 -> seq.add()
  send.we <= (s8, HIGH)
  send.dout <= (s8, range(reg, 23, 0))
  reg <= (s8, reg >>> 24)
  recv.re <= (s8, HIGH)
  
  // (9) 24bitFIFO <- reg(23:0) // (39:16)
  //     64bitFIFO.re <= '0'
  //     goto (2)
  val s9 = s8 -> seq.add()
  send.we <= (s9, HIGH)
  send.dout <= (s9, range(reg, 23, 0))
  recv.re <= (s8, LOW)
  
  val s10 = s9 -> seq.add()
  send.we <= (s10, LOW)
  s10 -> ((!send.full) and (recv.count > KICK_THREASHOLD), s2)
  
}

class Conv64to24Sim(n:String) extends SimModule(n) {
  
  def this() = this("conv64to24sim")
  
  val (clk, reset, count) = system(10)
  
  val m = new Conv64to24()
  val inst = instance(m, "U")
  inst.sysClk := clk
  inst.sysReset := reset
  
  val sig = signal("fe", 64)
  sig := value(0x0123456789abcdefL, 64)
  
  inst.signalFor(m.recv.count) := value(20, 32)
  inst.signalFor(m.recv.din) := value(0x0123456789abcdefL, 64)
  inst.signalFor(m.send.full) := LOW
  
}

object Conv64to24{
  
  def main(args:Array[String]) = {
    val m = new Conv64to24
    m.visualize_statemachine()
    m.genVHDL()
    val sim = new Conv64to24Sim()
    sim.genVHDL()
  }
  
}

