
`default_nettype none

module synthesijer_div(clk,
		       reset,
		       a,
		       b,
		       nd,
		       quantient,
		       remainder,
		       valid
		       );
   parameter WIDTH = 32;
   
   input wire clk;
   input wire reset;
   
   input signed [WIDTH-1:0] a;
   input signed [WIDTH-1:0] b;
   
   input wire nd;

   output signed [WIDTH-1:0] quantient;
   output signed [WIDTH-1:0] remainder;

   output wire valid;

   reg [7:0] counter = 8'h0;
   reg 	     oe;

   reg [WIDTH-1:0] b_reg = 0;
   reg [2*WIDTH-1+1:0] v = 0;

   reg q_sign, a_sign;

   function signed [WIDTH-1:0] with_sign;
      input signed [WIDTH-1:0] v;
      input s;
      
      begin
	 if(s == 1'b0)
	   with_sign = v;
	 else
	   with_sign = (~v) + 1;
      end
   endfunction

   function signed [WIDTH-1:0] my_abs;
      input signed [WIDTH-1:0] v;
      begin
	 if(v[WIDTH-1] == 1'b0)
	   my_abs = v;
	 else
	   my_abs = (~v) + 1;
      end
   endfunction
   
   assign quantient = with_sign(v[WIDTH-1:0], q_sign);
   assign remainder = with_sign(v[2*WIDTH:WIDTH+1], a_sign);
   assign valid     = oe;

   always @(posedge clk) begin
      if(reset == 1'b1) begin
        counter <= 8'h0;
      end else begin
         if(nd == 1'b1) begin
            counter <= WIDTH + 1;
	 end else if(counter > 0) begin
            counter <= counter - 1;
	 end
      end
   end

   always @(posedge clk) begin
      if(reset == 1'b1) begin
         oe <= 1'b0;
      end else begin
	 if(counter == 1) begin
            oe <= 1'b1;
	 end else begin
            oe <= 1'b0;
	 end
      end
   end

   reg [WIDTH-1+1:0] tmp;
   always @(posedge clk) begin
      if(counter == 0) begin
         v[2*WIDTH:WIDTH] <= 0;
	 v[WIDTH-1:0] <= my_abs(a);
         b_reg <= my_abs(b);
         q_sign <= a[WIDTH-1] ^ b[WIDTH-1];
         a_sign <= a[WIDTH-1];
      end else begin // counter > 0
         tmp = v[2 * WIDTH:WIDTH] - {1'b0, b_reg};
         if(tmp[WIDTH] == 1'b0) begin
	    v <= {tmp[WIDTH-1:0], v[WIDTH-1:0], 1'b1};
         end else begin
            v <= {v[2*WIDTH-1:0], 1'b0};
         end
      end
   end
   
endmodule // synthesijer_div

`default_nettype wire
