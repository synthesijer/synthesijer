SYNTHESIJER ?= ../../../../bin
SYNTHESIJER_APPLICATIONS ?= ../../../../../synthesijer-applications
SYNTHESIJER_VHDL_LIBS ?= ../../../../lib/vhdl

CLASSPATH = $(SYNTHESIJER):$(SYNTHESIJER_APPLICATIONS)/bin:.

SOURCES = $(SYNTHESIJER_APPLICATIONS)/src/synthesijer/lib/axi/SimpleAXIMemIface32RTL.java \
          $(SYNTHESIJER_APPLICATIONS)/src/synthesijer/lib/axi/SimpleAXIMemIface32RTLTest.java \
          $(SYNTHESIJER_APPLICATIONS)/src/synthesijer/lib/axi/AXILiteSlave32RTL.java \
          Test.java

VHDL_SOURCES = $(SOURCES:.java=.vhd)
VERILOG_SOURCES = $(SOURCES:.java=.v)
OPT = --ip-exact=Test

all: $(SOURCES)
	java -cp $(CLASSPATH) synthesijer.Main --verilog --vhdl $(OPT) $(SOURCES)
	mkdir -p Test_v1_0
	mkdir -p Test_v1_0/src
	cp *.vhd Test_v1_0/src/
	cp $(SYNTHESIJER_VHDL_LIBS)/singleportram.vhd Test_v1_0/src/
	cp $(SYNTHESIJER_VHDL_LIBS)/dualportram.vhd Test_v1_0/src/
	cp $(SYNTHESIJER_APPLICATIONS)/hdl/vhdl/axi_lite_slave_32.vhd Test_v1_0/src/
	cp $(SYNTHESIJER_APPLICATIONS)/hdl/vhdl/simple_axi_memiface_32.vhd Test_v1_0/src/

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
	-rm -f synthesijer_lib_axi_SimpleAXIMemIface32RTLTest.v
	-rm -f synthesijer_lib_axi_SimpleAXIMemIface32RTLTest.vhd
	-rm -f synthesijer_lib_axi_AXILiteSlave32RTL.vhd
	-rm -rf Test_v1_0
