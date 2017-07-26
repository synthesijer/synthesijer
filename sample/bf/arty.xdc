## Clock signal

set_property -dict { PACKAGE_PIN E3    IOSTANDARD LVCMOS33 } [get_ports { CLK100MHZ }]; #IO_L12P_T1_MRCC_35 Sch=gclk[100]
create_clock -add -name sys_clk_pin -period 10.00 -waveform {0 5} [get_ports { CLK100MHZ }];

set_property -dict { PACKAGE_PIN D10   IOSTANDARD LVCMOS33 } [get_ports { TXD_OUT}]; #IO_L19N_T3_VREF_16 Sch=uart_rxd_out
set_property -dict { PACKAGE_PIN A9    IOSTANDARD LVCMOS33 } [get_ports { RXD_IN}]; #IO_L14N_T2_SRCC_16 Sch=uart_txd_in

set_property -dict { PACKAGE_PIN D9    IOSTANDARD LVCMOS33 } [get_ports { USER_RESET }]; #IO_L6N_T0_VREF_16 Sch=btn[0]
