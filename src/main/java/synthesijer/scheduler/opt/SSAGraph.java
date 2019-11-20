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

  public SSAGraph(ControlFlowGraph cg){

    genSSANode();
  }

  public genSSANode(){
    ArrayList<SSAGraphNode> nodes = new ArrayList<>();
  }
}

class SSAGraphNode{
  public int num;
  public String type;
  public int sequence;
}

class SSAGraphEdge{
}
