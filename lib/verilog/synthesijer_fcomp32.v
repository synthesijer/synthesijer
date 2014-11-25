`default_nettype none

module synthesijer_fcomp32
  (
   input wire 	     clk,
   input wire 	     reset,
   input wire [31:0] a,
   input wire [31:0] b,
   input wire [7:0]  opcode,
   input wire 	     nd,
   output wire 	     result,
   output wire 	     valid
   );

   wire [7:0] 	     result_tmp;

   fcomp32_ip U(.aclk(clk),
	       .s_axis_a_tdata(a),
	       .s_axis_a_tvalid(nd),
	       .s_axis_b_tdata(b),
	       .s_axis_b_tvalid(nd),
	       .s_axis_operation_tdata(opcode),
	       .s_axis_operation_tvalid(nd),
	       .m_axis_result_tvalid(valid),
	       .m_axis_result_tdata(result_tmp)
	       );
   
   assign result = result_tmp[0];

endmodule // synthesijer_fcomp32

`default_nettype wire
