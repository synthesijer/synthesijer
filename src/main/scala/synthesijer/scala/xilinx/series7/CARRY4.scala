package synthesijer.scala.xilinx.series7

/**
  * @author miyo
  */

import synthesijer.scala._

import synthesijer.hdl.HDLSignal

class CARRY4 extends CombinationLogic("CARRY4") {
  
  val O = outP("O", 4)
  val CO = outP("CO", 4)
  
  val DI = inP("DI", 4)
  val S = inP("S", 4)
  
  val CYINIT = inP("CYINIT")
  val CI = inP("CI")
  
}

class CARRY4_test() extends Module("CARRY4_test", "clk", "reset") {
  
  val a = for(i <- 0 until 8) yield inP("a"+i+"p")
  val b = for(i <- 0 until 8) yield inP("b"+i+"p")
  val c = for(i <- 0 until 8) yield outP("c"+i+"p")
  
  c(0) := CARRY4.and(this, a(0), b(0))
  c(1) := a(1) and b(1)
  
  c(2) := CARRY4.or(this, a(2), b(2))
  c(3) := a(3) or b(3)
  
  c(4) := CARRY4.not(this, a(4))
  c(5) := (a(5)!)

  val x = CARRY4.half_addr(this, a(6), b(6))
  c(6) := x._1
  
  val y = a(7) + b(7)
  c(7) := y
  
}

class CARRY4_test2() extends Module("CARRY4_test2", "clk", "reset") {
  
  val x0 = inP("x0")
  val y0 = inP("y0")
  val z0 = outP("z0")
  val z1 = outP("z1")
  val z2 = outP("z2")
  
  z0 := x0 and y0

  val x1c = new CARRY4Sig(x0)
  val y1c = new CARRY4Sig(y0)
  
  z1 := x1c and y1c
  
  z2 := z0 or z1
  
  val a = inP("a", 32)
  val b = inP("b", 32)
  val c0 = outP("c0", 32)
  val c1 = outP("c1", 32)
  
  val a0 = new CARRY4Sig(a)
  val b0 = new CARRY4Sig(b)
  
  println("+")
  c0 := a0 + b0
  c1 := a + b
  
}

class CARRY4_sim(t:CARRY4_test) extends SimModule("CARRY4_sim"){
  
  val (clk, reset, counter) = system(10)
  
  val u = instance(t, "u")

  for(i <- 0 until 8){
    u.signalFor(t.a(i)) $ clk := ?(counter == 10, HIGH, ?(counter == 11,  LOW, ?(counter == 12, HIGH, LOW)))
    u.signalFor(t.b(i)) $ clk := ?(counter == 10, HIGH, ?(counter == 11, HIGH, ?(counter == 12,  LOW, LOW)))
  }
  
}

class CARRY4Sig(s:Signal) extends Signal(s.module, s.signal){
  def this(p:Port) = this(p.signal)
  def and (e:CARRY4Sig):ExprItem = CARRY4.and(s.module, this, e)
  def or (e:CARRY4Sig):ExprItem = CARRY4.or(s.module, this, e)
  def + (e:CARRY4Sig):ExprItem = CARRY4.add32(s.module, this, e)
}


object CARRY4 {

  private val tmpl = new CARRY4()
  
  def init(m:ModuleFunc) = {
    m.add_library("UNISIM", "UNISIM.vcomponents.all")
    m.add_library("UNIMACRO", "unimacro.Vcomponents.all")
  }
  
  def and(m:ModuleFunc, a:ExprItem, b:ExprItem):Signal = {
    init(m)
    val result = m.signal()
    val u = m.instance(tmpl)
    u.signalFor(tmpl.DI) := m.value(0, 4)
    u.signalFor(tmpl.S) := m.value(0, 3) & a
    u.signalFor(tmpl.CYINIT) := b
    result := u.signalFor(tmpl.O).ref(1)
    return result
  }

  def or(m:ModuleFunc, a:ExprItem, b:ExprItem):Signal = {
    init(m)
    val result = m.signal()
    val u = m.instance(tmpl)
    u.signalFor(tmpl.DI) := m.value(0, 3) & b
    u.signalFor(tmpl.S) := m.value(0, 3) & a
    u.signalFor(tmpl.CYINIT) := m.value(1, 4)
    result := u.signalFor(tmpl.O).ref(1)
    return result
  }
  
  def not(m:ModuleFunc, a:ExprItem):Signal = {
    init(m)
    val result = m.signal()
    val u = m.instance(tmpl)
    u.signalFor(tmpl.DI) := m.value(0, 4)
    u.signalFor(tmpl.S) := m.value(1, 4)
    u.signalFor(tmpl.CYINIT) := a
    result := u.signalFor(tmpl.O).ref(0)
    return result
  }
  
  def half_addr(m:ModuleFunc, a:ExprItem, b:ExprItem):(ExprItem, ExprItem) = {
    init(m)
    val s = m.signal()
    val c = m.signal()
    val u = m.instance(tmpl)
    u.signalFor(tmpl.DI) := m.value(0, 4)
    u.signalFor(tmpl.S) := m.value(0, 3) & a
    u.signalFor(tmpl.CYINIT) := b
    s := u.signalFor(tmpl.O).ref(0)
    c := u.signalFor(tmpl.O).ref(1)
    return (s, c)
  }

  def full_addr(m:ModuleFunc, a:ExprItem, b:ExprItem, c_in:ExprItem) : (ExprItem, ExprItem) = {
    init(m)
    val (s0, c0) = half_addr(m, a, b)
    val (s, c1) = half_addr(m, s0, c_in)
    val c = and(m, c0, c1)
    return (s, c)
  }

  def add32(m:ModuleFunc, a:ExprItem, b:ExprItem) : ExprItem = {
    init(m)
    val s = for(i <- 0 until 32) yield m.signal()
    val c = for(i <- 0 until 32) yield m.signal()
    for(i <- 0 until 32){
      if(i == 0){
    	val x = full_addr(m, a.ref(i), b.ref(i), m.LOW)
        s(i) := x._1
        c(i) := x._2
      }else{
    	val x = full_addr(m, a.ref(i), b.ref(i), c(i-1))
        s(i) := x._1
        c(i) := x._2
      }
    }
    return m.bitarray2vector(s)
  }

  def main(args:Array[String]) = {
    val t = new CARRY4_test()
    t.genVHDL()
    t.genVerilog()
    val sim = new CARRY4_sim(t)
    sim.genVHDL()
    sim.genVerilog()
    val t2 = new CARRY4_test2()
    t2.genVHDL()
  }
  
}
