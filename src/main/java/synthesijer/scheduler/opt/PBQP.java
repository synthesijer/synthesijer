package synthesijer.scheduler.opt;

import java.io.*;
import java.util.ArrayList;

import synthesijer.SynthesijerUtils;
import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;

public class PBQP implements SchedulerInfoOptimizer{
  public int confirmed_rule; 

  public PBQP(ArrayList<Integer> rules){
    int rule = rules.get(0);
    int min_cost = 1000;
    for(int r : rules){
      int cost = calcCost(r);
      if(cost < min_cost){
        min_cost = cost;
        rule = r;
      }
    }
    confirmed_rule = rule;
  }

  public SchedulerInfo opt(SchedulerInfo info){
    SchedulerInfo result = info.getSameInfo();
    for(SchedulerBoard b: info.getBoardsList()){
        result.addBoard(b);
    }
    return result;
  }

  public String getKey(){
    return "pbqp";
  }

  // コストを計算する 未完成
  public int calcCost(int r){
    return 3;
  }
}