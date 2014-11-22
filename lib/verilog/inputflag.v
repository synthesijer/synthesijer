`default_nettype none
  
module inputflag
  (
   input wire  clk,
   input wire  reset,

   input wire  din,
   output wire flag
   );

   assign flag = din;
  
endmodule

`default_nettype wire

