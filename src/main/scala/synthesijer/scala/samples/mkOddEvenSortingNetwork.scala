import synthesijer.scala._

class mkOddEvenSortingNetwork(
  n:String, val num:Int, width:Int, key:Int,
  odd_buf:Boolean, even_buf:Boolean, o_buf:Boolean, i_buf:Boolean) extends Module(n, "clk", "reset"){

  val inputs = for(i <- 0 until num) yield inP("input_" + i + "_p", width)
  val outputs = for(i <- 0 until num) yield outP("output_" + i + "_p", width)

  for(i <- 0 until outputs.length){
    outputs(i).reset(VECTOR_ZERO)
    outputs(i).default(VECTOR_ZERO)
  }

  private def compare_and_swap(x:ExprItem, y:ExprItem, buffer:Boolean):(ExprItem, ExprItem) = {
    val a, b = signal(width)
    val x_is_bigger_than_y = (x.range(width-1, width-key) > y.range(width-1, width-key))
    if(buffer){
      a $ sysClk := ?(x_is_bigger_than_y, y, x)
      b $ sysClk := ?(x_is_bigger_than_y, x, y)
    }else{
      a := ?(x_is_bigger_than_y, y, x)
      b := ?(x_is_bigger_than_y, x, y)
    }
    return (a, b)
  }

  private def even_merge(x:Seq[ExprItem], buffer:Boolean):Seq[ExprItem] = {
    val a = for(i <- 0 until num/2) yield compare_and_swap(x(2*i), x(2*i+1), buffer)
    return a.foldRight(Seq.empty[ExprItem])((n,z) => (n._1 +: (n._2 +: z)))
  }

  private def odd_merge(x:Seq[ExprItem], buffer:Boolean):Seq[ExprItem] = {
    val top, last = signal(width)
    if(odd_buf){
      top $ sysClk := x(0)
      last $ sysClk := x(num-1)
    }else{
      top := x(0)
      last := x(num-1)
    }
    val a = for(i <- 0 until (num/2-1)) yield compare_and_swap(x(2*i+1), x(2*i+2), buffer)
    val b = a.foldRight((last+:Seq.empty[ExprItem]))((n,z) => (n._1 +: (n._2 +: z)))
    return top +: b
  }

  private def merge(x:Seq[ExprItem], n:Int):Seq[ExprItem] = {
    if(n == 0){
      return x
    }else{
      if(n % 2 == 0){
        return merge(even_merge(x, even_buf), n-1)
      }else{
        return merge(odd_merge(x, odd_buf), n-1)
      }
    }
  }

  private def add_buffer_or_wire(x:Seq[ExprItem], buffer:Boolean):Seq[ExprItem] = {
    for(i <- 0 until num) yield {
      val s = signal(width)
      if(buffer){ s $ sysClk := x(i) }else{ s := x(i) }
      s
    }
  }

  val input0 = add_buffer_or_wire(inputs, i_buf)
  val result = merge(input0, num)
  val output0 = add_buffer_or_wire(result, o_buf)

  for(i <- 0 until outputs.length) outputs(i) := output0(i)

}

class mkOddEvenSortingNetworkSim(n:String, m:mkOddEvenSortingNetwork) extends SimModule(n){
  val (clk, reset, counter) = system(10)
  val inst = instance(m)
  for(i <- 0 until m.num){
    inst.signalFor(m.inputs(i)) $ clk := ?(counter == 10, value(10*(m.num-i), 32), value(0, 32));
  }

  inst.sysReset $ clk := ?(counter < 4, HIGH, LOW)
  inst.sysClk := clk
}

object mkOddEvenSortingNetwork{

  def gen(n:String, num:Int, w:Int, k:Int, odd_buf:Boolean, even_buf:Boolean, o_buf:Boolean, i_buf:Boolean) = {
    val m = new mkOddEvenSortingNetwork(n, num, w, k, odd_buf, even_buf, odd_buf, i_buf)
    m.genVHDL()
    m.genVerilog()
    val sim = new mkOddEvenSortingNetworkSim(m.getName() + "_sim", m)
    sim.genVHDL()
    sim.genVerilog()
  }

  def main(args:Array[String]) = {
    val base:String = "batcher_sorting_network"
    gen(base + "_f_f_f_f", 8, 32, 32, false, false, false, false)
    gen(base + "_f_f_f_t", 8, 32, 32, false, false, false, true)
    gen(base + "_f_f_t_f", 8, 32, 32, false, false, true,  false)
    gen(base + "_f_t_f_f", 8, 32, 32, false, true,  false, false)
    gen(base + "_t_f_f_f", 8, 32, 32, true,  false, false, false)
  }

}
