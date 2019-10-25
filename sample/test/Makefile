SYNTHESIJER ?= ../../target/synthesijer

SOURCES = Test000.java Test001.java Test001Sim.java Test002.java Test003.java Test004.java Test005.java Test006.java Test007.java Test008.java Test009.java Test010.java Test011.java Test012.java Test013.java Test013_A.java Test013_B.java Test013_C.java Test014.java Test017.java Test018.java Test020.java Test021.java Test021_test.java Test022.java Test022_test.java Test_ArithInt.java Test023.java
VHDL_SOURCES = $(SOURCES:.java=.vhd)
VERILOG_SOURCES = $(SOURCES:.java=.v)
SIM_SOURCES = sim_Test001.VHD sim_Test002.VHD sim_Test003.VHD sim_Test004.VHD sim_Test005.VHD sim_Test006.VHD sim_Test007.VHD sim_Test014.VHD sim_Test017.VHD sim_Test018.VHD sim_Test020.VHD sim_Test_ArithInt.VHD sim_Test021.VHD sim_Test022.VHD sim_Test023.VHD

IVERILOG_OPT = -DDUMP_ENABLE

OPT ?= --chaining --bb2 --ssa



.PHONY: all synthesis ghdl test clean test_vhdl test_verilog iverilog

all: synthesis

%.vhd : %.java
	java -cp . -jar $(SYNTHESIJER) synthesijer.Main --vhdl $(OPT) $(SOURCES)

%.v : %.java
	java -cp . -jar $(SYNTHESIJER) synthesijer.Main --verilog $(OPT) $(SOURCES)

synthesis: $(SOURCES)
	java -cp $(SYNTHESIJER) synthesijer.Main --verilog --vhdl $(OPT) $(SOURCES)

ghdl: $(VHDL_SOURCES)
	ghdl -a $(VHDL_SOURCES) sim.vhd ../../lib/vhdl/dualportram.vhd sim001.vhd sim006.vhd sim011.vhd sim013.vhd ../../lib/vhdl/*.vhd $(SIM_SOURCES)

test_java: synthesis
	java -cp $(SYNTHESIJER):. Test001
	java -cp $(SYNTHESIJER):. Test002
	java -cp $(SYNTHESIJER):. Test003
	java -cp $(SYNTHESIJER):. Test004
	java -cp $(SYNTHESIJER):. Test005
	java -cp $(SYNTHESIJER):. Test006
	java -cp $(SYNTHESIJER):. Test007
	java -cp $(SYNTHESIJER):. Test014
	java -cp $(SYNTHESIJER):. Test017
	java -cp $(SYNTHESIJER):. Test018
	java -cp $(SYNTHESIJER):. Test020
	java -cp $(SYNTHESIJER):. Test021_test
	java -cp $(SYNTHESIJER):. Test022_test
	java -cp $(SYNTHESIJER):. Test023

test_vhdl: ghdl
	ghdl -r sim_test001
	ghdl -r sim_test002
	ghdl -r sim_test003
	ghdl -r sim_test004
	ghdl -r sim_test005
	ghdl -r sim_test006
	ghdl -r sim_test007
	ghdl -r sim_test014
	ghdl -r sim_test017
	ghdl -r sim_test018
	ghdl -r sim_test020
	ghdl -r sim_test021
	ghdl -r sim_test022
	ghdl -r sim_test023
	ghdl -r sim_test_arithint

iverilog: synthesis
	iverilog $(IVERILOG_OPT) -o sim_verilog_Test001 sim_Test001.v Test001.v
	iverilog $(IVERILOG_OPT) -o sim_verilog_Test002 sim_Test002.v Test002.v ../../lib/verilog/dualportram.v
	iverilog $(IVERILOG_OPT) -o sim_verilog_Test003 sim_Test003.v Test002.v Test003.v ../../lib/verilog/dualportram.v
	iverilog $(IVERILOG_OPT) -o sim_verilog_Test004 sim_Test004.v Test004.v
	iverilog $(IVERILOG_OPT) -o sim_verilog_Test005 sim_Test005.v Test005.v Test004.v
	iverilog $(IVERILOG_OPT) -o sim_verilog_Test006 sim_Test006.v Test006.v
	iverilog $(IVERILOG_OPT) -o sim_verilog_Test007 sim_Test007.v Test007.v ../../lib/verilog/inputflag.v ../../lib/verilog/outputflag.v ../../lib/verilog/inputport16.v ../../lib/verilog/outputport16.v ../../lib/verilog/inputport32.v ../../lib/verilog/outputport32.v
	iverilog $(IVERILOG_OPT) -o sim_verilog_Test014 sim_Test014.v Test014.v
	iverilog $(IVERILOG_OPT) -o sim_verilog_Test017 sim_Test017.v Test017.v ../../lib/verilog/singleportram.v
	iverilog $(IVERILOG_OPT) -o sim_verilog_Test018 sim_Test018.v Test018.v ../../lib/verilog/singleportram.v
	iverilog $(IVERILOG_OPT) -o sim_verilog_Test020 sim_Test020.v Test020.v ../../lib/verilog/singleportram.v ../../lib/verilog/dualportram.v ../../lib/verilog/synthesijer_mul32.v
	iverilog $(IVERILOG_OPT) -o sim_verilog_Test021 sim_Test021.v Test021.v Test021_test.v ../../lib/verilog/singleportram.v ../../lib/verilog/dualportram.v ../../lib/verilog/synthesijer_mul32.v
	iverilog $(IVERILOG_OPT) -o sim_verilog_Test022 sim_Test022.v Test022.v Test022_test.v ../../lib/verilog/singleportram.v ../../lib/verilog/dualportram.v ../../lib/verilog/synthesijer_mul32.v
	iverilog $(IVERILOG_OPT) -o sim_verilog_Test_ArithInt sim_Test_ArithInt.v Test_ArithInt.v ../../lib/verilog/synthesijer_mul32.v ../../lib/verilog/synthesijer_mul64.v ../../lib/verilog/synthesijer_div.v ../../lib/verilog/synthesijer_div32.v ../../lib/verilog/synthesijer_div64.v
	iverilog $(IVERILOG_OPT) -o sim_verilog_Test023 sim_Test023.v Test023.v

test_verilog: iverilog
	./sim_verilog_Test001
	./sim_verilog_Test002
	./sim_verilog_Test003
	./sim_verilog_Test004
	./sim_verilog_Test005
	./sim_verilog_Test006
	./sim_verilog_Test007
	./sim_verilog_Test014
	./sim_verilog_Test017
	./sim_verilog_Test018
	./sim_verilog_Test020
	./sim_verilog_Test021
	./sim_verilog_Test022
	./sim_verilog_Test_ArithInt
	./sim_verilog_Test023

test: test_java test_vhdl test_verilog

clean:
	-rm -f *.class
	-rm -f $(VHDL_SOURCES)
	-rm -f $(VERILOG_SOURCES)
	-rm -f *.xml
	-rm -f *.ir
	-rm -f *.dot
	-rm -f *.svg
	-rm -f *.o
	-rm -f sim
	-rm -f *.vcd
	-rm -f *.ghw
	-rm -f work-obj93.cf
	-rm -f *.tmp_info
	-rm -f *~
	-rm -f sim???
	-rm -f sim_test*
	-rm -f sim_verilog_*
	-rm -f *.iroha
