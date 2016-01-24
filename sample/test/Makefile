SYNTHESIJER ?= ../../bin

SOURCES = Test000.java Test001.java Test001Sim.java Test002.java Test003.java Test004.java Test005.java Test006.java Test007.java Test008.java Test009.java Test010.java Test011.java Test012.java Test013.java Test013_A.java Test013_B.java Test013_C.java Test014.java Test017.java Test018.java
VHDL_SOURCES = $(SOURCES:.java=.vhd)
VERILOG_SOURCES = $(SOURCES:.java=.v)
SIM_SOURCES = sim_Test001.VHD sim_Test002.VHD sim_Test003.VHD sim_Test004.VHD sim_Test017.VHD sim_Test018.VHD

OPT = --chaining

.PHONY: all synthesis ghdl64 ghdl test clean test_vhdl test_verilog iverilog

all: synthesis

synthesis: $(SOURCES)
	java -cp $(SYNTHESIJER) synthesijer.Main --verilog --vhdl $(OPT) $(SOURCES)

ghdl64: synthesis
	ghdl -a -Wc,-m32 -Wa,--32 -Wl,--32 $(VHDL_SOURCES) sim.vhd sim006.vhd sim011.vhd sim013.vhd ../../lib/vhdl/singleportram.vhd ../../lib/vhdl/dualportram.vhd $(SIM_SOURCES)
	ghdl -e -Wa,--32 -Wl,-m32 sim
	ghdl -e -Wa,--32 -Wl,-m32 sim006
	ghdl -e -Wa,--32 -Wl,-m32 sim011
	ghdl -e -Wa,--32 -Wl,-m32 sim013

ghdl: synthesis
	ghdl -a $(VHDL_SOURCES) sim.vhd ../../lib/vhdl/dualportram.vhd sim001.vhd sim006.vhd sim011.vhd sim013.vhd ../../lib/vhdl/*.vhd $(SIM_SOURCES)
	ghdl -e sim
	ghdl -e sim006
	ghdl -e sim011
	ghdl -e sim013
	ghdl -e sim001
	ghdl -e sim_test001
	ghdl -e sim_test002
	ghdl -e sim_test003
	ghdl -e sim_test004
	ghdl -e sim_test017
	ghdl -e sim_test018

test_vhdl: ghdl
	./sim_test001
	./sim_test002
	./sim_test003
	./sim_test004
	./sim_test017
	./sim_test018

iverilog: synthesis
	iverilog -o sim_verilog_Test001 sim_Test001.v Test001.v
	iverilog -o sim_verilog_Test002 sim_Test002.v Test002.v ../../lib/verilog/dualportram.v
	iverilog -o sim_verilog_Test003 sim_Test003.v Test002.v Test003.v ../../lib/verilog/dualportram.v
	iverilog -o sim_verilog_Test004 sim_Test004.v Test004.v

test_verilog: iverilog
	./sim_verilog_Test001
	./sim_verilog_Test002
	./sim_verilog_Test003
	./sim_verilog_Test004

test: test_vhdl test_verilog

clean:
	-rm -f *.class
	-rm -f $(VHDL_SOURCES)
	-rm -f $(VERILOG_SOURCES)
	-rm -f *.xml
	-rm -f *.ir
	-rm -f *.dot
	-rm -f *.o
	-rm -f sim
	-rm -f *.vcd
	-rm -f *.ghw
	-rm -f work-obj93.cf
	-rm -f *.txt
	-rm -f *~
	-rm -f sim???
	-rm -f sim_test???
	-rm -f sim_verilog_Test???
