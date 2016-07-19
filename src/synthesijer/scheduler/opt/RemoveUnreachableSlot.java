package synthesijer.scheduler.opt;

import java.util.Hashtable;

import synthesijer.scheduler.Op;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;

public class RemoveUnreachableSlot implements SchedulerInfoOptimizer{
	
	public SchedulerInfo opt(SchedulerInfo info){
		SchedulerInfo result = info.getSameInfo();
		for(SchedulerBoard b: info.getBoardsList()){
			result.addBoard(conv(b));
		}
		return result;
	}
	
	public String getKey(){
		return "remove_unreachable_slot";
	}
	
	Hashtable<Integer, Boolean> usedFlag = new Hashtable<>(); 
	
	public SchedulerBoard conv(SchedulerBoard src){
		for(SchedulerSlot slot: src.getSlots()){
			for(int id: slot.getNextStep()){
				usedFlag.put(id, true);
			}
		}
		SchedulerBoard ret = src.genSameEnvBoard();
		for(SchedulerSlot slot: src.getSlots()){
			if(usedFlag.containsKey(slot.getStepId())){
				ret.addSlot(slot);
			}
		}
		return ret;
	}
	
}
