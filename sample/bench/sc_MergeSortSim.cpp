#include "VMergeSortSim.h"
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
    sc_signal<bool> test_req;
    sc_signal<bool> test_busy;
    sc_signal<bool> test_return;

    VMergeSortSim top("top");
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
    top.test_req(test_req);
    top.test_busy(test_busy);
    top.test_return(test_return);

    reset = 0;
    finish_flag_in = 0;
    finish_flag_we = 0;
    run_req = 0;
    start_req = 0;
    join_req = 0;
    yield_req = 0;

    // 検証開始
    reset = 1;
    run_req = 0;

    sc_start(100, SC_NS);
    reset = 0;

    sc_start(100, SC_NS);
    test_req = 1;
    
    sc_start(10, SC_NS);
    test_req = 0;
    
    sc_start(10, SC_NS);
    while(test_busy == 1)
        sc_start(10, SC_NS);

    cout << test_return << endl;

    exit(0);
}
