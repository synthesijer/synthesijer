package synthesijer.scala

class UPLIn(val m:Module, prefix:String, val width:Int){
	val req = m.inP(prefix  + "Req")
  val en = m.inP(prefix  + "En")
  val ack = m.outP(prefix + "Ack")
  val data = m.inP(prefix  + "Data", width)
  
  ack.default(m.LOW)
  
  def := (u:UPLIn) : Unit = {
    u.req := req
    u.en := en
    ack := u.ack
    u.data := data
  }
  
  // !!! not yet completed
  def templateRecv(seq:Sequencer, t:Array[ExprDestination]):(State,State) = {
	  val entry = seq.add()
	  val exit = seq.add()
	  
	  ack <= (entry, m.HIGH)
	  val s0 = entry -> (en, seq.add)
	  	  
	  var s = s0
	  var i = 0
	  for(item <- t){
		  item <= (s, i, data)
			if(s == s0){
			  s = s0 -> seq.add()
			}else{
				i = i + 1
				s.max_delay(i)
			}
	  }
	  
	  s -> (en!, exit)
	  
	  return (entry, exit)
	}
  
}

class UPLOut(m:Module, prefix:String, width:Int){
	val req = m.outP(prefix + "Req")
  val en = m.outP(prefix + "En")
  val ack = m.inP(prefix  + "Ack")
  val data = m.outP(prefix + "Data", width)
  
  def := (u:UPLOut) : Unit = {
    req := u.req
    en := u.en
    u.ack := ack 
    data := u.data 
  }

}


object UPLIn {
  
  def main(args:Array[String]) = {
    val m = new Module("uplin_recv", "clk", "reset")
	  val u = new UPLIn(m, "UPL_", 32)

    var lst = for{
      i <- 0 to 10
      v = m.signal(32)
    }yield(v)
      
    val seq = m.sequencer("recv_test")
    val (entry, exit) = u.templateRecv(seq, lst.toArray)
    seq.idle -> entry
    exit -> seq.idle
    
    m.genVHDL()
    
  }
  
}