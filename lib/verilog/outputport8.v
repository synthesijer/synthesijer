`default_nettype none

module outputport8
  #(
    parameter WIDTH = 8
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
