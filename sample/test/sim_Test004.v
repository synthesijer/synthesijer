`default_nettype none
  
module sim_Test004;
   
   reg clk   = 1'b0;
   reg reset = 1'b0;
   reg [31:0] counter = 32'h0;
   reg run_req = 1'b0;
   
   wire test_return;
   wire run_busy;
   
   Test004 u(
	     .clk(clk),
	     .reset(reset),
	     .run_busy(),
	     .run_req(),
	     .break_test_busy(),
	     .break_test_req(),
	     .continue_test_return(),
	     .continue_test_busy(),
	     .continue_test_req(),
	     .test_return(test_return),
	     .test_busy(run_busy),
	     .test_req(run_req),
	     .start_busy(),
	     .start_req(),
	     .join_busy(),
	     .join_req(),
	     .yield_busy(),
	     .yield_req()
	     );

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
      if(counter > 1000000 || (run_busy == 0 && counter > 105)) begin
	 if(test_return == 1) begin
            $display("Test004: TEST SUCCESS");
	 end else begin
            $display("Test004: TEST *** FAILURE ***");
	 end
	 $finish;
      end
   end
   
endmodule // sim_Test004
`default_nettype wire
