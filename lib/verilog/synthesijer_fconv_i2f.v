`default_nettype none

module synthesijer_fconv_i2f
  (
   input wire 		    clk,
   input wire 		    reset,
   input wire signed [31:0] a,
   input wire 		    nd,
   output wire [31:0] 	    result,
   output wire 		    valid
   );

   fconv_i2f_ip U(.aclk(clk),
		  .s_axis_a_tdata(a),
		  .s_axis_a_tvalid(nd),
		  .m_axis_result_tvalid(valid),
		  .m_axis_result_tdata(result)
		  );

endmodule // synthesijer_fconv_i2f

`default_nettype wire
