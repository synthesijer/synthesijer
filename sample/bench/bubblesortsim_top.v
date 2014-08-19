module bubblesim_top
(

);


  reg  clk = 1'b0;
  reg  reset = 1'b0;
  reg signed [32-1 : 0] counter = 0;
  parameter main_IDLE = 32'd0;
  parameter main_S0 = 32'd1;
  reg [31:0] main = main_IDLE;
  reg signed [32-1 : 0] main_delay = 0;
  wire signed [32-1 : 0] tmp_0001;
  wire  tmp_0002;
  wire  tmp_0003;
  wire  tmp_0004;
  wire  tmp_0005;



  assign tmp_0001 = counter + 1;
  assign tmp_0002 = counter > 3 ? 1'b1 : 1'b0;
  assign tmp_0003 = counter < 8 ? 1'b1 : 1'b0;
  assign tmp_0004 = tmp_0002 && tmp_0003;
  assign tmp_0005 = tmp_0004 == 1'b1 ? 1'b1 : 1'b0;

  always begin
  // state main = main_IDLE
  #10
  main <= main_S0;
  // state main = main_S0
  #10
  main <= main_IDLE;
  end


  always @(main) begin
     if (main == main_IDLE) begin
      clk <= 1'b0;
    end else if (main == main_S0) begin
      clk <= 1'b1;
    end
  end

  always @(main) begin
    reset <= tmp_0005;
  end

  always @(main) begin
     if (main == main_IDLE) begin
      counter <= tmp_0001;
    end else if (main == main_S0) begin
      counter <= tmp_0001;
    end
  end

  wire finish_flag;

  BubbleSortSim U(
    .clk(clk),
    .reset(reset),
    .field_finish_flag_output(finish_flag),
    .field_finish_flag_input(0),
    .field_finish_flag_input_we(0),
    .run_req(1),
    .run_busy(),
    .start_req(0),
    .start_busy(),
    .join_req(0),
    .join_busy(),
    .yield_req(0),
    .yield_busy()
    );

  always @(posedge clk) begin
    if (finish_flag == 1'b1) begin
      $write("end\n");
      $finish;
    end
  end

endmodule
