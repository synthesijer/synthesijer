`default_nettype none
  
module sim_Test_ArithInt;
   
   reg clk   = 1'b0;
   reg reset = 1'b0;
   reg [31:0] counter = 32'h0;
   reg run_req = 1'b0;
   
   wire test_return;
   wire run_busy;

   wire [63:0] lc;
   wire [31:0] ic;
   
   Test_ArithInt u(
	      .clk(clk),
	      .reset(reset),
	      .ic_in(0),
	      .ic_we(1'b0),
	      .ic_out(ic),
	      .lc_in(0),
	      .lc_we(1'b0),
	      .lc_out(lc),
	      .test_return(test_return),
	      .test_busy(run_busy),
	      .test_req(run_req)
	      );

   initial begin
      `ifdef DUMP_ENABLE
      $dumpfile("sim_Test_ArithInt.vcd");
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
      if(counter > 1000000 || (run_busy == 0 && counter > 105)) begin
	 if(test_return == 1) begin
            $display("Test_ArithInt: TEST SUCCESS");
	 end else begin
            $display("Test_ArithInt: TEST *** FAILURE ***");
	 end
	 $finish;
      end
   end
   
endmodule // sim_Test_ArithInt

`default_nettype wire
