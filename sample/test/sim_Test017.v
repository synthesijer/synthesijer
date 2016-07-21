`default_nettype none
  
module sim_Test017;
   
   reg clk   = 1'b0;
   reg reset = 1'b0;
   reg [31:0] counter = 32'h0;
   reg run_req = 1'b0;
   
   wire test_return;
   wire run_busy;
   
   Test017 U(
	     .clk(clk),
	     .reset(reset),
	     .test0_in(32'h0),
	     .test0_we(1'b0),
	     .test0_out(),
	     .test1_in(32'h0),
	     .test1_we(1'b0),
	     .test1_out(),
	     .test2_in(32'h0),
	     .test2_we(1'b0),
	     .test2_out(),
	     .test3_in(32'h0),
	     .test3_we(1'b0),
	     .test3_out(),
	     .test_idx(32'h0),
	     .test_return(test_return),
	     .test_busy(run_busy),
	     .test_req(run_req)
	     );
   
   initial begin
      `ifdef DUMP_ENABLE
      $dumpfile("sim_Test017.vcd");
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
            $display("Test017: TEST SUCCESS");
	 end else begin
            $display("Test017: TEST *** FAILURE ***");
	 end
	 $finish;
      end
   end
   
endmodule // sim_Test017
`default_nettype wire
