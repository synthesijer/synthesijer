package synthesijer.scheduler.opt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;

import synthesijer.SynthesijerUtils;
import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;

public class DominatorTree{

	private String base;

	public DominatorTree(ControlFlowGraph cfg, String key){
	}

}

class DominatorTreeNode{

	final ArrayList<DominatorTreeNode> pred = new ArrayList<>();

	final ArrayList<DominatorTreeNode> succ = new ArrayList<>();

	final ControlFlowGraphBB node;

	public DominatorTreeNode(ControlFlowGraphBB node){
		this.node = node;
	}

}
