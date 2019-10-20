## Pre-Requirements

* iverilog
* ghdl
* gtkwave (option)

## GHDL
For Ubuntu 18.04,

    sudo apt install -y git make gnat zlib1g-dev
	git clone https://github.com/ghdl/ghdl
	cd ghdl
	./configure --prefix=/usr/local
	make
	sudo make install
	
## Test

    make test
