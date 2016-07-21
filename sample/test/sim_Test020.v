`default_nettype none
  
module sim_Test020;
   
   reg clk   = 1'b0;
   reg reset = 1'b0;
   reg [31:0] counter = 32'h0;
   reg run_req = 1'b0;
   
   wire test_return;
   wire run_busy;
   
   Test020 U(
	     .clk(clk),
	     .reset(reset),
	     .a_in(32'h0),
	     .a_we(1'b0),
	     .a_out(),
	     .b_in(32'h0),
	     .b_we(1'b0),
	     .b_out(),
	     .c_in(32'h0),
	     .c_we(1'b0),
	     .c_out(),
	     .d_in(32'h0),
	     .d_we(1'b0),
	     .d_out(),
	     .e_in(32'h0),
	     .e_we(1'b0),
	     .e_out(),
	     .f_in(32'h0),
	     .f_we(1'b0),
	     .f_out(),
	     .mem2_address(32'h0),
	     .mem2_we(1'b0),
	     .mem2_oe(1'b1),
	     .mem2_din(32'h0),
	     .mem2_dout(),
	     .mem2_length(),
	     .test_return(test_return),
	     .test_busy(run_busy),
	     .test_req(run_req)
	     );
   
   initial begin
      `ifdef DUMP_ENABLE
      $dumpfile("sim_Test020.vcd");
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
      if(counter > 100000 || (run_busy == 0 && counter > 105)) begin
	 if(test_return == 1) begin
            $display("Test020: TEST SUCCESS");
	 end else begin
            $display("Test020: TEST *** FAILURE ***");
	 end
	 $finish;
      end
   end
   
endmodule // sim_Test020
`default_nettype wire
