SYNTHESIJER ?= ../../../bin

top.vhd: MkTop.java
	javac -cp $(SYNTHESIJER):. $<
	java -cp $(SYNTHESIJER):. $(<:.java=)

top.v: MkTop.java
	javac -cp $(SYNTHESIJER):. $<
	java -cp $(SYNTHESIJER):. $(<:.java=)

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
