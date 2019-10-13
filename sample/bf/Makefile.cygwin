SYNTHESIJER ?= ../../target/synthesijer
SOURCES = BF.java HW.java IO.java ../serial_echo/RS232C_RX_Wrapper.java ../serial_echo/RS232C_TX_Wrapper.java rs232c.java 
#OPT = --chaining --ip-exact=HW
OPT = --ip-exact=HW
CLASSES = $(SOURCES:.java=.class)
VHDL_SOURCES = $(SOURCES:.java=.vhd)
VERILOG_SOURCES = $(SOURCES:.java=.v)

all: $(SOURCES)
	java -jar $(SYNTHESIJER) --vhdl --verilog $(OPT) $(SOURCES)

clean:
	-rm -f $(CLASSES)
	-rm -f *.class
	-rm -f $(VHDL_SOURCES)
	-rm -f $(VERILOG_SOURCES)
	-rm -f *.xml
	-rm -f *.dot
	-rm -f *.ir
	-rm -f *.o
	-rm -f *.vcd
	-rm -f work-obj93.cf
	-rm -f *.svg
	-rm -f *.tmp_info
	-rm -rf HW_v1_0

