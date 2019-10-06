package synthesijer.scala

class SevenSeg(n:String,c:String,r:String) extends Module(n,c,r){
  
  def this() = this("sevenseg", "clk", "reset")
  
  val data = inP("data", 4)
  val segment = outP("segment", 7)
  
  val tbl = List((0, 0x7e), (1, 0x30), (2, 0x6d), (3, 0x79), (4, 0x33), (5, 0x5b), (6, 0x5f), (7, 0x70), (8, 0x7f), (9, 0x79))
  segment := decoder(data, tbl, segment.width())

}

class SevenSegSim(n:String) extends SimModule(n){
  
  def this() = this("sevenseg_sim")
  
  val (clk, reset, counter) = system(10)
  
  val m = new SevenSeg()
  val inst = instance(m, "U")
  inst.sysClk := clk;
  inst.sysReset := reset;
  
  val pat = List(0,1,2,3,4,5,6,7,8,9).map(a => (10*a+20, a))
  inst.signalFor(m.data) := decoder(counter, pat, m.data.width())
  
}

object SevenSeg{
  
  def main(args:Array[String]){
    val m = new SevenSeg()
    m.genVHDL();
    val sim = new SevenSegSim()
    sim.genVHDL();
  }
  
}