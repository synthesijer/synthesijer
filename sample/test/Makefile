SOURCES = Test001.java Test001Sim.java Test002.java Test003.java Test004.java Test005.java Test006.java
VHDL_SOURCES = $(SOURCES:.java=.vhd)
VERILOG_SOURCES = $(SOURCES:.java=.v)

all: $(SOURCES)
	java -cp ../../bin synthesijer.Main $(SOURCES)

ghdl64: $(VHDL_SOURCES)
	ghdl -a -Wc,-m32 -Wa,--32 -Wl,--32 $(VHDL_SOURCES) sim.vhd ../../lib/vhdl/simple_dualportram.vhd
	ghdl -e -Wa,--32 -Wl,-m32 sim

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
