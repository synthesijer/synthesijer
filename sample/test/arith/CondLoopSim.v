module CondLoopSim();

  reg clk = 0;
  always #5
    clk <= !clk;

  CondLoop u(.clk(clk),
             .reset(0),
             .run_busy(),
             .run_req(0),
             .start_busy(),
             .start_req(1),
             .join_busy(),
             .join_req(0),
             .yield_busy(),
             .yield_req(0)
            );

  initial begin
   $display("fefe");
   $dumpvars(0);
   $dumpfile("hoge.vcd");
  end
endmodule

