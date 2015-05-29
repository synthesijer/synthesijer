module dualportram #(
    parameter DEPTH = 10,
    parameter WIDTH = 32,
    parameter WORDS = 1024
) (
    input wire 		    clk,
    input wire 		    reset,
   
    input wire 		    we,
    input wire 		    oe,
    input wire [31:0] 	    address,
    input wire [WIDTH-1:0]  din,
    output wire [WIDTH-1:0] dout,
     
    input wire 		    we_b,
    input wire 		    oe_b,
    input wire [31:0] 	    address_b,
    input wire [WIDTH-1:0]  din_b,
    output wire [WIDTH-1:0] dout_b,
   
    output wire [31:0] 	    length
);
 
   (* ram_style = "block" *) reg [WIDTH-1:0] mem [WORDS-1:0];
   reg [WIDTH-1:0] q, q_b;

   assign dout   = q;
   assign dout_b = q_b;
   assign length = WORDS;

   always @(posedge clk) begin
      q <= mem[address[DEPTH-1:0]];
      if(we) begin
         mem[address[DEPTH-1:0]] <= din;
      end
   end
 
   always @(posedge clk) begin
      q_b <= mem[address_b[DEPTH-1:0]];
      if(we_b) begin
         mem[address_b[DEPTH-1:0]] <= din_b;
      end
   end
 
endmodule
