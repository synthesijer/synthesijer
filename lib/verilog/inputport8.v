`default_nettype none

module inputport8
  #(
    parameter WIDTH = 8
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
