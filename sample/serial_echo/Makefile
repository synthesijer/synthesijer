SOURCE = EchoTest.java RS232C_RX_Wrapper.java RS232C_TX_Wrapper.java ToUpper.java

all: exstick microboard hdl

hdl: $(SOURCE)
	java -cp ../../bin:. synthesijer.Main $(SOURCE)

exstick_top: EchoTestTop.java 
	javac -cp ../../bin:. EchoTestTop.java
	java -cp ../../bin:. EchoTestTop

microboard: MicroboardTop.java 
	javac -cp ../../bin:. MicroboardTop.java
	java -cp ../../bin:. MicroboardTop
