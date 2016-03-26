#include "VPrimeSim.h"
int sc_main(int argc, char **argv) {

    Verilated::commandArgs(argc, argv);

    sc_clock clk ("clk", 10, SC_NS, 0.5, 1, SC_NS, true);

    sc_signal<bool> reset;
    sc_signal<bool> finish_flag_in;
    sc_signal<bool> finish_flag_we;
    sc_signal<bool> finish_flag_out;
    sc_signal<bool> run_busy;
    sc_signal<bool> run_req;
    sc_signal<bool> start_busy;
    sc_signal<bool> start_req;
    sc_signal<bool> join_busy;
    sc_signal<bool> join_req;
    sc_signal<bool> yield_busy;
    sc_signal<bool> yield_req;
    sc_signal<bool> result_we;
    sc_signal<uint32_t> result_in;
    sc_signal<uint32_t> result_out;
    
    VPrimeSim top("top");
    top.clk(clk);

    top.reset(reset);
    top.finish_flag_in(finish_flag_in);
    top.finish_flag_we(finish_flag_we);
    top.finish_flag_out(finish_flag_out);
    top.run_busy(run_busy);
    top.run_req(run_req);
    top.start_busy(start_busy);
    top.start_req(start_req);
    top.join_busy(join_req);
    top.join_req(join_req);
    top.yield_busy(yield_busy);
    top.yield_req(yield_req);
    top.result_we(result_we);
    top.result_in(result_in);
    top.result_out(result_out);

    reset = 0;
    finish_flag_in = 0;
    finish_flag_we = 0;
    run_req = 0;
    start_req = 0;
    join_req = 0;
    yield_req = 0;
    result_we = 0;
    result_in = 0;

    // 検証開始
    reset = 1;
    run_req = 0;

    sc_start(100, SC_NS);
    reset = 0;

    sc_start(100, SC_NS);
    run_req = 1;
    
    sc_start(10, SC_NS);
    run_req = 0;
    
    sc_start(10, SC_NS);
    while(finish_flag_out == 0)
        sc_start(10, SC_NS);

    cout << result_out << endl;

    exit(0);
}
