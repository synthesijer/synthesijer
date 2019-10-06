package synthesijer.scala

class BRAM(m:Module, prefix:String, width:Int){
	val address = m.outP(prefix + "_address", 32)
  val we = m.outP(prefix + "_we")
  val din = m.inP(prefix  + "_din", width)
  val dout = m.outP(prefix + "_dout", width)
}

class FIFO_OUT(m:Module, prefix:String, width:Int){
  val we = m.outP(prefix + "_we")
  val dout = m.outP(prefix + "_dout", width)
  val full = m.inP(prefix + "_full")
  val count = m.inP(prefix + "_wr_count", 32)
}

class FIFO_IN(m:Module, prefix:String, width:Int){
  val re = m.outP(prefix + "_re")
  val din = m.inP(prefix + "_din", width)
  val empty = m.inP(prefix + "_empty")
  val count = m.inP(prefix + "_rd_count", 32)
}
