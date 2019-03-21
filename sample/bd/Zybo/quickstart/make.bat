
set SOURCES=%SYNTHESIJER_EXT_LIB%\src\synthesijer\lib\axi\SimpleAXIMemIface32RTL.java ^
            %SYNTHESIJER_EXT_LIB%\src\synthesijer\lib\axi\SimpleAXIMemIface32RTLTest.java ^
            %SYNTHESIJER_EXT_LIB%\src\synthesijer\lib\axi\AXILiteSlave32RTL.java ^
            AXIHP_MEMCPY.java


set OPT=--ip-exact=AXIHP_MEMCPY

echo %SOURCES%
echo %OPT%

java -jar %SYNTHESIJER% --lib-classes=%SYNTHESIJER_EXT_LIB%\bin --verilog --vhdl %OPT% %SOURCES%
REM java -cp %SYNTHESIJER%;%SYNTHESIJER_EXT_LIB%\bin;. synthesijer.Main --verilog --vhdl %OPT% %SOURCES%
copy *.vhd AXIHP_MEMCPY_v1_0\src\
copy %SYNTHESIJER_LIB%\vhdl\singleportram.vhd                    AXIHP_MEMCPY_v1_0\src\
copy %SYNTHESIJER_LIB%\vhdl\dualportram.vhd                      AXIHP_MEMCPY_v1_0\src\
copy %SYNTHESIJER_EXT_LIB%\hdl\vhdl\axi_lite_slave_32.vhd      AXIHP_MEMCPY_v1_0\src\
copy %SYNTHESIJER_EXT_LIB%\hdl\vhdl\simple_axi_memiface_32.vhd AXIHP_MEMCPY_v1_0\src\
