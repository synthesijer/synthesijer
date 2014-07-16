SOURCE = Prime.java PrimeSim.java BubbleSort.java BubbleSortSim.java

all: $(SOURCE)
	java -cp ../../bin synthesijer.Main $(SOURCE)

linux: $(SOURCE)
	ghdl -a -Wc,-m32 -Wa,--32 -Wl,--32 Prime.vhd PrimeSim.vhd primesim_top.vhd ../../lib/vhdl/simple_dualportram.vhd
	ghdl -a -Wc,-m32 -Wa,--32 -Wl,--32 BubbleSort.vhd BubbleSortSim.vhd bubblesortsim_top.vhd ../../lib/vhdl/simple_dualportram.vhd
	ghdl -e -Wa,--32 -Wl,-m32 primesim_top
	ghdl -e -Wa,--32 -Wl,-m32 bubblesortsim_top

mac: $(SOURCE)
	ghdl -a --std=93c --workdir=. bubblesortsim_top.vhd BubbleSort.vhd BubbleSortSim.vhd ../../lib/vhdl/simple_dualportram.vhd
	ghdl -a --std=93c --workdir=. primesim_top.vhd Prime.vhd PrimeSim.vhd ../../lib/vhdl/simple_dualportram.vhd
