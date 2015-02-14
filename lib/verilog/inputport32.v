`default_nettype none

module inputport32
  #(
    parameter WIDTH = 32
    )
   (
    input wire 		  clk,
    input wire 		  reset,

    input wire [WIDTH-1:0] din,
    output wire signed [WIDTH-1:0] value
    );
   
   assign value = din;
  
endmodule

`default_nettype wire
