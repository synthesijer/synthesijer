SYNTHESIJER ?= ../../bin

SOURCES = Test000.java Test001.java Test001Sim.java Test002.java Test003.java Test004.java Test005.java Test006.java Test007.java Test008.java Test009.java Test010.java Test011.java
VHDL_SOURCES = $(SOURCES:.java=.vhd)
VERILOG_SOURCES = $(SOURCES:.java=.v)
OPT = --chaining

all: $(SOURCES)
	java -cp $(SYNTHESIJER) synthesijer.Main --verilog --vhdl $(OPT) $(SOURCES)

ghdl64: $(VHDL_SOURCES)
	ghdl -a -Wc,-m32 -Wa,--32 -Wl,--32 $(VHDL_SOURCES) sim.vhd sim006.vhd sim011.vhd ../../lib/vhdl/singleportram.vhd ../../lib/vhdl/dualportram.vhd
	ghdl -e -Wa,--32 -Wl,-m32 sim
	ghdl -e -Wa,--32 -Wl,-m32 sim006
	ghdl -e -Wa,--32 -Wl,-m32 sim011

ghdl32: $(VHDL_SOURCES)
	ghdl -a $(VHDL_SOURCES) sim.vhd ../../lib/vhdl/dualportram.vhd sim006.vhd sim011.vhd ../../lib/vhdl/*.vhd
	ghdl -e sim
	ghdl -e sim006
	ghdl -e sim011

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
	-rm -f *.txt
