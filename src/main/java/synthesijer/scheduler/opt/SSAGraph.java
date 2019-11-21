package synthesijer.scheduler.opt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Enumeration;

import synthesijer.SynthesijerUtils;
import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;

public class SSAGraph{
  ArrayList<SSAGraphNode> ssa_nodes;  

  public SSAGraph(ControlFlowGraph cg){
    //System.out.println("スロット数: "+cg.slots.length);
    ssa_nodes = buildAll(cg.slots);
    toString(ssa_nodes);
  }

  public ArrayList<SSAGraphNode> buildAll(SchedulerSlot[] slots){
    ArrayList<SSAGraphNode> nodes = new ArrayList<>();
    int count = 1;

    for(SchedulerSlot s: slots){
      nodes.add(genSSANode(s, count));
      count ++;
    }
    return nodes;
  }

  public SSAGraphNode genSSANode(SchedulerSlot slot, int count){
    SSAGraphNode n = new SSAGraphNode();
    SchedulerItem[] items = slot.getItems();
    n.num = count;
    for(SchedulerItem i: items){
      if(i.dest == null){
        n.type = i.op.toString();
      }else{
        if(i.toSexp().contains("ADD")){
          n.type = "operator";
        }else{
          n.type = "operand";
        }
        n.dest = i.dest.getName();
      }
    }
    n.sequence = slot.getStepId();
    return n;
  }

  public void toString(ArrayList<SSAGraphNode> nodes){
    System.out.println("SSAGraphNodes ---");
    for(SSAGraphNode n: nodes){
      System.out.print("("+n.sequence+") ");
      switch(n.type){
        case "operator":
          System.out.println("OP: + ->");
          break;
        case "operand":
          System.out.println("x : 10 ->");
          break;
        default:
          System.out.println(n.type+" ->");
      }
    }
    System.out.println("");
  }
}

class SSAGraphNode{
  public int num;
  public String type;
  public int sequence;
  public String dest;
}

class SSAGraphEdge{
}
