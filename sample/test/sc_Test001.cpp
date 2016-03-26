#include "VTest001.h"
int sc_main(int argc, char **argv) {

    Verilated::commandArgs(argc, argv);

    sc_clock clk ("clk", 10, SC_NS, 0.5, 1, SC_NS, true);
    sc_signal<bool> reset;

    sc_signal<bool> acc_busy;
    sc_signal<bool> acc_req;
    sc_signal<bool> add_busy;
    sc_signal<bool> add_req;
    sc_signal<bool> add2_busy;
    sc_signal<bool> add2_req;
    sc_signal<bool> acc2_busy;
    sc_signal<bool> acc2_req;
    sc_signal<bool> acc3_busy;
    sc_signal<bool> acc3_req;
    sc_signal<bool> acc4_busy;
    sc_signal<bool> acc4_req;
    sc_signal<bool> switch_test_busy;
    sc_signal<bool> switch_test_req;
    sc_signal<bool> test_return;
    sc_signal<bool> test_busy;
    sc_signal<bool> test_req;
    sc_signal<uint32_t> acc_y;
    sc_signal<uint32_t> add_x;
    sc_signal<uint32_t> add_y;
    sc_signal<uint32_t> add2_x;
    sc_signal<uint32_t> add2_y;
    sc_signal<uint32_t> acc2_num;
    sc_signal<uint32_t> acc2_y;
    sc_signal<uint32_t> acc3_num;
    sc_signal<uint32_t> acc3_y;
    sc_signal<uint32_t> acc4_num;
    sc_signal<uint32_t> acc4_y;
    sc_signal<uint32_t> switch_test_x;
    sc_signal<uint32_t> acc_return;
    sc_signal<uint32_t> add_return;
    sc_signal<uint32_t> add2_return;
    sc_signal<uint32_t> acc2_return;
    sc_signal<uint32_t> acc3_return;
    sc_signal<uint32_t> acc4_return;
    sc_signal<uint32_t> switch_test_return;

    VTest001 top("top");
    top.clk(clk);

    top.reset(reset);
    top.test_req(test_req);
    top.test_busy(test_busy);

    top.acc_y(acc_y);
    top.add_x(add_x);
    top.add_y(add_y);
    top.add2_x(add2_x);
    top.add2_y(add2_y);
    top.acc2_num(acc2_num);
    top.acc2_y(acc2_y);
    top.acc3_num(acc3_num);
    top.acc3_y(acc3_y);
    top.acc4_num(acc4_num);
    top.acc4_y(acc4_y);
    top.switch_test_x(switch_test_x);
    top.acc_return(acc_return);
    top.acc_busy(acc_busy);
    top.acc_req(acc_req);
    top.add_return(add_return);
    top.add_busy(add_busy);
    top.add_req(add_req);
    top.add2_return(add2_return);
    top.add2_busy(add2_busy);
    top.add2_req(add2_req);
    top.acc2_return(acc2_return);
    top.acc2_busy(acc2_busy);
    top.acc2_req(acc2_req);
    top.acc3_return(acc3_return);
    top.acc3_busy(acc3_busy);
    top.acc3_req(acc3_req);
    top.acc4_return(acc4_return);
    top.acc4_busy(acc4_busy);
    top.acc4_req(acc4_req);
    top.switch_test_return(switch_test_return);
    top.switch_test_busy(switch_test_busy);
    top.switch_test_req(switch_test_req);
    top.test_return(test_return);

    acc_req = 0;
    add_req = 0;
    add2_req = 0;
    acc2_req = 0;
    acc3_req = 0;
    acc4_req = 0;
    switch_test_req = 0;
    acc_y = 0;
    add_x = 0;
    add_y = 0;
    add2_x = 0;
    add2_y = 0;
    acc2_num = 0;
    acc2_y = 0;
    acc3_num = 0;
    acc3_y = 0;
    acc4_num = 0;
    acc4_y = 0;
    switch_test_x = 0;

    sc_trace_file *tf = sc_create_vcd_trace_file("sample");
    sc_trace(tf, clk, "clock");
    // 検証開始
    reset = 1;
    test_req = 0;

    sc_start(100, SC_NS);
    reset = 0;

    sc_start(100, SC_NS);
    test_req = 1;
    
    sc_start(10, SC_NS);
    test_req = 0;
    
    sc_start(10, SC_NS);
    while(test_busy)
        sc_start(10, SC_NS);

    //cout << hex << showbase << bcd << endl;
    if(test_return == 0){
      std::cout << "Test001: Test failure" << std::endl;
    }else{
      std::cout << "Test001: Test success" << std::endl;
    }

    exit(0);
}
