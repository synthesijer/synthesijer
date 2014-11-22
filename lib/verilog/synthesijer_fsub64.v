`default_nettype none

module synthesijer_fsub64
  (
   input wire 	      clk,
   input wire 	      reset,
   input wire [63:0]  a,
   input wire [63:0]  b,
   input wire 	      nd,
   output wire [63:0] result,
   output wire 	      valid
   );

   fsub64_ip U(.aclk(clk),
	       .s_axis_a_tdata(a),
	       .s_axis_a_tvalid(nd),
	       .s_axis_b_tdata(b),
	       .s_axis_b_tvalid(nd),
	       .m_axis_result_tvalid(valid),
	       .m_axis_result_tdata(result)
	       );
   
endmodule // synthesijer_fsub64

`default_nettype wire
