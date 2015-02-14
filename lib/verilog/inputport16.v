`default_nettype none

module inputport16
  #(
    parameter WIDTH = 16
    )
   (
    input wire 		  clk,
    input wire 		  reset,

    input wire [WIDTH-1:0] din,
    output signed [WIDTH-1:0] value
    );
   
   assign value = din;
  
endmodule

`default_nettype wire
