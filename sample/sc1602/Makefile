SYNTHESIJER ?= ../../bin

SOURCES = SC1602Test.java SC1602Wrapper.java
VHDL_SOURCES = $(SOURCES:.java=.vhd)
VERILOG_SOURCES = $(SOURCES:.java=.v)
OPT = --chaining --ip-exact=SC1602Test --legacy-instance-variable-name

all:
	java -cp $(SYNTHESIJER):. synthesijer.Main --vhdl --verilog $(OPT) $(SOURCES)
	javac -cp $(SYNTHESIJER):. SC1602TestKC705.java
	java -cp $(SYNTHESIJER):. SC1602TestKC705

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
	-rm -f SC1602TestKC705.vhd
	-rm -f SC1602TestKC705.v

