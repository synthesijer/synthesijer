`default_nettype none

module synthesijer_logic_rshift64
  (
   input wire 		     clk,
   input wire 		     reset,
   input wire signed [63:0]  a,
   input wire signed [63:0]  b,
   input wire 		     nd,
   output wire signed [63:0] result,
   output wire 		     valid
   );

//   wire unsigned [63:0] a_tmp;
//   wire unsigned [63:0] result_tmp;
   wire [63:0] a_tmp;
   wire [63:0] result_tmp;

   assign a_tmp = a;
   
   assign result_tmp = a_tmp >> b[5:0];
   
   assign result = result_tmp;
   assign valid = 1'b1;

endmodule // synthesijer_logic_rshift64

`default_nettype wire
