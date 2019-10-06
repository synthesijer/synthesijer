package synthesijer.scala

class XORSHIFT32(n:String,c:String,r:String) extends Module(n,c,r){
  
  def this() = this("xorshift32", "clk", "reset")
	
  val q = outP("q", 32)
  val y = signal(64)
  val y0 = signal(64)
  val y1 = signal(64)
  val y2 = signal(64)
  
  q := range(y2, 31, 0)
  
  val m = sequencer("main")
  y.reset(value(2463534242L, 64))
  y <= (m.idle, value(2463534242L, 64))
  
  y0 := (y xor (y << 13)) // y ^= (y << 13)
  y1 := (y0 >>> 17) // y = (y >> 17)
  y2 := (y1 xor (y1 << 5)) // y ^= (y << 5)
  
  val s0 = m.idle -> m.add()
  q <= (s0, y2)
  y <= (s0, y2)

}

class XORSHIFT32Sim(name:String, target:XORSHIFT32) extends SimModule(name){
  
	def this(target:XORSHIFT32) = this("xorshift32_sim", target)
	val (clk, reset, _) = system(10);

	val inst = instance(target, "U")
	inst.sysClk := clk
	inst.sysReset := reset

}

object XORSHIFT32{
  def main(args:Array[String]) = {
    val m = new XORSHIFT32()
    val s = new XORSHIFT32Sim(m)
    m.genVHDL()
    s.genVHDL()
    m.genComponentXML()
  }
}
