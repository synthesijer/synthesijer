
import synthesijer.scala._
import synthesijer.utils._

class mkSimpleFifoTest extends SimModule("simple_fifo_sim"){

  val (clk, reset, counter) = system(10)

  val i = instance(new SimpleFifo32())
  i.sysClk := clk
  i.sysReset := reset

  i.signalFor("we") $ clk := ?(counter == 10, HIGH,
                             ?(counter == 11, HIGH,
                             ?(counter == 12, HIGH,
                             ?(counter == 13, HIGH,
                             ?(counter == 14, HIGH,
                             ?(counter == 15, HIGH,
                             LOW))))))
  i.signalFor("din") $ clk := ?(counter == 10, value(10, 32),
                              ?(counter == 11, value(11, 32),
                              ?(counter == 12, value(12, 32),
                              ?(counter == 13, value(13, 32),
                              ?(counter == 14, value(14, 32),
                              ?(counter == 15, value(15, 32),
                              value(0xffffffff, 32)))))))
  i.signalFor("re") $ clk := ?(counter == 20, HIGH,
                             ?(counter == 21, HIGH,
                             ?(counter == 22, HIGH,
                             ?(counter == 23, HIGH,
                             ?(counter == 24, HIGH,
                             ?(counter == 25, HIGH,
                             LOW))))))
}

object mkSimpleFifoTest{

  def main(args:Array[String]) = {
    val i = new mkSimpleFifoTest()
    i.genVHDL()
    i.genVerilog()
  }

}
