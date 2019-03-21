package synthesijer.scheduler.opt;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;

public class ControlDataFlowGraph{

	private ControlDataFlowGraphNode top;

	public ControlDataFlowGraph(SchedulerBoard board){
	}

	private void trace(SchedulerBoard board){
	}

	public void add(ControlDataFlowGraphNode n){
		
	}
	
}

class ControlDataFlowGraphNode{

	private final ArrayList<ControlDataFlowGraphNode> pred = new ArrayList<>();
	
	private final ArrayList<ControlDataFlowGraphNode> succ = new ArrayList<>();
	
	private final SchedulerSlot slot;

	public ControlDataFlowGraphNode(SchedulerSlot slot){
		this.slot = slot;
	}
	
}
