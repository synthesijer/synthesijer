SYNTHESIJER ?= ../../bin

SOURCES = Test000.java Test001.java Test001Sim.java Test002.java Test003.java Test004.java Test005.java Test006.java Test007.java Test008.java Test009.java Test010.java Test011.java Test012.java Test013.java Test013_A.java Test013_B.java Test013_C.java Test014.java Test017.java Test018.java Test020.java
VHDL_SOURCES = $(SOURCES:.java=.vhd)
VERILOG_SOURCES = $(SOURCES:.java=.v)
SIM_SOURCES = sim_Test001.VHD sim_Test002.VHD sim_Test003.VHD sim_Test004.VHD sim_Test005.VHD sim_Test006.VHD sim_Test007.VHD sim_Test017.VHD sim_Test018.VHD sim_Test020.VHD

OPT = --chaining

.PHONY: all synthesis ghdl64 ghdl test clean test_vhdl test_verilog iverilog

all: synthesis

%.vhd : %.java
	java -cp $(SYNTHESIJER) synthesijer.Main --vhdl $(OPT) $(SOURCES)

%.v : %.java
	java -cp $(SYNTHESIJER) synthesijer.Main --verilog $(OPT) $(SOURCES)

#synthesis: $(SOURCES)
#	java -cp $(SYNTHESIJER) synthesijer.Main --verilog --vhdl $(OPT) $(SOURCES)

ghdl64: $(VHDL_SOURCES)
	ghdl -a -Wc,-m32 -Wa,--32 -Wl,--32 $(VHDL_SOURCES) sim.vhd sim006.vhd sim011.vhd sim013.vhd ../../lib/vhdl/singleportram.vhd ../../lib/vhdl/dualportram.vhd $(SIM_SOURCES)
	ghdl -e -Wa,--32 -Wl,-m32 sim
	ghdl -e -Wa,--32 -Wl,-m32 sim006
	ghdl -e -Wa,--32 -Wl,-m32 sim011
	ghdl -e -Wa,--32 -Wl,-m32 sim013

ghdl: $(VHDL_SOURCES)
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
	ghdl -e sim_test005
	ghdl -e sim_test006
	ghdl -e sim_test007
	ghdl -e sim_test017
	ghdl -e sim_test018
	ghdl -e sim_test020

test_java: $(VHDL_SOURCES)
	java -cp $(SYNTHESIJER):. Test001
	java -cp $(SYNTHESIJER):. Test002
	java -cp $(SYNTHESIJER):. Test003
	java -cp $(SYNTHESIJER):. Test004
	java -cp $(SYNTHESIJER):. Test005
	java -cp $(SYNTHESIJER):. Test006
	java -cp $(SYNTHESIJER):. Test007
	java -cp $(SYNTHESIJER):. Test017
	java -cp $(SYNTHESIJER):. Test018
	java -cp $(SYNTHESIJER):. Test020

test_vhdl: ghdl
	./sim_test001
	./sim_test002
	./sim_test003
	./sim_test004
	./sim_test005
	./sim_test006
	./sim_test007
	./sim_test017
	./sim_test018
	./sim_test020

iverilog: $(VERILOG_SOURCES)
	iverilog -o sim_verilog_Test001 sim_Test001.v Test001.v
	iverilog -o sim_verilog_Test002 sim_Test002.v Test002.v ../../lib/verilog/dualportram.v
	iverilog -o sim_verilog_Test003 sim_Test003.v Test002.v Test003.v ../../lib/verilog/dualportram.v
	iverilog -o sim_verilog_Test004 sim_Test004.v Test004.v
	iverilog -o sim_verilog_Test005 sim_Test005.v Test005.v Test004.v
	iverilog -o sim_verilog_Test006 sim_Test006.v Test006.v
	iverilog -o sim_verilog_Test007 sim_Test007.v Test007.v ../../lib/verilog/inputflag.v ../../lib/verilog/outputflag.v ../../lib/verilog/inputport16.v ../../lib/verilog/outputport16.v ../../lib/verilog/inputport32.v ../../lib/verilog/outputport32.v
	iverilog -o sim_verilog_Test017 sim_Test017.v Test017.v ../../lib/verilog/singleportram.v
	iverilog -o sim_verilog_Test018 sim_Test018.v Test018.v ../../lib/verilog/singleportram.v
	iverilog -o sim_verilog_Test020 sim_Test020.v Test020.v ../../lib/verilog/singleportram.v ../../lib/verilog/dualportram.v ../../lib/verilog/synthesijer_mul32.v

test_verilog: iverilog
	./sim_verilog_Test001
	./sim_verilog_Test002
	./sim_verilog_Test003
	./sim_verilog_Test004
	./sim_verilog_Test005
	./sim_verilog_Test006
	./sim_verilog_Test007
	./sim_verilog_Test017
	./sim_verilog_Test018
	./sim_verilog_Test020

test: test_java test_vhdl test_verilog

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
