SYNTHESIJER ?= ../../../../target/synthesijer
SYNTHESIJER_EXTRA_LIB ?= ../../../../extra-libs

CLASSPATH = $(SYNTHESIJER):$(SYNTHESIJER_EXTRA_LIB)/bin:.

SOURCES = $(SYNTHESIJER_EXTRA_LIB)/src/synthesijer/lib/axi/SimpleAXIMemIface32RTL.java \
          $(SYNTHESIJER_EXTRA_LIB)/src/synthesijer/lib/axi/SimpleAXIMemIface32RTLTest.java \
          MemTest.java

VHDL_SOURCES = $(SOURCES:.java=.vhd)
VERILOG_SOURCES = $(SOURCES:.java=.v)
OPT = --chaining --ip-exact=MemTest

all: $(SOURCES)
	java -cp $(CLASSPATH) synthesijer.Main --verilog --vhdl $(OPT) $(SOURCES)

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
	-rm -rf MemTest_v1_0
	-rm -f SimpleAXIMemIface32RTLTest.v
	-rm -f SimpleAXIMemIface32RTLTest.vhd
