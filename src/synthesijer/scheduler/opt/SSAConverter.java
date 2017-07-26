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

public class SSAConverter implements SchedulerInfoOptimizer{

	public SchedulerInfo opt(SchedulerInfo info){
		SchedulerInfo result = info.getSameInfo();
		for(SchedulerBoard b: info.getBoardsList()){
			result.addBoard(conv(b));
		}
		return result;
	}

	public String getKey(){
		return "ssa_converter";
	}

	public SchedulerBoard conv(SchedulerBoard src){
		ControlFlowGraph g = new ControlFlowGraph(src);
		System.out.println("board: " + src.getName());
		g.dumpBB(System.out);
		SchedulerBoard ret = src.genSameEnvBoard();
		SchedulerSlot[] slots = src.getSlots();

		for(ControlFlowGraph.BasicBlock bb : g.trace()){
			for(ControlFlowGraph.BasicBlockItem item: bb.items){
				ret.addSlot(item.slot);
			}
		}

		return ret;
	}
	
}
