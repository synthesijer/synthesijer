`default_nettype none
  
module sim_Test021;
   
   reg clk   = 1'b0;
   reg reset = 1'b0;
   reg [31:0] counter = 32'h0;
   reg run_req = 1'b0;
   
   wire test_return;
   wire run_busy;
   
   Test021_test U(
		  .clk(clk),
		  .reset(reset),
		  .test_return(test_return),
		  .test_busy(run_busy),
		  .test_req(run_req)
		  );
   
   initial begin
      `ifdef DUMP_ENABLE
      $dumpfile("sim_Test021.vcd");
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
            $display("Test021: TEST SUCCESS");
	 end else begin
            $display("Test021: TEST *** FAILURE ***");
	 end
	 $finish;
      end
   end
   
endmodule // sim_Test021
`default_nettype wire
