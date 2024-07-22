# Synthesijer
https://synthesijer.github.io/web/

Synthesijer is a compiler from Java to VHDL/Verilog-HDL.
In addition to that, we can build hardware logic by using HDL building block in Synthesijer with Scala(Synthesijer.Scala).

## Install from snap
Please refer https://snapcraft.io/synthesijer .

## Build from source code
### Requirement

* JDK 17 https://openjdk.java.net/install/
* sbt 1.10.0 https://www.scala-sbt.org/index.html

### Build

    % git clone https://github.com/synthesijer/synthesijer.git
    % cd synthesijer
    % make

## Run

    % ./target/synthesijer sample/test/Test000.java

or 

    % java -jar ./target/synthesijer sample/test/Test000.java

Some options are printed by the following

    % java -jar ./target/synthesijer --help

