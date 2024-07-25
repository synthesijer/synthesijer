`default_nettype none
  
module sim_Test007;
   
   reg clk   = 1'b0;
   reg reset = 1'b0;
   reg [31:0] counter = 32'h0;
   reg run_req = 1'b0;
   
   wire test_return;
   wire run_busy;
   
   Test007 u(
	     .clk(clk),
	     .reset(reset),
	     .in_din_exp(1'b1),
	     .out_dout_exp(),
	     .in16_din_exp(16'd100),
	     .out16_dout_exp(),
	     .in32_din_exp(32'd200),
	     .out32_dout_exp(),
	     .test_t1(1'b1),
	     .test_t2(100),
	     .test_t3(200),
	     .run_busy(),
	     .run_req(1'b0),
	     .test_return(test_return),
	     .test_busy(run_busy),
	     .test_req(run_req)
	     );
   
   initial begin
      `ifdef DUMP_ENABLE
      $dumpfile("sim_Test007.vcd");
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
            $display("Test007: TEST SUCCESS");
	 end else begin
            $display("Test007: TEST *** FAILURE ***");
            $fatal(1);
	 end
	 $finish;
      end
   end
   
endmodule // sim_Test007
`default_nettype wire
