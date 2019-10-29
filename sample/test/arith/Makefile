SYNTHESIJER ?= ../../../target/synthesijer

OPT ?= --chaining --bb2

all:
	java -cp $(SYNTHESIJER) synthesijer.Main $(OPT) arith.java

clean:
	-rm -f *.ir
	-rm -f *.tmp_info
	-rm -f *.dot
	-rm -f *.class
	-rm -f arith.v
	-rm -f arith.vhd
