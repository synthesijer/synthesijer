SYNTHESIJER ?= ../../../bin

microboard_top.vhd: MicroboardTop.java
	javac -cp $(SYNTHESIJER):. $<
	java -cp $(SYNTHESIJER):. $(<:.java=)

microboard_top.v: MicroboardTop.java
	javac -cp $(SYNTHESIJER):. $<
	java -cp $(SYNTHESIJER):. $(<:.java=)

ise:
	mkdir -p xst/projnav.tmp
	xst -intstyle ise -ifn "microboard_top.xst" -ofn "microboard_top.syr" 
	ngdbuild -intstyle ise -dd _ngo -nt timestamp -uc microboard.ucf -p xc6slx9-csg324-2 microboard_top.ngc microboard_top.ngd  
	map -intstyle ise -p xc6slx9-csg324-2 -w -logic_opt off -ol high -t 1 -xt 0 -register_duplication off -r 4 -global_opt off -mt off -ir off -pr off -lc off -power off -o microboard_top_map.ncd microboard_top.ngd microboard_top.pcf 
	par -w -intstyle ise -ol high -mt off microboard_top_map.ncd microboard_top.ncd microboard_top.pcf 
	trce -intstyle ise -v 3 -s 2 -n 3 -fastpaths -xml microboard_top.twx microboard_top.ncd -o microboard_top.twr microboard_top.pcf 
	bitgen -intstyle ise -f microboard_top.ut microboard_top.ncd 

all-vhdl: microboard_top.vhd
	ln -sf microboard_top_vhdl.prj microboard_top.prj
	$(MAKE) ise

all-verilog: microboard_top.v
	ln -sf microboard_top_verilog.prj microboard_top.prj
	$(MAKE) ise

clean:
	-rm -f MicroboardTop.class
	-rm -f microboard_top.vhd
	-rm -f microboard_top.v
	-rm -f microboard_top.bgn
	-rm -f microboard_top.bld
	-rm -f microboard_top.drc
	-rm -f microboard_top.lso
	-rm -f microboard_top.ncd
	-rm -f microboard_top.ngc
	-rm -f microboard_top.ngd
	-rm -f microboard_top.ngr
	-rm -f microboard_top.pad
	-rm -f microboard_top.par
	-rm -f microboard_top.pcf
	-rm -f microboard_top.ptwx
	-rm -f microboard_top.syr
	-rm -f microboard_top.twr
	-rm -f microboard_top.twx
	-rm -f microboard_top.unroutes
	-rm -f microboard_top.xpi
	-rm -f microboard_top_bitgen.xwbt
	-rm -f microboard_top_map.map
	-rm -f microboard_top_map.mrp
	-rm -f microboard_top_map.ncd
	-rm -f microboard_top_map.ngm
	-rm -f microboard_top_map.xrpt
	-rm -f microboard_top_ngdbuild.xrpt
	-rm -f microboard_top_pad.csv
	-rm -f microboard_top_pad.txt
	-rm -f microboard_top_par.xrpt
	-rm -f microboard_top_summary.xml
	-rm -f microboard_top_usage.xml
	-rm -f microboard_top_xst.xrpt
	-rm -rf _ngo
	-rm -rf _xmsgs
	-rm -rf xst
	-rm -rf xlnx_auto_0_xdb
	-rm -f par_usage_statistics.html
	-rm -f webtalk.log
