
set SOURCES=%SYNTHESIJER_EXTRA_LIB%\src\synthesijer\lib\axi\AXIMemIface32RTL.java ^
	    %SYNTHESIJER_EXTRA_LIB%\src\synthesijer\lib\axi\AXIMemIface32RTLTest.java ^
	    %SYNTHESIJER_EXTRA_LIB%\src\synthesijer\lib\axi\AXILiteSlave32RTL.java ^
            AXIHP_MEMCPY2.java


set OPT=--ip-exact=AXIHP_MEMCPY2

echo %SOURCES%
echo %OPT%

REM java -jar %SYNTHESIJER% --lib-classes=%SYNTHESIJER_EXTRA_LIB%\bin --verilog --vhdl %OPT% %SOURCES%
java -cp %SYNTHESIJER%;%SYNTHESIJER_EXTRA_LIB%\bin;%SYNTHESIJER_EXTRA_LIB%\src. synthesijer.Main --verilog --vhdl %OPT% %SOURCES%
copy *.vhd AXIHP_MEMCPY2_v1_0\src\
copy %SYNTHESIJER_LIB%\vhdl\singleportram.vhd                    AXIHP_MEMCPY2_v1_0\src\
copy %SYNTHESIJER_LIB%\vhdl\dualportram.vhd                      AXIHP_MEMCPY2_v1_0\src\
copy %SYNTHESIJER_EXTRA_LIB%\hdl\vhdl\axi_lite_slave_32.vhd      AXIHP_MEMCPY2_v1_0\src\
copy %SYNTHESIJER_EXTRA_LIB%\hdl\vhdl\simple_axi_memiface_32.vhd AXIHP_MEMCPY2_v1_0\src\
copy %SYNTHESIJER_EXTRA_LIB%\hdl_lib\vhdl\simple_fifo_32.vhd AXIHP_MEMCPY2_v1_0\src\
copy %SYNTHESIJER_EXTRA_LIB%\hdl_lib\vhdl\simple_fifo.vhd AXIHP_MEMCPY2_v1_0\src\
