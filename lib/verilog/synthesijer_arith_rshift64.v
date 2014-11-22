`default_nettype none

module synthesijer_arith_rshift64
  (
   input wire 		     clk,
   input wire 		     reset,
   input wire signed [63:0]  a,
   input wire signed [63:0]  b,
   input wire 		     nd,
  
   output wire signed [63:0] result,
   output wire 		     valid
   );

   assign result = a >>> b[5:0];
   assign valid = 1'b1;

endmodule

`default_nettype wire
