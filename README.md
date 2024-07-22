# Synthesijer
https://synthesijer.github.io/web/

Synthesijer is a compiler from Java to VHDL/Verilog-HDL.
In addition to that, we can build hardware logic by using HDL building block in Synthesijer with Scala(Synthesijer.Scala).

## Install from snap
Please refer https://snapcraft.io/synthesijer .

## Build from source code
### Requirement

* JDK 17 https://openjdk.java.net/install/
* sbt 1.10.0 https://www.scala-sbt.org/index.html

### Build

    % git clone https://github.com/synthesijer/synthesijer.git
    % cd synthesijer
    % make

### Run

    % ./target/synthesijer sample/test/Test000.java

or 

    % java -jar ./target/synthesijer sample/test/Test000.java

Some options are printed by the following

    % java -jar ./target/synthesijer --help


## Getting started in Synthesijer.scala

### Prerequirements

+ Java (11 and later)
+ Sbt (1.2.8 and later)

### Prepare the project directory

    % sbt new synthesijer/sjr-scala.g8 --name=sjr-scala-test
    % cd sjr-scala-test
    % sbt run

By executing this command, Hello.scala is compiled and executed. And then, you can get VHDL, Verilog and some constriants files.

### Write and run program
For example, modify src/main/scala/example/Hello.scala as the following.
    import synthesijer.scala._
        
    object led {
      
      def generate_led(n:Int) : Module = {
        val m = new Module("led")
        val q = m.outP("q")
        val counter = m.signal("counter", 32)
        q := counter.ref(n)
        
        // at main state, counter <= counter + 1
        val seq = m.sequencer("main")
        val s0 = seq.add()
        seq.idle -> s0
        counter <= (seq.idle, m.VECTOR_ZERO)
        counter <= (s0, counter + 1)
        return m
      }
      
      def generate_sim(target:Module, name:String) : SimModule = {
    
              val sim = new SimModule(name)
              val inst = sim.instance(target, "U")
              
              val (clk, reset, counter) = sim.system(10)
    
              inst.sysClk := clk
              inst.sysReset := reset
              
              return sim
      }
    
      
      def main(args:Array[String]) = {
        val led = generate_led(5)
        val sim = generate_sim(led, "led_sim")
        led.genVHDL()
        led.genVerilog()
        sim.genVHDL()
    
        led.portFor("q").setPinID("M14"); led.portFor("q").setIoAttr("LVCMOS33");
        led.sysClk.setPinID("L16");   led.sysClk.setIoAttr("LVCMOS33");
        led.sysReset.setPinID("G15"); led.sysReset.setIoAttr("LVCMOS33");
    
        println(led.genUCF())
        println(led.genXDC())
      }
    }

After writing program, run it.
