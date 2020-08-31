`default_nettype none

module sim_Test030();

    reg clk;
    reg reset;

    wire [32-1 : 0] test_a_length;
    wire [32-1 : 0] test_a_address_b;
    wire signed [32-1 : 0] test_a_din_b;
    wire signed [32-1 : 0] test_a_dout_b;
    wire test_a_we_b;
    wire test_a_oe_b;
    wire signed [32-1 : 0] test_b_length;
    wire signed [32-1 : 0] test_b_address_b;
    wire signed [32-1 : 0] test_b_din_b;
    wire signed [32-1 : 0] test_b_dout_b;
    wire test_b_we_b;
    wire test_b_oe_b;
    wire signed [32-1 : 0] test_c_length;
    wire signed [32-1 : 0] test_c_address_b;
    wire signed [32-1 : 0] test_c_din_b;
    wire signed [32-1 : 0] test_c_dout_b;
    wire test_c_we_b;
    wire test_c_oe_b;
    wire test_busy;

    reg test_req;
    reg [31:0] counter;

    reg sim_a_we;
    reg [31:0] sim_a_a;
    reg [31:0] sim_a_d;
    reg sim_b_we;
    reg [31:0] sim_b_a;
    reg [31:0] sim_b_d;

    reg [50:0] test_busy_d;

    initial begin
	clk = 0;
	reset = 1;
	counter = 0;
	test_req = 0;
	sim_a_we = 0;
	sim_a_a = 0;
	sim_a_d = 0;
	sim_b_we = 0;
	sim_b_a = 0;
	sim_b_d = 0;
	$dumpfile("sim_Test030.vcd");
	$dumpvars();
    end

    always begin
	clk = ~clk;
	#5;
    end

    always @(posedge clk) begin
	counter <= counter + 1;

	if(counter == 10) begin
	    reset <= 0;
	end

	if(counter >= 100 && counter < 100+1024) begin
	    sim_a_we <= 1;
	    sim_b_we <= 1;
	    sim_a_a <= counter;
	    sim_a_d <= counter;
	    sim_b_a <= counter;
	    sim_b_d <= counter + counter;
	end else begin
	    sim_a_we <= 0;
	    sim_b_we <= 0;
	end

	if(counter == 2000) begin
	    test_req <= 1;
	end else begin
	    test_req <= 0;
	end

	if(counter > 2100 && test_busy_d[50] == 0) begin
	    $display("done");
	    $finish;
	end

	if(counter > 100000) begin
	    $display("timeout");
	    $finish;
	end

	test_busy_d <= {test_busy_d[50:0], test_busy};

    end

    Test030 dut(.clk(clk),
		.reset(reset),
		.test_a_length(test_a_length),
		.test_a_address_b(test_a_address_b),
		.test_a_din_b(test_a_din_b),
		.test_a_dout_b(test_a_dout_b),
		.test_a_we_b(test_a_we_b),
		.test_a_oe_b(test_a_oe_b),
		.test_b_length(test_b_length),
		.test_b_address_b(test_b_address_b),
		.test_b_din_b(test_b_din_b),
		.test_b_dout_b(test_b_dout_b),
		.test_b_we_b(test_b_we_b),
		.test_b_oe_b(test_b_oe_b),
		.test_c_length(test_c_length),
		.test_c_address_b(test_c_address_b),
		.test_c_din_b(test_c_din_b),
		.test_c_dout_b(test_c_dout_b),
		.test_c_we_b(test_c_we_b),
		.test_c_oe_b(test_c_oe_b),
		.test_busy(test_busy),
		.test_req(test_req));
    
    dualportram#(.DEPTH(10), .WIDTH(32), .WORDS(1024))
    mem_a (.clk(clk),
	   .reset(reset),
	   .we(sim_a_we),
	   .oe(1'b1),
	   .address(sim_a_a),
	   .din(sim_a_d),
	   .dout(),
	   .we_b(test_a_we_b),
	   .oe_b(test_a_oe_b),
	   .address_b(test_a_address_b),
	   .din_b(test_a_din_b),
	   .dout_b(test_a_dout_b),
	   .length(test_a_length));
    dualportram#(.DEPTH(10), .WIDTH(32), .WORDS(1024))
    mem_b (.clk(clk),
	   .reset(reset),
	   .we(sim_b_we),
	   .oe(1'b1),
	   .address(sim_b_a),
	   .din(sim_b_d),
	   .dout(),
	   .we_b(test_b_we_b),
	   .oe_b(test_b_oe_b),
	   .address_b(test_b_address_b),
	   .din_b(test_b_din_b),
	   .dout_b(test_b_dout_b),
	   .length(test_b_length));
    dualportram#(.DEPTH(10), .WIDTH(32), .WORDS(1024))
    mem_c (.clk(clk),
	   .reset(reset),
	   .we(1'b0),
	   .oe(1'b1),
	   .address(0),
	   .din(0),
	   .dout(),
	   .we_b(test_c_we_b),
	   .oe_b(test_c_oe_b),
	   .address_b(test_c_address_b),
	   .din_b(test_c_din_b),
	   .dout_b(test_c_dout_b),
	   .length(test_c_length));

endmodule // sim_Test030

`default_nettype wire
