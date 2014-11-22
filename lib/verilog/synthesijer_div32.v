`default_nettype none

module synthesijer_div32
  (
   input wire 		     clk,
   input wire 		     reset,
   input wire signed [31:0]  a,
   input wire signed [31:0]  b,
   input wire 		     nd,
   output wire signed [31:0] quantient,
   output wire signed [31:0] remainder,
   output wire 		     valid
   );

   wire [63:0] 		     result_tmp;
   wire 		     valid_tmp;

   div32_ip U(.aclk(clk),
	      .aresetn(~reset),
	      .s_axis_dividend_tdata(a),
	      .s_axis_dividend_tvalid(nd),
	      .s_axis_dividend_tready(),
	      .s_axis_divisor_tdata(b),
	      .s_axis_divisor_tvalid(nd),
	      .s_axis_divisor_tready(),
	      .m_axis_dout_tvalid(valid_tmp),
	      .m_axis_dout_tdata(result_tmp)
	      );

   assign valid = valid_tmp;
   assign quantient = result_tmp[63:32];
   assign remainder = result_tmp[31:0];
   
endmodule // synthesijer_div32

`default_nettype none
