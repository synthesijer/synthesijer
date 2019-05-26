SUBDIRS = ./led \
	./serial_echo \
	./bench \
	./quickstart \
	./sc1602 \
	./test \
	./bf/microboard \
	./bf/top \
	./bf \
	./bd/TB-7Z-020 \
	./bd/zynq/axi_test \
	./bd/Zybo/quickstart \
	./bd/Zybo/axiburst_test 

all: $(SUBDIRS)

clean: $(SUBDIRS)

$(SUBDIRS): FORCE
	make -C $@ $(MAKECMDGOALS)

FORCE:
