SYNTHESIJER ?= ../../synthesijer.jar

SOURCES = led.java counter.java Firefly.java
VHDL_SOURCES = $(SOURCES:.java=.vhd)
VERILOG_SOURCES = $(SOURCES:.java=.v)
OPT = --chaining

all: led

led: $(SOURCES)
	java -cp $(SYNTHESIJER) synthesijer.Main $(OPT) $(SOURCES)
	javac -cp $(SYNTHESIJER):. led_top.java
	java -cp $(SYNTHESIJER):. led_top

clean:
	-rm -f *.class
	-rm -f $(VHDL_SOURCES)
	-rm -f $(VERILOG_SOURCES)
	-rm -f *.xml
	-rm -f *.dot
	-rm -f *.o
	-rm -f sim
	-rm -f *.vcd
	-rm -f *.ir
	-rm -f *.txt
	-rm -f work-obj93.cf
	-rm -f led_top.vhd
	-rm -f led_top.v

