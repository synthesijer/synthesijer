`default_nettype none

module outputflag
  (
    input wire 	clk,
    input wire 	reset,

    output wire dout,
    input wire 	flag
   );
   
   assign dout = flag;
  
endmodule

`default_nettype wire
