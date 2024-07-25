`default_nettype none
  
module sim_Test001;
   
   reg clk   = 1'b0;
   reg reset = 1'b0;
   reg [31:0] counter = 32'h0;
   reg run_req = 1'b0;
   
   wire test_return;
   wire run_busy;
   
   Test001 u(
	     .clk(clk),
	     .reset(reset),
	     .acc_y(32'h0),
	     .add_x(32'h0),
	     .add_y(32'h0),
	     .add2_x(32'h0),
	     .add2_y(32'h0),
	     .acc2_num(32'h0),
	     .acc2_y(32'h0),
	     .acc3_num(32'h0),
	     .acc3_y(32'h0),
	     .acc4_num(32'h0),
	     .acc4_y(32'h0),
	     .switch_test_x(32'h0),
	     .acc_return(),
	     .acc_busy(),
	     .acc_req(1'b0),
	     .add_return(),
	     .add_busy(),
	     .add_req(1'b0),
	     .add2_return(),
	     .add2_busy(),
	     .add2_req(1'b0),
	     .acc2_return(),
	     .acc2_busy(),
	     .acc2_req(1'b0),
	     .acc3_return(),
	     .acc3_busy(),
	     .acc3_req(1'b0),
	     .acc4_return(),
	     .acc4_busy(),
	     .acc4_req(1'b0),
	     .switch_test_return(),
	     .switch_test_busy(),
	     .switch_test_req(1'b0),
	     .test_return(test_return),
	     .test_busy(run_busy),
	     .test_req(run_req)
	     );

   initial begin
      `ifdef DUMP_ENABLE
      $dumpfile("sim_Test001.vcd");
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
            $display("Test001: TEST SUCCESS");
	 end else begin
            $display("Test001: TEST *** FAILURE ***");
            $fatal(1);
	 end
	 $finish;
      end
   end
   
endmodule // sim_Test001
`default_nettype wire
