`default_nettype none

module inputport
  #(
    paramter WIDTH = 32
    )
   (
    input wire 		  clk,
    input wire 		  reset,

    input wire [WIDTH-1:0] din,
    input wire [WIDHT-1:0] value
    );
   
   assign value = din;
  
endmodule

`default_nettype wire
