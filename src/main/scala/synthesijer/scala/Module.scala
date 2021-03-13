package synthesijer.scala

import synthesijer.Misc
import synthesijer.hdl.HDLExpr
import synthesijer.hdl.HDLInstance
import synthesijer.hdl.HDLModule
import synthesijer.hdl.HDLOp
import synthesijer.hdl.HDLPort
import synthesijer.hdl.HDLPrimitiveType
import synthesijer.hdl.HDLSignal
import synthesijer.hdl.HDLSimModule
import synthesijer.hdl.HDLUtils
import synthesijer.hdl.expr.HDLPreDefinedConstant
import synthesijer.hdl.expr.HDLValue
import synthesijer.tools.xilinx.HDLModuleToComponentXML

trait ModuleFunc extends HDLModule{
  
  var id = 0
  var all_ports = Seq.empty[Port]
  var all_signals = Seq.empty[Signal]
  var all_instances = Seq.empty[Instance]
  var all_sequencers = Seq.empty[Sequencer]
  
  def genVHDL() = Utils.genVHDL(this)

  def genVerilog() = Utils.genVerilog(this)

  def genVHDLTmpl() = Utils.genVHDLTmpl(this)

  def genComponentXML() = HDLModuleToComponentXML.conv(this, null, "vendor", "user");

  def genUCF() = Misc.genUCF(this)

  def genXDC() = Misc.genXDC(this)

  def portFor(name:String):Port = {
    for(p <- all_ports){
      if(p.port.getName() == name) return p
    }
    return null
  }

  def outP(name:String) : Port = {
    val p = new Port(this, newPort(name, HDLPort.DIR.OUT, HDLPrimitiveType.genBitType()))
    all_ports = all_ports :+ p
    return p
  }
  def outP(name:String, width:Int) : Port = {
    val p = new Port(this, newPort(name, HDLPort.DIR.OUT, HDLPrimitiveType.genVectorType(width)))
    all_ports = all_ports :+ p
    return p
  }
  def outSignedP(name:String, width:Int) : Port = {
    val p = new Port(this, newPort(name, HDLPort.DIR.OUT, HDLPrimitiveType.genSignedType(width)))
    all_ports = all_ports :+ p
    return p
  }
  
  def inP(name:String) : BitPort = {
    val p = new BitPort(this, newPort(name, HDLPort.DIR.IN, HDLPrimitiveType.genBitType()))
    all_ports = all_ports :+ p
    return p
  }
  def inP(name:String, width:Int) : Port = {
    val p = new Port(this, newPort(name, HDLPort.DIR.IN, HDLPrimitiveType.genVectorType(width)))
    all_ports = all_ports :+ p
    return p
  }

  def inSignedP(name:String, width:Int) : Port = {
    val p = new Port(this, newPort(name, HDLPort.DIR.IN, HDLPrimitiveType.genSignedType(width)))
    all_ports = all_ports :+ p
    return p
  }
  
  def ioP(name:String) : BitPort = {
    val p = new BitPort(this, newPort(name, HDLPort.DIR.INOUT, HDLPrimitiveType.genBitType()))
    all_ports = all_ports :+ p
    return p
  }
  def ioP(name:String, width:Int) : Port = {
    val p = new Port(this, newPort(name, HDLPort.DIR.INOUT, HDLPrimitiveType.genVectorType(width)))
    all_ports = all_ports :+ p
    return p
  }

  def signal(name:String, width:Integer) : Signal = {
    val s = new Signal(this, newSignal(name, HDLPrimitiveType.genSignedType(width)))
    all_signals = all_signals :+ s
    return s
  }
  def signal(width:Integer) : Signal = {
    val s = new Signal(this, newSignal("synthesijer_scala_tmp_" + id, HDLPrimitiveType.genSignedType(width)))
    id = id + 1
    all_signals = all_signals :+ s
    return s
  }
  
  def signal(name:String) : BitSignal = {
    val s = new BitSignal(this, newSignal(name, HDLPrimitiveType.genBitType()))
    all_signals = all_signals :+ s
    return s
  }
  
  def signal() : BitSignal = {
    val s = new BitSignal(this, newSignal("synthesijer_scala_tmp_" + id, HDLPrimitiveType.genBitType()))
    id = id + 1
    all_signals = all_signals :+ s
    return s
  }
  
  private[scala] def expr(op:HDLOp, e0:ExprItem, e1:ExprItem, e2:ExprItem) : Expr = new Expr(this, newExpr(op, e0.toHDLExpr(), e1.toHDLExpr(), e2.toHDLExpr()))
  private[scala] def expr(op:HDLOp, e0:ExprItem, e1:ExprItem) : Expr = new Expr(this, newExpr(op, e0.toHDLExpr(), e1.toHDLExpr()))
  private[scala] def expr(op:HDLOp, e0:ExprItem) : Expr = new Expr(this, newExpr(op, e0.toHDLExpr()))
  
  private[scala] def expr(op:HDLOp, e0:ExprItem, e1:ExprItem, e2:Int) : Expr = new Expr(this, newExpr(op, e0.toHDLExpr(), e1.toHDLExpr(), Utils.toHDLValue(e2)));
  private[scala] def expr(op:HDLOp, e0:ExprItem, e1:Int) : Expr = new Expr(this, newExpr(op, e0.toHDLExpr(), Utils.toHDLValue(e1)));

  def sequencer(name:String) : Sequencer = {
    val s = new Sequencer(newSequencer(name))
    all_sequencers = all_sequencers :+ s
    return s
  }
  
  def instance(target:ModuleFunc, name:String) : Instance = {
    val i = new Instance(this, newModuleInstance(target, name))
    all_instances = all_instances :+ i
    return i
  }
  
  def instance(target:ModuleFunc) : Instance = {
    val i = instance(target, "synthesijer_scala_inst_" + id)
    id = id + 1
    return i
  }
  
  def instance(target:HDLModule, name:String) : Instance = {
    val i = new Instance(this, newModuleInstance(target, name))
    all_instances = all_instances :+ i
    return i
  }
  
  def instance(target:HDLModule) : Instance = {
    val i = instance(target, "synthesijer_scala_inst_" + id)
    id = id + 1
    return i
  }
  
  def visualize_statemachine() : Unit =  HDLUtils.genHDLSequencerDump(this)

  def visualize_resource() : Unit = HDLUtils.genResourceUsageTable(this)
  
  def parameter(name:String, value:Int) = newParameter(name, HDLPrimitiveType.genIntegerType(), new HDLValue(value))

  def parameter(name:String, value:String) = newParameter(name, HDLPrimitiveType.genStringType(), new HDLValue(value))

  def parameter(name:String, value:Value) = newParameter(name, value.toHDLExpr().getType(), value.toHDLExpr())
  
  def range(exp:ExprItem, b:Int, e:Int):ExprItem = expr(Op.take, expr(Op.>>>, exp, e), b - e + 1)
  
  def ref(exp:ExprItem, i:Int):ExprItem = expr(Op.REF, exp, i)
  
  def value(n:Long, width:Int):Value = new Value(this, n, width);
  
  def integer(n:Long, width:Int):ExprItem = new IntegerValue(this, n, width);
  
  def ?(c:ExprItem, e0:ExprItem, e1:ExprItem):ExprItem = expr(Op.IF, c, e0, e1)
  
  def padding0(e:ExprItem, v:Int) = expr(Op.padding0, e, v)
  
  def padding(e:ExprItem, v:Int) = expr(Op.padding, e, v)
  
  def drop(e:ExprItem, v:Int) = expr(Op.drop, e, v)
  
  def str2ary(s:String) = s.map(x => value(x, 8)).reduce((a:ExprItem, b:ExprItem) => (a & b))
  
  def bitarray2vector(s:Seq[BitSignal]):ExprItem = {
    var a:ExprItem = s(0)
    for(i <- 1 until s.length){
      a = s(i) & a
    }
    return a
  }
  
  val VECTOR_ZERO = new Constant(this, HDLPreDefinedConstant.VECTOR_ZERO)
  val ZERO = new Constant(this, HDLPreDefinedConstant.INTEGER_ZERO)
  val ONE = new Constant(this, HDLPreDefinedConstant.INTEGER_ONE)
  val TRUE = new Constant(this, HDLPreDefinedConstant.BOOLEAN_TRUE)
  val FALSE = new Constant(this, HDLPreDefinedConstant.BOOLEAN_FALSE)
  val LOW = new Constant(this, HDLPreDefinedConstant.LOW)
  val HIGH = new Constant(this, HDLPreDefinedConstant.HIGH)
  
  def decoder(sel:ExprItem, lst:List[(Int, Int)], w:Int) =
    lst.foldRight[ExprItem](value(0,w)){
      (a,z) => ?(sel == a._1, value(a._2, w), z)
    }
  
  def genSimModule():SimpleSimModule = new SimpleSimModule(getName() + "_sim", this)
  
  def add_library(k:String, v:String) = addLibraryUse(k, v)
  
  def component_required(f:Boolean) = setComponentDeclRequired(f)
  
}

class Module(val name:String, sysClkName:String, sysRsetName:String) extends HDLModule(name, sysClkName, sysRsetName) with ModuleFunc{
  def this(name:String) = this(name, "clk", "reset")
  
  def this(name:String, clk:Signal, reset:Signal) = this(name, clk.signal.getName, reset.signal.getName)
  
  val sysClk = new Port(this, getSysClk())
  val sysReset = new Port(this, getSysReset())
  
}

class CombinationLogic(val name:String) extends HDLModule(name) with ModuleFunc{

}

class SimModule(name:String) extends HDLSimModule(name) with ModuleFunc{

  def system(tick:Int):(Signal,Signal,Signal) = {
    val clk = signal("clk")
    val reset = signal("reset")
    val counter = signal("counter", 32); counter.reset(value(0, 32))
    
    val seq = sequencer("system")
    seq.tick(tick)
    
    val ss = seq.idle
    val s0 = seq.add("S0")
    ss -> s0 -> ss

    clk <= (ss, LOW)
    clk <= (s0, HIGH)

    counter $ clk := expr(Op.+, counter, 1)

    reset.reset(LOW)
    reset $ clk := expr(Op.IF, expr(Op.and, expr(Op.>, counter, 3), expr(Op.<, counter, 8)), HIGH, LOW)

    setSysClk(clk.signal)
    setSysReset(reset.signal)
    
    return (clk, reset, counter)
  }
  
}

class SimpleSimModule(name:String, target:ModuleFunc) extends SimModule(name){
  
  val inst = instance(target, "U")
  val (clk, reset, counter) = system(4)
  inst.sysClk := clk
  inst.sysReset := reset

  val ports = for(p <- target.getPorts if (p.getName() != "clk" && p.getName() != "reset")) yield(p)
  val pairs =
    (for(p <- ports;
      val s = if(p.getSignal().getWidth() > 1){
	signal(p.getName(), p.getSignal().getWidth())
      }else{
        signal(p.getName())
      }
    ) yield(p,s)).foldRight(Map.empty[HDLPort, Signal]){
      (t,z) => z + (t._1 -> t._2)
    }
  
  for((p, s) <- pairs){
    if(p.isOutput()){
      s := inst.signalFor(p.getName())
    }else{ // input
      inst.signalFor(p.getName()) := s
    }
  }
  
  def signalFor(p:Port):Option[Signal] = pairs.get(p.port)
  
  def ::(p:Port):Option[Signal] = signalFor(p)
  
}

class Instance(module:ModuleFunc, target:HDLInstance) {
  
  val sysClk = new Signal(module, target.getSignalForPort(target.getSubModule().getSysClkName()))
  val sysReset = new Signal(module, target.getSignalForPort(target.getSubModule().getSysResetName()))
  
  def signalFor(name:String) = new Signal(module, target.getSignalForPort(name))
  
  def ::(name:String) = signalFor(name)
  
  def signalFor(p:Port) = new Signal(module, target.getSignalForPort(p.port.getName()))
  
  def ::(p:Port) = signalFor(p)

  def parameter(name:String, value:Int):Unit = {
    target.setParameterOverwrite(name, new HDLValue(value))
  }
  
  def parameter(name:String, value:String):Unit = {
    target.setParameterOverwrite(name, new HDLValue(value))
  }
  
  def parameter(name:String, value:Value):Unit = {
    target.setParameterOverwrite(name, value.toHDLExpr())
  }
  
}

class BitPort(module:ModuleFunc, port:HDLPort) extends Port(module, port){
  
  override def signal:BitSignal = new BitSignal(module, port.getSignal())

}

class Port(module:ModuleFunc, val port:HDLPort) extends ExprItem(module) with ExprDestination{
  
  def := (e:ExprItem):Unit = port.getSignal().setAssign(null, e.toHDLExpr)
  
  def <= (t:(State, ExprItem)) : Unit = port.getSignal().setAssign(t._1.state, t._2.toHDLExpr)
  
  def <= (t:(State, Int, ExprItem)) : Unit = port.getSignal().setAssign(t._1.state, t._2, t._3.toHDLExpr)
  
  def <= (e:StateExpr) : Unit = this <= (e.state, e.expr)
  
  def is (e:StateExpr) : Unit = this <= (e.state, e.expr)
  
  def #= (t:(Sequencer, ExprItem)) : Unit = port.getSignal().setAssignForSequencer(t._1.seq, t._2.toHDLExpr)

  def #= (e:SeqExpr) : Unit = this #= (e.seq, e.expr)

  def reset(e:ExprItem): Unit = port.getSignal().setResetValue(e.toHDLExpr)
  
  def toHDLExpr() = port.getSignal()
  
  def default(e:ExprItem):Unit = port.getSignal().setDefaultValue(e.toHDLExpr())
  
  def width() : Int = port.getSignal().getWidth()
  
  def $(e:Port) = new PortEventAssignPair(this, e)

  def $(s:Signal) = new SignalEventAssignPair(this, s)
  
  def getHDLSignal : HDLSignal = port.getSignal()
  
  def signal:Signal = new Signal(module, port.getSignal())

  def setPinID(v:String) = port.setPinID(v)

  def getPinID() = port.getPinID()

  def setIoAttr(v:String) = port.setIoAttr(v)

  def getIoAttr() = port.getIoAttr()

}

abstract class ExprItem(val module:ModuleFunc) {
  
  def toHDLExpr() : HDLExpr
  
  def + (e:ExprItem):ExprItem = module.expr(Op.+, this, e)
  def + (v:Int) : ExprItem = module.expr(Op.+, this, v)
  
  def - (e:ExprItem):ExprItem = module.expr(Op.-, this, e)
  def - (v:Int) : ExprItem = module.expr(Op.-, this, v)

  def * (e:ExprItem):ExprItem = module.expr(Op.*, this, e)
  def * (v:Int) : ExprItem = module.expr(Op.*, this, v)

  def and (e:ExprItem):ExprItem = module.expr(Op.and, this, e)
  def or (e:ExprItem):ExprItem = module.expr(Op.or, this, e)
  def xor (e:ExprItem):ExprItem = module.expr(Op.xor, this, e)
  def unary_! : ExprItem = module.expr(Op.not, this)

  @deprecated("postfix unary method need compile option in Scala 2.13.x ")
  def ! : ExprItem = module.expr(Op.not, this)
  
  def == (e:ExprItem):ExprItem = module.expr(Op.eq, this, e)
  def == (v:Int) : ExprItem = module.expr(Op.eq, this, v)
  
  def != (e:ExprItem) : ExprItem = module.expr(Op.neq, this, e)
  def != (v:Int) : ExprItem = module.expr(Op.neq, this, v)
  
  def /= (v:Int) : ExprItem = module.expr(Op./=, this, v)
  
  def < (e:ExprItem):ExprItem = module.expr(Op.<, this, e)
  def < (v:Int) : ExprItem = module.expr(Op.<, this, v)
  
  def > (e:ExprItem):ExprItem = module.expr(Op.>, this, e)
  def > (v:Int) : ExprItem = module.expr(Op.>, this, v)
  
  def leq (e:ExprItem):ExprItem = module.expr(Op.<=, this, e)
  def leq (v:Int):ExprItem = module.expr(Op.<=, this, v)
  
  def geq (e:ExprItem):ExprItem = module.expr(Op.>=, this, e)
  def geq (v:Int):ExprItem = module.expr(Op.>=, this, v)
  
  def /= (e:ExprItem):ExprItem = module.expr(Op./=, this, e)
  
  def & (e:ExprItem):ExprItem = module.expr(Op.concat, this, e)
  def concat (e:ExprItem):ExprItem = module.expr(Op.&, this, e)
  
  def >> (v:Int):ExprItem = module.expr(Op.>>, this, v)
  def >>> (v:Int):ExprItem = module.expr(Op.>>>, this, v)
  def << (v:Int):ExprItem = module.expr(Op.<<, this, v)

  def * (s:State):StateExpr = new StateExpr(s, this)

  def in (s:State):StateExpr = new StateExpr(s, this)
  
  def _in (s:State):StateExpr = new StateExpr(s, this)

  def -> (s:State):StateExpr = new StateExpr(s, this)
  
  def ref(i:Int):ExprItem = module.ref(this, i)

  def range(b:Int, e:Int):ExprItem = module.range(this, b, e)

  def drop(v:Int):ExprItem = module.drop(this, v)
  
  def ?(a:ExprItem, b:ExprItem) = module.?(this, a, b)

}

class PortEventAssignPair(d:ExprDestination, e:Port){
  def := (expr:ExprItem) = d.getHDLSignal().setAssignForPortEvent(e.port, expr.toHDLExpr())
}

class SignalEventAssignPair(d:ExprDestination, s:Signal){
  def := (expr:ExprItem) = d.getHDLSignal().setAssignForSignalEvent(s.signal, expr.toHDLExpr())
}

trait ExprDestination {
  def := (e:ExprItem);
  def <= (t:(State, ExprItem));
  def <= (t:(State, Int, ExprItem));
  def #= (t:(Sequencer, ExprItem));
  def width():Int;
  def <= (e:StateExpr);
  def is (e:StateExpr);
  def #= (e:SeqExpr);
  def $ (e:Port) : PortEventAssignPair;
  def $ (s:Signal) : SignalEventAssignPair;
  
  def getHDLSignal() : HDLSignal;
  
}

class BitSignal(module:ModuleFunc, signal:HDLSignal) extends Signal(module, signal){ }


class Signal(module:ModuleFunc, val signal:HDLSignal) extends ExprItem(module) with ExprDestination{
  
  def := (e:ExprItem) : Unit = signal.setAssign(null, e.toHDLExpr)
  
  def <= (t:(State, ExprItem)) : Unit = signal.setAssign(t._1.state, t._2.toHDLExpr)
  
  def <= (e:StateExpr) : Unit = this <= (e.state, e.expr)
  
  def is (e:StateExpr) : Unit = this <= (e.state, e.expr)

  def <= (t:(State, Int, ExprItem)) : Unit = signal.setAssign(t._1.state, t._2, t._3.toHDLExpr)
  
  def := (t:(Sequencer, ExprItem)) : Unit = signal.setAssignForSequencer(t._1.seq, t._2.toHDLExpr)
  
  def #= (t:(Sequencer, ExprItem)) : Unit = signal.setAssignForSequencer(t._1.seq, t._2.toHDLExpr)

  def #= (e:SeqExpr) : Unit = this #= (e.seq, e.expr)

  def width() : Int = signal.getWidth()

  def reset(e:ExprItem): Unit = signal.setResetValue(e.toHDLExpr)
  
  def toHDLExpr() = signal
  
  def default(e:ExprItem):Unit = signal.setDefaultValue(e.toHDLExpr())
  
  def setDebug(f:Boolean) : Unit = signal.setDebugFlag(f)
  
  def $(e:Port) = new PortEventAssignPair(this, e)

  def $(s:Signal) = new SignalEventAssignPair(this, s)
  
  def getHDLSignal = signal
  
}

class Expr(module:ModuleFunc, val expr:HDLExpr) extends ExprItem(module){
  
  def toHDLExpr() = expr

}

class Value(module:ModuleFunc, n:Long, width:Int) extends ExprItem(module){
  val value = new HDLValue(n.toString(), HDLPrimitiveType.genVectorType(width))
  def toHDLExpr() = value
}

class IntegerValue(module:ModuleFunc, n:Long, width:Int) extends ExprItem(module){
  val value = new HDLValue(n.toString(), HDLPrimitiveType.genSignedType(width))
  def toHDLExpr() = value
}

object Utils {
  
  def genVHDL(m:HDLModule) = HDLUtils.generate(m, HDLUtils.VHDL);
  
  def genVerilog(m:HDLModule) = HDLUtils.generate(m, HDLUtils.Verilog);
  
  def toHDLValue(num:Int) = new HDLValue(num.toString, HDLPrimitiveType.genIntegerType())

  def genVHDLTmpl(m:HDLModule) = {
    println("component " + m.getName())
    if(m.getParameters().length > 0){
      println("generic (")
      println(m.getParameters().mkString("  ", ";\n  ", ""))
      println(");")
    }
    println("port (")
    println((for(p <- m.getPorts) yield {
      "%s : %s %s".format(p.getName(), p.getDir().getVHDL(), p.getType().asInstanceOf[HDLPrimitiveType].getVHDL(false))
    }).mkString("  ", ";\n  ", ""))
    println(");")
    println("end component " + m.getName() + ";")
  }

}

class Constant(module:ModuleFunc, val v:HDLPreDefinedConstant) extends ExprItem(module){
  val name = toString
  def toHDLExpr() = v
}

object Op{
  val + = HDLOp.ADD
  val add = HDLOp.ADD
  val - = HDLOp.SUB
  val sub = HDLOp.SUB
  val * = HDLOp.HDLMUL
  val mul = HDLOp.HDLMUL
  val and = HDLOp.AND
  val or = HDLOp.OR
  val xor = HDLOp.XOR
  val not = HDLOp.NOT
  val == = HDLOp.EQ
  val eq = HDLOp.EQ
  val < = HDLOp.LT
  val lt = HDLOp.LT
  val > = HDLOp.GT
  val gt = HDLOp.GT
  val <= = HDLOp.LEQ
  val leq = HDLOp.LEQ
  val >= = HDLOp.GEQ
  val geq = HDLOp.GEQ
  val /= = HDLOp.NEQ
  val neq = HDLOp.NEQ
  val REF = HDLOp.REF
  val IF = HDLOp.IF
  val & = HDLOp.CONCAT
  val concat = HDLOp.CONCAT
  val drop = HDLOp.DROPHEAD
  val take = HDLOp.TAKE
  val padding = HDLOp.PADDINGHEAD
  val padding0 = HDLOp.PADDINGHEAD_ZERO
  val id = HDLOp.ID
  val >> = HDLOp.ARITH_RSHIFT // TODO check
  val >>> = HDLOp.LOGIC_RSHIFT
  val << = HDLOp.LSHIFT
}

