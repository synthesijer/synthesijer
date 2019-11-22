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
        n.dest = i.dest.getName();
        switch(i.op.toString()){
          case "ASSIGN":
            n.type = "operand";
            String[] tmp = n.dest.split("_",0);
            n.variable_name = tmp[1];
            n.value = i.src[0].getName();
            break;
          case "ADD":
            n.type = "operator";
            n.operator = "+";
            break;
          case "SUB":
            n.type = "operator";
            n.operator = "-";
            break;
          case "MUL32":
            n.type = "operator";
            n.operator = "*";
            break;
          case "DIV32":
            n.type = "operator";
            n.operator = "/";
            break;
          default:
            n.type = "operator";
            n.operator = "まだ";
            break;
        }
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
          System.out.println("OP: "+n.operator+" ->");
          //System.out.println(n.dest);
          break;
        case "operand":
          System.out.println(n.variable_name+" : "+n.value+" ->");
          break;
        default:
          System.out.println(n.type+" ->");
      }
    }
    System.out.println("");
  }

  public ArrayList<SSAGraphNode> getSSAGraph(){
    return this.ssa_nodes;
  }
}

class SSAGraphNode{
  public int num;
  public String type;
  public int sequence;
  public String dest;
  public String variable_name;
  public String value;
  public String operator;
}

class SSAGraphEdge{
}
