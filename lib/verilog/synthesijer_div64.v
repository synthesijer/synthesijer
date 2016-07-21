`default_nettype none

module synthesijer_div64
  (
   input wire 		     clk,
   input wire 		     reset,
   input wire signed [63:0]  a,
   input wire signed [63:0]  b,
   input wire 		     nd,
   output wire signed [63:0] quantient,
   output wire signed [63:0] remainder,
   output wire 		     valid
   );

   defparam U.WIDTH = 64;
   synthesijer_div U(.clk(clk),
		     .reset(reset),
		     .a(a),
		     .b(b),
		     .nd(nd),
		     .quantient(quantient),
		     .remainder(remainder),
		     .valid(valid)
		     );
   
   // wire [127:0] 	     result_tmp;
   // wire 		     valid_tmp;

   // div64_ip U(.aclk(clk),
   // 	      .aresetn(~reset),
   // 	      .s_axis_dividend_tdata(a),
   // 	      .s_axis_dividend_tvalid(nd),
   // 	      .s_axis_dividend_tready(),
   // 	      .s_axis_divisor_tdata(b),
   // 	      .s_axis_divisor_tvalid(nd),
   // 	      .s_axis_divisor_tready(),
   // 	      .m_axis_dout_tvalid(valid_tmp),
   // 	      .m_axis_dout_tdata(result_tmp)
   // 	      );

   // assign valid = valid_tmp;
   // assign quantient = result_tmp[127:64];
   // assign remainder = result_tmp[63:0];
   
endmodule // synthesijer_div64

`default_nettype none
