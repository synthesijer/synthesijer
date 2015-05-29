`default_nettype none

module singleportram #( parameter WIDTH = 32, DEPTH = 10, WORDS =1024 )
   (
    input wire 		    clk,
    input wire 		    reset,
    output wire [31:0] 	    length,
    input wire [31:0] 	    address_b,
    input wire [WIDTH-1:0]  din_b,
    output wire [WIDTH-1:0] dout_b,
    input wire 		    we_b,
    input wire 		    oe_b
    );

   (* ram_style = "block" *) reg [WIDTH-1:0] 	    mem [WORDS-1:0];
   reg [WIDTH-1:0] 	    dout_r;
   assign dout_b = dout_r;

   assign length = WORDS;

   always@(posedge clk) begin
      if(we_b) begin
	 mem[address_b[DEPTH-1:0]] <= din_b;
      end
   end

   always@(posedge clk) begin
      dout_r <= mem[address_b[DEPTH-1:0]];
   end

endmodule

`default_nettype wire
