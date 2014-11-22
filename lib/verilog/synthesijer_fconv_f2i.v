`default_nettype none

module synthesijer_fconv_f2i
  (
   input wire 		     clk,
   input wire 		     reset,
   input wire [31:0] 	     a,
   input wire 		     nd,
   output wire signed [31:0] result,
   output wire 		     valid
   );

   fconv_f2i_ip U(.aclk(clk),
		  .s_axis_a_tdata(a),
		  .s_axis_a_tvalid(nd),
		  .m_axis_result_tvalid(valid),
		  .m_axis_result_tdata(result)
		  );

endmodule // synthesijer_fconv_f2i

`default_nettype wire
