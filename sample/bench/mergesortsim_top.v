`default_nettype none
  
module mergesortsim_top;
   
   reg clk   = 1'b0;
   reg reset = 1'b0;
   reg [31:0] counter = 32'h0;
   reg run_req = 1'b0;
   
   wire test_return;
   wire run_busy;
   
   MergeSortSim U (.clk(clk),
	       .reset(reset),
	       .finish_flag_in(1'b0),
	       .finish_flag_we(1'b0),
	       .finish_flag_out(),
	       .run_busy(),
	       .run_req(1'b0),
	       .test_return(test_return),
	       .test_busy(run_busy),
	       .test_req(run_req),
	       .start_busy(),
	       .start_req(1'b0),
	       .join_busy(),
	       .join_req(1'b0),
	       .yield_busy(),
	       .yield_req(1'b0)
	       );
   
   initial begin
      `ifdef DUMP_ENABLE
      $dumpfile("mergesortsim_top.vcd");
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
      if(counter > 200000000 || (run_busy == 0 && counter > 105)) begin
	 if(test_return == 1) begin
            $display("MergeSortSim: TEST SUCCESS");
	 end else begin
            $display("MergeSortSim: TEST *** FAILURE ***");
	 end
	 $finish;
      end
   end
   
endmodule // primesim_top

`default_nettype wire
