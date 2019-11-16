package synthesijer.scheduler.opt;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.PhiSchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;

public class InnerBasicBlockParallelizer implements SchedulerInfoOptimizer{

	public static final boolean DEBUG = false;

	public String getKey(){
		return "inner_bb_parallelize";
	}

	public SchedulerInfo opt(SchedulerInfo info){
		SchedulerInfo result = info.getSameInfo();
		for(SchedulerBoard b: info.getBoardsList()){
			SchedulerBoard newB = b.copyBoard();
			result.addBoard(conv(newB));
		}
		return result;
	}

	public SchedulerBoard conv(SchedulerBoard src){
		var cfg = new ControlFlowGraph(src, getKey());
		for(var b : cfg.getBasicBlocks()){
			System.out.println("--basic block--");
			var items = b.getItems();
			for(var i: items){
				System.out.println(i.info());
			}
		}
			
		return src;
	}

}
