SUBDIRS = ./led \
	./serial_echo \
	./bench \
	./quickstart \
	./sc1602 \
	./test \
	./bf/microboard \
	./bf/top \
	./bf 

all: $(SUBDIRS)

clean: $(SUBDIRS)

$(SUBDIRS): FORCE
	make -C $@ $(MAKECMDGOALS)

FORCE:
