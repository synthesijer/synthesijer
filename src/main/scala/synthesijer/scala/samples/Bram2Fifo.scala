package synthesijer.scala

import synthesijer.scala._
import synthesijer.lib.SimpleBlockRAM

class Bram2Fifo(n:String, c:String, r:String, words:Int, width:Int) extends Module(n,c,r){
  
  def this(words:Int, width:Int) = this("bram2fifo_" + words + "_" + width, "clk", "reset", words, width)

  val init = inP("init")
  val kick = inP("kick")
  val busy = outP("busy")
  val test = inP("test")
  val offset = inP("offset", 32)
  
  val bram = new BRAM(this, "bram", width)
  val fifo = new FIFO_OUT(this, "fifo", width)
  val write_count = signal(32);
  
  val seq = sequencer("main")
  
  val busy_reg = signal()
  busy := (busy_reg or kick or init)
  busy_reg <= (seq.idle, kick or init)
  busy_reg.default(HIGH)

  bram.we.default(LOW)
  fifo.we.default(LOW)
  bram.address <= (seq.idle, offset)
  write_count <= (seq.idle, VECTOR_ZERO)
  
  val write_addr = signal(32)
  write_addr <= (seq.idle, offset)
  
  def gen_init_entry():State = {
    val s = seq.add()
    write_addr <= (s, (write_addr + 1))
    bram.address <= (s, write_addr)
    bram.dout <= (s, ?(test, write_addr, VECTOR_ZERO))
    bram.we <= (s, HIGH)
    write_count <= (s, (write_count + 1))
    return s
  }

  def gen_emit_prepare():State = {
    val s = seq.add()
    bram.address <= (s, (bram.address + 1))
    return s
  }

  def gen_emit_entry():State = {
    val s = seq.add()
    bram.address <= (s, (bram.address +1))
    write_count <= (s, (write_count + 1))
    fifo.we <= (s, HIGH)
    fifo.dout <= (s, bram.din)
    return s
  }

  val init_seq = gen_init_entry()
  val emit_seq = gen_emit_entry()
  seq.idle -> (init, init_seq)
  seq.idle -> (kick, gen_emit_prepare()) -> emit_seq
  init_seq -> (write_count == (words-1), seq.idle)
  emit_seq -> (write_count == (words-1), seq.idle)
}

class Bram2FifoSim(name:String, target:Bram2Fifo) extends SimModule(name){
  
  def this(target:Bram2Fifo) = this("bram2fifo_sim", target)

  val (clk, reset, counter) = system(10)

  val inst = instance(target, "U")
  val ram = instance(new SimpleBlockRAM(32, 5, 32), "MEM")

  inst.signalFor(target.kick) := ?(counter == 200, HIGH, LOW)
  inst.signalFor(target.init) := ?(counter == 10, HIGH, LOW)
  inst.signalFor(target.test) := HIGH
  
  inst.sysClk := clk
  inst.sysReset := reset

  inst.signalFor(target.offset) := VECTOR_ZERO
  ram.signalFor("address_b") := inst.signalFor(target.bram.address)
  ram.signalFor("we_b") := inst.signalFor(target.bram.we)
  inst.signalFor(target.bram.din) := ram.signalFor("dout_b")
  ram.signalFor("din_b") := inst.signalFor(target.bram.dout)
  ram.sysClk := clk
}

object Bram2Fifo {

  def main(args:Array[String]) = {
    val m = new Bram2Fifo(1024, 32)
    m.visualize_statemachine();
    m.genVHDL()
    val debug = new Bram2Fifo(32,32)
    debug.genVHDL()
    val sim = new Bram2FifoSim(debug)
    sim.genVHDL()
  }
}

