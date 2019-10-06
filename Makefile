all:
	cp -f scripts/run.script.general scripts/run.script
	sbt assembly

clean:
	-rm -rf pkg
	-rm -f pkg.tar.gz
	-rm -f *.snap
	snapcraft clean
	sbt clean
	
snapcraft: clean
	cp -f scripts/run.script.snap scripts/run.script
	sbt assembly
	mkdir -p pkg
	cp target/synthesijer pkg
	tar cvzf pkg.tar.gz pkg
	snapcraft

