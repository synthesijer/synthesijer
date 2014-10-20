set_property DONT_TOUCH true [get_cells design_1_i/processing_system7_0/inst]
set_property DONT_TOUCH true [get_cells design_1_i/processing_system7_0]
set_property DONT_TOUCH true [get_cells design_1_i]

set_property IOSTANDARD LVCMOS33 [get_ports {LED[*]}]
set_property PACKAGE_PIN H20 [get_ports {LED[0]}]
set_property PACKAGE_PIN G19 [get_ports {LED[1]}]
set_property PACKAGE_PIN F19 [get_ports {LED[2]}]
set_property PACKAGE_PIN E19 [get_ports {LED[3]}]
set_property PACKAGE_PIN E20 [get_ports {LED[4]}]
set_property PACKAGE_PIN G20 [get_ports {LED[5]}]
set_property PACKAGE_PIN G21 [get_ports {LED[6]}]
set_property PACKAGE_PIN F21 [get_ports {LED[7]}]

set_property PACKAGE_PIN D18 [get_ports SYS_CLK3]
set_property IOSTANDARD LVCMOS33 [get_ports SYS_CLK3]
create_clock -period 6.060 [get_ports SYS_CLK3]

set_property IOSTANDARD LVCMOS33 [get_ports DVI_*]
set_property SLEW FAST [get_ports DVI_*]

set_property PACKAGE_PIN F16 [get_ports {DVI_B[0]}]
set_property PACKAGE_PIN E16 [get_ports {DVI_B[1]}]
set_property PACKAGE_PIN D16 [get_ports {DVI_B[2]}]
set_property PACKAGE_PIN D17 [get_ports {DVI_B[3]}]
set_property PACKAGE_PIN E15 [get_ports {DVI_B[4]}]
set_property PACKAGE_PIN D15 [get_ports {DVI_B[5]}]
set_property PACKAGE_PIN G15 [get_ports {DVI_B[6]}]
set_property PACKAGE_PIN G16 [get_ports {DVI_B[7]}]

set_property DRIVE 8 [get_ports {DVI_B[7]}]
set_property DRIVE 8 [get_ports {DVI_B[6]}]
set_property DRIVE 8 [get_ports {DVI_B[5]}]
set_property DRIVE 8 [get_ports {DVI_B[4]}]
set_property DRIVE 8 [get_ports {DVI_B[3]}]
set_property DRIVE 8 [get_ports {DVI_B[2]}]
set_property DRIVE 8 [get_ports {DVI_B[1]}]
set_property DRIVE 8 [get_ports {DVI_B[0]}]

set_property PACKAGE_PIN F18 [get_ports {DVI_G[0]}]
set_property PACKAGE_PIN E18 [get_ports {DVI_G[1]}]
set_property PACKAGE_PIN G17 [get_ports {DVI_G[2]}]
set_property PACKAGE_PIN F17 [get_ports {DVI_G[3]}]
set_property PACKAGE_PIN C15 [get_ports {DVI_G[4]}]
set_property PACKAGE_PIN B15 [get_ports {DVI_G[5]}]
set_property PACKAGE_PIN B16 [get_ports {DVI_G[6]}]
set_property PACKAGE_PIN B17 [get_ports {DVI_G[7]}]

set_property DRIVE 8 [get_ports {DVI_G[7]}]
set_property DRIVE 8 [get_ports {DVI_G[6]}]
set_property DRIVE 8 [get_ports {DVI_G[5]}]
set_property DRIVE 8 [get_ports {DVI_G[4]}]
set_property DRIVE 8 [get_ports {DVI_G[3]}]
set_property DRIVE 8 [get_ports {DVI_G[2]}]
set_property DRIVE 8 [get_ports {DVI_G[1]}]
set_property DRIVE 8 [get_ports {DVI_G[0]}]

set_property PACKAGE_PIN A16 [get_ports {DVI_R[0]}]
set_property PACKAGE_PIN A17 [get_ports {DVI_R[1]}]
set_property PACKAGE_PIN A18 [get_ports {DVI_R[2]}]
set_property PACKAGE_PIN A19 [get_ports {DVI_R[3]}]
set_property PACKAGE_PIN C17 [get_ports {DVI_R[4]}]
set_property PACKAGE_PIN C18 [get_ports {DVI_R[5]}]
set_property PACKAGE_PIN C19 [get_ports {DVI_R[6]}]
set_property PACKAGE_PIN B19 [get_ports {DVI_R[7]}]

set_property DRIVE 8 [get_ports {DVI_R[7]}]
set_property DRIVE 8 [get_ports {DVI_R[6]}]
set_property DRIVE 8 [get_ports {DVI_R[5]}]
set_property DRIVE 8 [get_ports {DVI_R[4]}]
set_property DRIVE 8 [get_ports {DVI_R[3]}]
set_property DRIVE 8 [get_ports {DVI_R[2]}]
set_property DRIVE 8 [get_ports {DVI_R[1]}]
set_property DRIVE 8 [get_ports {DVI_R[0]}]

set_property PACKAGE_PIN B20 [get_ports DVI_DE]
set_property PACKAGE_PIN D20 [get_ports DVI_VS]
set_property PACKAGE_PIN C20 [get_ports DVI_HS]
set_property PACKAGE_PIN A22 [get_ports DVI_DKEN]
set_property PACKAGE_PIN A21 [get_ports DVI_CLK]
set_property PACKAGE_PIN C22 [get_ports {DVI_CTRL[0]}]
set_property PACKAGE_PIN E21 [get_ports {DVI_CTRL[1]}]
set_property PACKAGE_PIN D21 [get_ports {DVI_CTRL[2]}]
set_property PACKAGE_PIN D22 [get_ports DVI_ISEL]
set_property PACKAGE_PIN B22 [get_ports DVI_PWRDN]
set_property PACKAGE_PIN B21 [get_ports DVI_MSEN]

set_property DRIVE 8 [get_ports DVI_DE]
set_property DRIVE 8 [get_ports DVI_VS]
set_property DRIVE 8 [get_ports DVI_HS]
set_property DRIVE 8 [get_ports DVI_DKEN]
set_property DRIVE 8 [get_ports DVI_CLK]
set_property DRIVE 8 [get_ports {DVI_CTRL[0]}]
set_property DRIVE 8 [get_ports {DVI_CTRL[1]}]
set_property DRIVE 8 [get_ports {DVI_CTRL[2]}]
set_property DRIVE 8 [get_ports DVI_ISEL]
set_property DRIVE 8 [get_ports DVI_PWRDN]
set_property DRIVE 8 [get_ports DVI_MSEN]


set_false_path -from [get_clocks clk_fpga_0] -to [get_clocks SYS_CLK3]





set_property MARK_DEBUG true [get_nets obj_obj_axi_reader_RVALID]
set_property MARK_DEBUG true [get_nets obj_obj_axi_writer_AWREADY]
set_property MARK_DEBUG true [get_nets obj_obj_axi_writer_WREADY]
set_property MARK_DEBUG true [get_nets obj_obj_axi_writer_BVALID]
set_property MARK_DEBUG true [get_nets obj_obj_axi_reader_RLAST]
set_property MARK_DEBUG true [get_nets obj_obj_axi_reader_ARREADY]
set_property MARK_DEBUG true [get_nets U_CANVAS/read_data_req]
set_property MARK_DEBUG true [get_nets U_CANVAS/write_data_req]
create_debug_core u_ila_0_0 ila
set_property ALL_PROBE_SAME_MU true [get_debug_cores u_ila_0_0]
set_property ALL_PROBE_SAME_MU_CNT 2 [get_debug_cores u_ila_0_0]
set_property C_ADV_TRIGGER false [get_debug_cores u_ila_0_0]
set_property C_DATA_DEPTH 1024 [get_debug_cores u_ila_0_0]
set_property C_EN_STRG_QUAL true [get_debug_cores u_ila_0_0]
set_property C_INPUT_PIPE_STAGES 0 [get_debug_cores u_ila_0_0]
set_property C_TRIGIN_EN false [get_debug_cores u_ila_0_0]
set_property C_TRIGOUT_EN false [get_debug_cores u_ila_0_0]
set_property port_width 1 [get_debug_ports u_ila_0_0/clk]
connect_debug_port u_ila_0_0/clk [get_nets [list wr_clk]]
set_property port_width 4 [get_debug_ports u_ila_0_0/probe0]
connect_debug_port u_ila_0_0/probe0 [get_nets [list {obj_axi_writer_WSTRB[0]} {obj_axi_writer_WSTRB[1]} {obj_axi_writer_WSTRB[2]} {obj_axi_writer_WSTRB[3]}]]
create_debug_port u_ila_0_0 probe
set_property port_width 2 [get_debug_ports u_ila_0_0/probe1]
connect_debug_port u_ila_0_0/probe1 [get_nets [list {obj_axi_writer_AWBURST[0]} {obj_axi_writer_AWBURST[1]}]]
create_debug_port u_ila_0_0 probe
set_property port_width 4 [get_debug_ports u_ila_0_0/probe2]
connect_debug_port u_ila_0_0/probe2 [get_nets [list {obj_axi_writer_AWCACHE[0]} {obj_axi_writer_AWCACHE[1]} {obj_axi_writer_AWCACHE[2]} {obj_axi_writer_AWCACHE[3]}]]
create_debug_port u_ila_0_0 probe
set_property port_width 3 [get_debug_ports u_ila_0_0/probe3]
connect_debug_port u_ila_0_0/probe3 [get_nets [list {obj_axi_writer_AWPROT[0]} {obj_axi_writer_AWPROT[1]} {obj_axi_writer_AWPROT[2]}]]
create_debug_port u_ila_0_0 probe
set_property port_width 32 [get_debug_ports u_ila_0_0/probe4]
connect_debug_port u_ila_0_0/probe4 [get_nets [list {obj_axi_writer_WDATA[0]} {obj_axi_writer_WDATA[1]} {obj_axi_writer_WDATA[2]} {obj_axi_writer_WDATA[3]} {obj_axi_writer_WDATA[4]} {obj_axi_writer_WDATA[5]} {obj_axi_writer_WDATA[6]} {obj_axi_writer_WDATA[7]} {obj_axi_writer_WDATA[8]} {obj_axi_writer_WDATA[9]} {obj_axi_writer_WDATA[10]} {obj_axi_writer_WDATA[11]} {obj_axi_writer_WDATA[12]} {obj_axi_writer_WDATA[13]} {obj_axi_writer_WDATA[14]} {obj_axi_writer_WDATA[15]} {obj_axi_writer_WDATA[16]} {obj_axi_writer_WDATA[17]} {obj_axi_writer_WDATA[18]} {obj_axi_writer_WDATA[19]} {obj_axi_writer_WDATA[20]} {obj_axi_writer_WDATA[21]} {obj_axi_writer_WDATA[22]} {obj_axi_writer_WDATA[23]} {obj_axi_writer_WDATA[24]} {obj_axi_writer_WDATA[25]} {obj_axi_writer_WDATA[26]} {obj_axi_writer_WDATA[27]} {obj_axi_writer_WDATA[28]} {obj_axi_writer_WDATA[29]} {obj_axi_writer_WDATA[30]} {obj_axi_writer_WDATA[31]}]]
create_debug_port u_ila_0_0 probe
set_property port_width 32 [get_debug_ports u_ila_0_0/probe5]
connect_debug_port u_ila_0_0/probe5 [get_nets [list {obj_axi_reader_ARADDR[0]} {obj_axi_reader_ARADDR[1]} {obj_axi_reader_ARADDR[2]} {obj_axi_reader_ARADDR[3]} {obj_axi_reader_ARADDR[4]} {obj_axi_reader_ARADDR[5]} {obj_axi_reader_ARADDR[6]} {obj_axi_reader_ARADDR[7]} {obj_axi_reader_ARADDR[8]} {obj_axi_reader_ARADDR[9]} {obj_axi_reader_ARADDR[10]} {obj_axi_reader_ARADDR[11]} {obj_axi_reader_ARADDR[12]} {obj_axi_reader_ARADDR[13]} {obj_axi_reader_ARADDR[14]} {obj_axi_reader_ARADDR[15]} {obj_axi_reader_ARADDR[16]} {obj_axi_reader_ARADDR[17]} {obj_axi_reader_ARADDR[18]} {obj_axi_reader_ARADDR[19]} {obj_axi_reader_ARADDR[20]} {obj_axi_reader_ARADDR[21]} {obj_axi_reader_ARADDR[22]} {obj_axi_reader_ARADDR[23]} {obj_axi_reader_ARADDR[24]} {obj_axi_reader_ARADDR[25]} {obj_axi_reader_ARADDR[26]} {obj_axi_reader_ARADDR[27]} {obj_axi_reader_ARADDR[28]} {obj_axi_reader_ARADDR[29]} {obj_axi_reader_ARADDR[30]} {obj_axi_reader_ARADDR[31]}]]
create_debug_port u_ila_0_0 probe
set_property port_width 2 [get_debug_ports u_ila_0_0/probe6]
connect_debug_port u_ila_0_0/probe6 [get_nets [list {obj_axi_reader_ARBURST[0]} {obj_axi_reader_ARBURST[1]}]]
create_debug_port u_ila_0_0 probe
set_property port_width 4 [get_debug_ports u_ila_0_0/probe7]
connect_debug_port u_ila_0_0/probe7 [get_nets [list {obj_axi_reader_ARCACHE[0]} {obj_axi_reader_ARCACHE[1]} {obj_axi_reader_ARCACHE[2]} {obj_axi_reader_ARCACHE[3]}]]
create_debug_port u_ila_0_0 probe
set_property port_width 32 [get_debug_ports u_ila_0_0/probe8]
connect_debug_port u_ila_0_0/probe8 [get_nets [list {obj_axi_writer_AWADDR[0]} {obj_axi_writer_AWADDR[1]} {obj_axi_writer_AWADDR[2]} {obj_axi_writer_AWADDR[3]} {obj_axi_writer_AWADDR[4]} {obj_axi_writer_AWADDR[5]} {obj_axi_writer_AWADDR[6]} {obj_axi_writer_AWADDR[7]} {obj_axi_writer_AWADDR[8]} {obj_axi_writer_AWADDR[9]} {obj_axi_writer_AWADDR[10]} {obj_axi_writer_AWADDR[11]} {obj_axi_writer_AWADDR[12]} {obj_axi_writer_AWADDR[13]} {obj_axi_writer_AWADDR[14]} {obj_axi_writer_AWADDR[15]} {obj_axi_writer_AWADDR[16]} {obj_axi_writer_AWADDR[17]} {obj_axi_writer_AWADDR[18]} {obj_axi_writer_AWADDR[19]} {obj_axi_writer_AWADDR[20]} {obj_axi_writer_AWADDR[21]} {obj_axi_writer_AWADDR[22]} {obj_axi_writer_AWADDR[23]} {obj_axi_writer_AWADDR[24]} {obj_axi_writer_AWADDR[25]} {obj_axi_writer_AWADDR[26]} {obj_axi_writer_AWADDR[27]} {obj_axi_writer_AWADDR[28]} {obj_axi_writer_AWADDR[29]} {obj_axi_writer_AWADDR[30]} {obj_axi_writer_AWADDR[31]}]]
create_debug_port u_ila_0_0 probe
set_property port_width 3 [get_debug_ports u_ila_0_0/probe9]
connect_debug_port u_ila_0_0/probe9 [get_nets [list {obj_axi_reader_ARPROT[0]} {obj_axi_reader_ARPROT[1]} {obj_axi_reader_ARPROT[2]}]]
create_debug_port u_ila_0_0 probe
set_property port_width 1 [get_debug_ports u_ila_0_0/probe10]
connect_debug_port u_ila_0_0/probe10 [get_nets [list obj_axi_reader_ARVALID]]
create_debug_port u_ila_0_0 probe
set_property port_width 1 [get_debug_ports u_ila_0_0/probe11]
connect_debug_port u_ila_0_0/probe11 [get_nets [list obj_axi_reader_RREADY]]
create_debug_port u_ila_0_0 probe
set_property port_width 1 [get_debug_ports u_ila_0_0/probe12]
connect_debug_port u_ila_0_0/probe12 [get_nets [list obj_axi_writer_AWVALID]]
create_debug_port u_ila_0_0 probe
set_property port_width 1 [get_debug_ports u_ila_0_0/probe13]
connect_debug_port u_ila_0_0/probe13 [get_nets [list obj_axi_writer_BREADY]]
create_debug_port u_ila_0_0 probe
set_property port_width 1 [get_debug_ports u_ila_0_0/probe14]
connect_debug_port u_ila_0_0/probe14 [get_nets [list obj_axi_writer_WVALID]]
create_debug_port u_ila_0_0 probe
set_property port_width 1 [get_debug_ports u_ila_0_0/probe15]
connect_debug_port u_ila_0_0/probe15 [get_nets [list obj_obj_axi_reader_ARREADY]]
create_debug_port u_ila_0_0 probe
set_property port_width 1 [get_debug_ports u_ila_0_0/probe16]
connect_debug_port u_ila_0_0/probe16 [get_nets [list obj_obj_axi_reader_RLAST]]
create_debug_port u_ila_0_0 probe
set_property port_width 1 [get_debug_ports u_ila_0_0/probe17]
connect_debug_port u_ila_0_0/probe17 [get_nets [list obj_obj_axi_reader_RVALID]]
create_debug_port u_ila_0_0 probe
set_property port_width 1 [get_debug_ports u_ila_0_0/probe18]
connect_debug_port u_ila_0_0/probe18 [get_nets [list obj_obj_axi_writer_AWREADY]]
create_debug_port u_ila_0_0 probe
set_property port_width 1 [get_debug_ports u_ila_0_0/probe19]
connect_debug_port u_ila_0_0/probe19 [get_nets [list obj_obj_axi_writer_BVALID]]
create_debug_port u_ila_0_0 probe
set_property port_width 1 [get_debug_ports u_ila_0_0/probe20]
connect_debug_port u_ila_0_0/probe20 [get_nets [list obj_obj_axi_writer_WREADY]]
create_debug_port u_ila_0_0 probe
set_property port_width 1 [get_debug_ports u_ila_0_0/probe21]
connect_debug_port u_ila_0_0/probe21 [get_nets [list U_CANVAS/read_data_req]]
create_debug_port u_ila_0_0 probe
set_property port_width 1 [get_debug_ports u_ila_0_0/probe22]
connect_debug_port u_ila_0_0/probe22 [get_nets [list test_req]]
create_debug_port u_ila_0_0 probe
set_property port_width 1 [get_debug_ports u_ila_0_0/probe23]
connect_debug_port u_ila_0_0/probe23 [get_nets [list U_CANVAS/write_data_req]]
set_property C_CLK_INPUT_FREQ_HZ 300000000 [get_debug_cores dbg_hub]
set_property C_ENABLE_CLK_DIVIDER false [get_debug_cores dbg_hub]
set_property C_USER_SCAN_CHAIN 1 [get_debug_cores dbg_hub]
connect_debug_port dbg_hub/clk [get_nets wr_clk]
