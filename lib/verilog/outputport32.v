`default_nettype none

module outputport32
  #(
    parameter WIDTH = 32
    )
   (
    input wire 		   clk,
    input wire 		   reset,

    output wire [WIDTH-1:0] dout,
    input wire signed [WIDTH-1:0] value
    );

   assign dout = value;
  
endmodule

`default_nettype wire
