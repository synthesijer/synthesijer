SYNTHESIJER ?= ../../../../target/synthesijer
SYNTHESIJER_EXTRA_LIB ?= ../../../../extra-libs
SYNTHESIJER_LIB ?= ../../../../lib

CLASSPATH = $(SYNTHESIJER):$(SYNTHESIJER_EXTRA_LIB)/bin:.

SOURCES = $(SYNTHESIJER_EXTRA_LIB)/src/synthesijer/lib/axi/SimpleAXIMemIface32RTL.java \
          $(SYNTHESIJER_EXTRA_LIB)/src/synthesijer/lib/axi/SimpleAXIMemIface32RTLTest.java \
          $(SYNTHESIJER_EXTRA_LIB)/src/synthesijer/lib/axi/AXILiteSlave32RTL.java \
          AXIHP_MEMCPY.java

VHDL_SOURCES = $(SOURCES:.java=.vhd)
VERILOG_SOURCES = $(SOURCES:.java=.v)
OPT = --ip-exact=AXIHP_MEMCPY

all: $(SOURCES)
	java -cp $(CLASSPATH) synthesijer.Main --verilog --vhdl $(OPT) $(SOURCES)
	cp *.vhd AXIHP_MEMCPY_v1_0/src/
	cp $(SYNTHESIJER_LIB)/vhdl/singleportram.vhd AXIHP_MEMCPY_v1_0/src/
	cp $(SYNTHESIJER_LIB)/vhdl/dualportram.vhd AXIHP_MEMCPY_v1_0/src/
	cp $(SYNTHESIJER_EXTRA_LIB)/hdl/vhdl/axi_lite_slave_32.vhd AXIHP_MEMCPY_v1_0/src/
	cp $(SYNTHESIJER_EXTRA_LIB)/hdl/vhdl/simple_axi_memiface_32.vhd AXIHP_MEMCPY_v1_0/src/

clean:
	-rm -f *.class
	-rm -f $(VHDL_SOURCES)
	-rm -f $(VERILOG_SOURCES)
	-rm -f *.xml
	-rm -f *.dot
	-rm -f *.o
	-rm -f *.vcd
	-rm -f work-obj93.cf
	-rm -f *.ir
	-rm -f *.txt
	-rm -f synthesijer_lib_axi_SimpleAXIMemIface32RTLTest.v
	-rm -f synthesijer_lib_axi_SimpleAXIMemIface32RTLTest.vhd
	-rm -f synthesijer_lib_axi_AXILiteSlave32RTL.vhd
	-rm -rf AXIHP_MEMCPY_v1_0
	-rm -f SimpleAXIMemIface32RTLTest.v
	-rm -f SimpleAXIMemIface32RTLTest.vhd
