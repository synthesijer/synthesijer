`default_nettype none
  
module sim_Test002;
   
   reg clk   = 1'b0;
   reg reset = 1'b0;
   reg [31:0] counter = 32'h0;
   reg run_req = 1'b0;
   
   wire test_return;
   wire run_busy;
   
   Test002 u(
	     .clk(clk),
	     .reset(reset),
	     .a_address(0), .a_we(1'b0), .a_oe(1'b0), .a_din(32'h0), .a_dout(), .a_length(),
	     .x_in(32'h0), .x_we(1'b0), .x_out(),
	     .y_in(32'h0), .y_we(1'b0), .y_out(),
	     .dec_i(32'h0),
	     .inc_i(32'h0),
	     .copy_i(32'h0), .copy_j(32'h0),
	     .set_i(32'h0), .set_v(32'h0),
	     .get_i(32'h0),
	     .switch_test_x(32'h0),
	     .init_busy(), .init_req(1'b0),
	     .dec_return(), .dec_busy(), .dec_req(1'b0),
	     .inc_return(), .inc_busy(), .inc_req(1'b0),
	     .copy_busy(), .copy_req(1'b0),
	     .set_busy(), .set_req(1'b0),
	     .get_return(), .get_busy(), .get_req(1'b0),
	     .switch_test_return(), .switch_test_busy(), .switch_test_req(1'b0),
	     .sum_x_y_return(), .sum_x_y_busy(), .sum_x_y_req(1'b0),
	     .test_return(test_return),
	     .test_busy(run_busy),
	     .test_req(run_req)
	     );
   
   initial begin
      `ifdef DUMP_ENABLE
      $dumpfile("sim_Test002.vcd");
      $dumpvars();
      `endif
   end

   always #5
     clk <= !clk;

   always @(posedge clk) begin
      counter <= counter + 1;
      if(counter >= 3 && counter <= 8) begin
	 reset <= 1'b1;
      end else begin
	 reset <= 1'b0;
      end
      
      if(counter > 100)
	run_req <= 1'b1;
      if(counter > 10000 || (run_busy == 0 && counter > 105)) begin
	 if(test_return == 1) begin
            $display("Test002: TEST SUCCESS");
	 end else begin
            $display("Test002: TEST *** FAILURE ***");
	 end
	 $finish;
      end
   end
   
endmodule // sim_Test002
`default_nettype wire
