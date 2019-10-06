SYNTHESIJER ?= ../../target/synthesijer
SOURCES = Prime.java PrimeSim.java BubbleSort.java BubbleSortSim.java MergeSort.java MergeSortSim.java
VHDL_SOURCES = $(SOURCES:.java=.vhd)
VERILOG_SOURCES = $(SOURCES:.java=.v)
OPT = --chaining --bb2

all: build


build: $(SOURCES)
	java -cp $(SYNTHESIJER) synthesijer.Main --vhdl --verilog $(OPT) $(SOURCES)

ghdl64: build primesim_top.vhd bubblesortsim_top.vhd mergesortsim_top.vhd
	ghdl -a -Wc,-m32 -Wa,--32 -Wl,--32 $(VHDL_SOURCES) primesim_top.vhd bubblesortsim_top.vhd mergesortsim_top.vhd ../../lib/vhdl/dualportram.vhd ../../lib/vhdl/singleportram.vhd
	ghdl -e -Wa,--32 -Wl,-m32 primesim_top
	ghdl -e -Wa,--32 -Wl,-m32 bubblesortsim_top
	ghdl -e -Wa,--32 -Wl,-m32 mergesortsim_top

ghdl32: build primesim_top.vhd bubblesortsim_top.vhd mergesortsim_top.vhd
	ghdl -a --std=93c --workdir=. $(VHDL_SOURCES) bubblesortsim_top.vhd primesim_top.vhd mergesortsim_top.vhd ../../lib/vhdl/dualportram.vhd ../../lib/vhdl/singleportram.vhd
	ghdl -e primesim_top
	ghdl -e bubblesortsim_top
	ghdl -e mergesortsim_top

iverilog : build primesim_top.v bubblesortsim_top.v mergesortsim_top.v
	iverilog -DDUMP_ENABLE -o primesim_top primesim_top.v PrimeSim.v Prime.v ../../lib/verilog/dualportram.v ../../lib/verilog/singleportram.v
	iverilog -DDUMP_ENABLE -o bubblesortsim_top bubblesortsim_top.v BubbleSortSim.v BubbleSort.v ../../lib/verilog/dualportram.v ../../lib/verilog/singleportram.v
	iverilog -DDUMP_ENABLE -o mergesortsim_top mergesortsim_top.v MergeSortSim.v MergeSort.v ../../lib/verilog/dualportram.v ../../lib/verilog/singleportram.v

clean:
	-rm -f *.class
	-rm -f $(VHDL_SOURCES)
	-rm -f $(VERILOG_SOURCES)
	-rm -f *.xml
	-rm -f *.dot
	-rm -f *.o
	-rm -f *.s
	-rm -f *.ir
	-rm -f *.vcd
	-rm -f *.ghw
	-rm -f *.svg
	-rm -f *.png
	-rm -f *~
	-rm -f work-obj93.cf
	-rm -f primesim_top 
	-rm -f bubblesortsim_top
	-rm -f mergesortsim_top
	-rm -f *.txt
