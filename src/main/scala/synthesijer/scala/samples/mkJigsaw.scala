import synthesijer.scala._

class mkJigsaw3to2(val w:Int, val h:Int, n:String) extends Module(n, "clk", "reset"){
  val din = inP("din", h * 2)
  val en = inP("en")

  val dout = outP("dout", w)
  val valid = outP("valid"); valid.default(LOW)

  val ZERO_P  = signal("ZERO_P", 2);  ZERO_P  := value(0, 2)
  val ONE_P   = signal("ONE_P", 2);   ONE_P   := value(1, 2)
  val TWO_P   = signal("TWO_P", 2);   TWO_P   := value(2, 2)
  val UNKNOWN = signal("UNKNOWN", 2); UNKNOWN := value(3, 2)

  val din_reg = signal(h*2)

  // initialization of each piece (lldd)
  val map = for(i <- 0 until w * h; val s = signal("signal_" + i, 4)) yield s

  // return new piece expr(lldd)
  def next_piece(u:ExprItem, r:ExprItem) = {
    ?(u == ZERO_P and r == ZERO_P, ZERO_P & ZERO_P,
    ?(u == ZERO_P and r == ONE_P,  ZERO_P & ONE_P,
    ?(u == ZERO_P and r == TWO_P,  ONE_P  & ZERO_P,
    ?(u == ONE_P  and r == ZERO_P, ONE_P  & ONE_P,
    ?(u == ONE_P  and r == ONE_P,  TWO_P  & ZERO_P,
    ?(u == ONE_P  and r == TWO_P,  TWO_P  & ONE_P,
    UNKNOWN & UNKNOWN))))))
  }
  
  // stop condition
  var stop_flag:ExprItem = HIGH
  for(i <- 0 until h) {
    val e = ?(map((i+1)*w-1).range(3, 2) == ZERO_P, HIGH, LOW)
    stop_flag = stop_flag and e
  }

  // result
  var result:ExprItem = ?(map(w*(h-1)).range(1, 0) == ZERO_P, value(0,1), value(1,1))
  for(i <- 1 until w) {
    val e = ?(map(w*(h-1)+i).range(1, 0) == ZERO_P, value(0,1), value(1,1))
    result = e & result
  }

}

class mkJigsaw3to2_FF(w:Int, h:Int) extends mkJigsaw3to2(w, h, "jigsaw3to2_ff"){

  for(s <- map) s.reset(value(0xf,4))

  val seq = sequencer("main")
  val s0 = seq.idle * en -> seq.add()
  din_reg <= seq.idle * ?(en, din, value(-1, h*2))

  for(i <- 0 until w * h){
    if(i == 0){
      val r = din_reg.range(2*h-1, 2*h-1-1) // input[0]
      map(i) <= s0 * next_piece(ZERO_P, r)
    }else if(i%w == 0){ // right edge
      val r = din_reg.range(2*h-1-2*(i/w), 2*h-1-2*(i/w)-1) // input[i/w]
      val u = map(i-w).range(1, 0)  // dd of the upper piece
      map(i) <= s0 * next_piece(u, r)
    }else if(i/w == 0){ // upper edge
      val r = map(i-1).range(3, 2) // ll of the right piece
      map(i) <= s0 * next_piece(ZERO_P, r)
    }else{
      val u = map(i-w).range(1, 0) // dd of the upper piece
      val r = map(i-1).range(3, 2) // ll of the right piece
      map(i) <= s0 * next_piece(u, r)
    }
  }
  s0 * (stop_flag == HIGH) -> seq.idle

  valid <= s0 * stop_flag
  dout <= s0 * result
}

class mkJigsaw3to2_Expr(w:Int, h:Int) extends mkJigsaw3to2(w, h, "jigsaw3to2_expr"){

  din_reg := ?(en, din, value(-1, h*2))

  for(i <- 0 until w * h){
    if(i == 0){
      val r = din_reg.range(2*h-1, 2*h-1-1) // input[0]
      map(i) := next_piece(ZERO_P, r)
    }else if(i%w == 0){ // right edge
      val r = din_reg.range(2*h-1-2*(i/w), 2*h-1-2*(i/w)-1) // input[i/w]
      val u = map(i-w).range(1, 0)  // dd of the upper piece
      map(i) := next_piece(u, r)
    }else if(i/w == 0){ // upper edge
      val r = map(i-1).range(3, 2) // ll of the right piece
      map(i) := next_piece(ZERO_P, r)
    }else{
      val u = map(i-w).range(1, 0) // dd of the upper piece
      val r = map(i-1).range(3, 2) // ll of the right piece
      map(i) := next_piece(u, r)
    }
  }

  valid $ sysClk := stop_flag
  dout $ sysClk := result

}

class mkJigsaw3to2_Sim(m:mkJigsaw3to2, n:String) extends SimModule(n){

  def this(m:mkJigsaw3to2) = this(m, m.name + "_sim")
  
  val (clk, reset, counter) = system(10)
  
  val inst = instance(m, "U")
  inst.sysClk := clk
  inst.sysReset := reset
  
  inst.signalFor(m.en) $ clk := ?(counter == 100, HIGH, LOW)
  // 201 -> 10_00_01 -> 0x21
  inst.signalFor(m.din) $ clk := ?(counter == 100, value(0x21, m.h * 2), VECTOR_ZERO)
  
}


object mkJigsaw {

  def main(args:Array[String]) = {
    {
      val m = new mkJigsaw3to2_FF(6, 4)
      val sim = new mkJigsaw3to2_Sim(m)
      m.genVHDL()
      m.genVerilog()
      sim.genVHDL()
      sim.genVerilog()
    }

    {
      val m = new mkJigsaw3to2_Expr(6, 4)
      val sim = new mkJigsaw3to2_Sim(m)
      m.genVHDL()
      m.genVerilog()
      sim.genVHDL()
      sim.genVerilog()
    }

  }

}
