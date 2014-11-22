`default_nettype none

module synthesijer_mul64
  (
    input wire 		      clk,
    input wire 		      reset,
    input wire signed [63:0]  a,
    input wire signed [63:0]  b,
    input wire 		      nd,
    output wire signed [63:0] result,
    output wire 	      valid
   );

   wire signed[127:0] tmp;

   assign tmp = a * b;
   assign result = tmp[63:0];
   assign valid = 1'b1;

endmodule // synthesijer_mul64

`default_nettype wire
