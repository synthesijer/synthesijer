
module simple_dualportram #( parameter WIDTH = 32, DEPTH = 10, WORDS =1024 )
(
	input  wire clk,
        input  wire reset,
	output wire [31:0] length,
	input  wire [31:0] raddress,
	input  wire [31:0] waddress,
	input  wire [WIDTH-1:0] din,
	output wire [WIDTH-1:0] dout,
	input  wire we
);

	reg [WIDTH-1:0] mem [WORDS-1:0];
	reg [WIDTH-1:0] dout_r;
	assign dout = dout_r;

	assign length = WORDS;

	always@(posedge clk) begin
		if(we) begin
			mem[waddress[DEPTH-1:0]] <= din;
		end
	end

	always@(posedge clk) begin
		dout_r <= mem[raddress[DEPTH-1:0]];
	end

endmodule
