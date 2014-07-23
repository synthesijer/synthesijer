SOURCES = BF.java HW.java IO.java ../serial_echo/RS232C_RX_Wrapper.java ../serial_echo/RS232C_TX_Wrapper.java rs232c.java 
VHDL_SOURCES = $(SOURCES:.java=.vhd)
VERILOG_SOURCES = $(SOURCES:.java=.v)

all: $(SOURCES) microboard
	java -cp ../../bin:../serial_echo:. synthesijer.Main $(SOURCES)

microboard: MicroboardTop.java
	javac -cp ../../bin:. $<
	java -cp ../../bin:. $(<:.java=)

clean:
	-rm -f *.class
	-rm -f $(VHDL_SOURCES)
	-rm -f $(VERILOG_SOURCES)
	-rm -f *.xml
	-rm -f *.dot
	-rm -f *.o
	-rm -f *.vcd
	-rm -f work-obj93.cf
	-rm -f *.svg
	-rm -f microboard_top.vhd
	-rm -f microboard_top.v

