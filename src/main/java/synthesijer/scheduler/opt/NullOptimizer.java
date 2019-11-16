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

public class NullOptimizer implements SchedulerInfoOptimizer{

	public static final boolean DEBUG = false;

	public String getKey(){
		return "null_optimizer";
	}

	public SchedulerInfo opt(SchedulerInfo info){
		SchedulerInfo result = info.getSameInfo();
		for(SchedulerBoard b: info.getBoardsList()){
			SchedulerBoard newB = b.copyBoard();
			result.addBoard(newB);
		}
		return result;
	}

}
