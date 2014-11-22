`default_nettype none

module synthesijer_fconv_d2l
  (
   input wire 		clk,
   input wire 		reset,
   input wire [63:0] 	a,
   input wire 		nd,
   output wire signed [63:0] result,
   output wire 		valid
   );

   fconv_d2l_ip U(.aclk(clk),
		  .s_axis_a_tdata(a),
		  .s_axis_a_tvalid(nd),
		  .m_axis_result_tvalid(valid),
		  .m_axis_result_tdata(result)
		  );

endmodule // synthesijer_fconv_d2l

`default_nettype wire
