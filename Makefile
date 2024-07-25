all:
	cp -f scripts/run.script.general scripts/run.script
	sbt compile
	sbt assembly
	chmod 755 target/synthesijer

clean:
	-rm -rf pkg
	-rm -f pkg.tar.gz
	-rm -f *.snap
	sbt clean
	-rm -rf target
	snapcraft clean

test:
	cd sample/test; \
	make; \
	make ghdl; \
	make iverilog; \
	make test_vhdl; \
	make test_verilog; \
	make clean

	
# TODO: broken
#snapcraft: clean
#	cp -f scripts/run.script.snap scripts/run.script
#	sbt assembly
#	mkdir -p pkg
#	cp target/synthesijer pkg
#	tar cvzf pkg.tar.gz pkg
#	snapcraft

