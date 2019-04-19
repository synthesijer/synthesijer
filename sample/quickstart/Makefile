SYNTHESIJER ?= ../../synthesijer.jar

SOURCES = Test.java Top.java
VHDL_SOURCES = $(SOURCES:.java=.vhd)
VERILOG_SOURCES = $(SOURCES:.java=.v)
OPT = --chaining

led: $(SOURCES)
	java -cp $(SYNTHESIJER) synthesijer.Main --vhdl --verilog $(OPT) $(SOURCES)

clean:
	-rm -f *.class
	-rm -f $(VHDL_SOURCES)
	-rm -f $(VERILOG_SOURCES)
	-rm -f *.xml
	-rm -f *.dot
	-rm -f *.o
	-rm -f sim
	-rm -f *.vcd
	-rm -f *.txt
	-rm -f work-obj93.cf
	-rm -f led_top.vhd
	-rm -f led_top.v

