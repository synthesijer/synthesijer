SOURCES = EchoTest.java RS232C_RX.java RS232C_TX.java ToUpper.java
VHDL_SOURCES = $(SOURCES:.java=.vhd)
VERILOG_SOURCES = $(SOURCES:.java=.v)

all: hdl exstick microboard

hdl: $(SOURCES)
	java -cp ../../bin:. synthesijer.Main $(SOURCES)

exstick: EchoTestTop.java 
	javac -cp ../../bin:. EchoTestTop.java
	java -cp ../../bin:. EchoTestTop

microboard: MicroboardTop.java 
	javac -cp ../../bin:. MicroboardTop.java
	java -cp ../../bin:. MicroboardTop

clean:
	-rm -f *.class
	-rm -f $(VHDL_SOURCES)
	-rm -f $(VERILOG_SOURCES)
	-rm -f *.xml
	-rm -f *.dot
	-rm -f *.o
	-rm -f sim
	-rm -f *.vcd
	-rm -f work-obj93.cf
	-rm -f top.vhd top.v
	-rm -f microboard_top.vhd, microboard_top.v
