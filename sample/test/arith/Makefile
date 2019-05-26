SYNTHESIJER ?= ../../../synthesijer.jar

OPT = --chaining --bb2 --ssa

all:
	java -cp $(SYNTHESIJER) synthesijer.Main $(OPT) arith.java

clean:
	-rm -f *.ir
	-rm -f *.txt
	-rm -f *.dot
	-rm -f *.class
	-rm -f arith.v
	-rm -f arith.vhd
