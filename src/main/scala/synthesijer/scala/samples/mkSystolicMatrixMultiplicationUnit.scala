import synthesijer.scala._

class mkSystolicMatrixMultiplicationUnit(val n:Int, val w:Int)
    extends Module("systolic_mmu_"+w+"_"+n+"_"+n, "clk", "reset"){

  val a_inputs = for(i <- 0 until n) yield inP("inA_" + i, w)
  val a_valids = for(i <- 0 until n) yield inP("validA_" + i)

  val b_inputs = for(i <- 0 until n) yield inP("inB_" + i, w)
  val b_valids = for(i <- 0 until n) yield inP("validB_" + i)

  val c_outputs = for(i <- 0 until n*n) yield outP("outC_" + i, 2*w)

  val done = outP("done")

  val cell = new mkSystolicMatrixMultiplicationCell(w)
  val PEs = for(i <- 0 until n*n) yield instance(cell, "U_"+i)

  for(r <- 0 until n){
    for(c <- 0 until n){
      val i = r*n+c
      // horizontal connections
      if(c == 0){ // from input
        PEs(i).signalFor(cell.ain) := a_inputs(r)
        PEs(i).signalFor(cell.ain_valid) := a_valids(r)
      }else{ // from the above cell
        PEs(i).signalFor(cell.ain) := PEs(i-1).signalFor(cell.aout)
        PEs(i).signalFor(cell.ain_valid) := PEs(i-1).signalFor(cell.aout_valid)
      }
      // vertical connections
      if(r == 0){ // from input
        PEs(i).signalFor(cell.bin) := b_inputs(c)
        PEs(i).signalFor(cell.bin_valid) := b_valids(c)
      }else{ // from the left cell
        PEs(i).signalFor(cell.bin) := PEs(i-n).signalFor(cell.bout)
        PEs(i).signalFor(cell.bin_valid) := PEs(i-n).signalFor(cell.bout_valid)
      }
      c_outputs(i) := PEs(i).signalFor(cell.cout)
    }
  }

  val pre_valid = signal("pre_valid")
  val pre_valid_reg = signal("pre_valid_reg")
  pre_valid := PEs(n*n-1).signalFor(cell.ain_valid) and PEs(n*n-1).signalFor(cell.bin_valid)
  pre_valid_reg $ sysClk := pre_valid
  done := ?(pre_valid == LOW and pre_valid_reg == HIGH, HIGH, LOW)

}

class mkSystolicMatrixMultiplicationCell(w:Int) extends Module("systolic_mmu_pe_"+w, "clk", "reset"){
  val ain = inP("inA", w); val ain_valid = inP("inA_valid")
  val bin = inP("inB", w); val bin_valid = inP("inB_valid")

  val aout = outP("outA", w); val aout_valid = outP("outA_valid")
  val bout = outP("outB", w); val bout_valid = outP("outB_valid")

  val cout = outP("outC", 2*w)

  aout $ sysClk := ain
  bout $ sysClk := bin
  aout_valid $ sysClk := ain_valid
  bout_valid $ sysClk := bin_valid

  val c = signal(2*w); cout := c
  val ab = signal(2*w); ab := ain * bin

  c $ sysClk := ?(sysReset == HIGH, value(0, 2*w),
                ?((ain_valid and bin_valid) == HIGH, c + ab,
                c))
}

class mkSystolicMatrixMultiplicationUnitSim(m:mkSystolicMatrixMultiplicationUnit) extends SimModule(m.name + "_sim")
{
  val (clk, reset, counter) = system(10)
  val inst = instance(m, "U")

  for(i <- 0 until m.n){
    inst.signalFor(m.a_inputs(i)) $ clk := ?(counter == value(10+i, 32), value(1+i, m.w),
                                           ?(counter == value(11+i, 32), value(2+i, m.w),
                                           ?(counter == value(12+i, 32), value(3+i, m.w),
                                           ?(counter == value(13+i, 32), value(4+i, m.w),
                                           value(0, m.w)))))
    inst.signalFor(m.a_valids(i)) $ clk := ?(counter == value(10+i, 32), HIGH,
                                           ?(counter == value(11+i, 32), HIGH,
                                           ?(counter == value(12+i, 32), HIGH,
                                           ?(counter == value(13+i, 32), HIGH,
                                           LOW))))
  }
  for(i <- 0 until m.n){
    inst.signalFor(m.b_inputs(i)) $ clk := ?(counter == value(10+i, 32), value(10+i, m.w),
                                           ?(counter == value(11+i, 32), value(20+i, m.w),
                                           ?(counter == value(12+i, 32), value(30+i, m.w),
                                           ?(counter == value(13+i, 32), value(40+i, m.w),
                                           value(0, m.w)))))
    inst.signalFor(m.b_valids(i)) $ clk := ?(counter == value(10+i, 32), HIGH,
                                           ?(counter == value(11+i, 32), HIGH,
                                           ?(counter == value(12+i, 32), HIGH,
                                           ?(counter == value(13+i, 32), HIGH,
                                           LOW))))
  }

}


object mkSystolicMatrixMultiplicationUnit{

  def main(args:Array[String]) = {
    val n = if(args.length > 0){ args(0).toInt } else { 4 }
    val width = if(args.length > 1){ args(2).toInt } else { 8 }
    println("mkSystolicMatirxMultiplicationUnit " + "N=" + n + ", width=" + width)
    val cell = new mkSystolicMatrixMultiplicationCell(width)
    val mmu = new mkSystolicMatrixMultiplicationUnit(n, width)
    cell.genVHDL()
    cell.genVerilog()
    mmu.genVHDL()
    mmu.genVerilog()
    val sim = new mkSystolicMatrixMultiplicationUnitSim(mmu)
    sim.genVHDL()
    sim.genVerilog()
  }

}
