package synthesijer.scheduler.opt;

import java.io.*;

import synthesijer.SynthesijerUtils;
import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;

public class PBQP implements SchedulerInfoOptimizer{
  public PBQP(){
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
}