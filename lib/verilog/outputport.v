`default_nettype none

module outputport
  #(
    parameter WIDTH = 32
    )
   (
    input wire 		   clk,
    input wire 		   reset,

    output wire 	   dout,
    input wire [WIDTH-1:0] value
    );

   assign dout = value;
  
endmodule

`default_nettype wire
