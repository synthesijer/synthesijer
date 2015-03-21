SYNTHESIJER ?= ../../../../bin
SYNTHESIJER_APPLICATIONS ?= ../../../../../synthesijer-applications
SYNTHESIJER_VHDL_LIBS ?= ../../../../lib/vhdl

CLASSPATH = $(SYNTHESIJER):$(SYNTHESIJER_APPLICATIONS)/bin:.

SOURCES = $(SYNTHESIJER_APPLICATIONS)/src/synthesijer/lib/axi/AXIMemIface32RTL.java \
          $(SYNTHESIJER_APPLICATIONS)/src/synthesijer/lib/axi/AXIMemIface32RTLTest.java \
          $(SYNTHESIJER_APPLICATIONS)/src/synthesijer/lib/axi/AXILiteSlave32RTL.java \
          AXIHP_MEMCPY2.java

VHDL_SOURCES = $(SOURCES:.java=.vhd)
VERILOG_SOURCES = $(SOURCES:.java=.v)
OPT = --ip-exact=AXIHP_MEMCPY2

all: $(SOURCES)
	java -cp $(CLASSPATH) synthesijer.Main --verilog --vhdl $(OPT) $(SOURCES)
	cp *.vhd AXIHP_MEMCPY2_v1_0/src/
	cp $(SYNTHESIJER_VHDL_LIBS)/singleportram.vhd AXIHP_MEMCPY2_v1_0/src/
	cp $(SYNTHESIJER_VHDL_LIBS)/dualportram.vhd AXIHP_MEMCPY2_v1_0/src/
	cp $(SYNTHESIJER_APPLICATIONS)/hdl/vhdl/axi_lite_slave_32.vhd AXIHP_MEMCPY2_v1_0/src/
	cp $(SYNTHESIJER_APPLICATIONS)/hdl/vhdl/simple_axi_memiface_32.vhd AXIHP_MEMCPY2_v1_0/src/

clean:
	-rm -f *.class
	-rm -f $(VHDL_SOURCES)
	-rm -f $(VERILOG_SOURCES)
	-rm -f *.xml
	-rm -f *.dot
	-rm -f *.o
	-rm -f *.vcd
	-rm -f work-obj93.cf
	-rm -f *.txt
	-rm -f synthesijer_lib_axi_AXIMemIface32RTLTest.v
	-rm -f synthesijer_lib_axi_AXIMemIface32RTLTest.vhd
	-rm -f synthesijer_lib_axi_AXILiteSlave32RTL.vhd
	-rm -rf AXIHP_MEMCPY2_v1_0
