package synthesijer.scheduler.opt;

import synthesijer.scheduler.Op;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;

public class ReduceRedundantJump implements SchedulerInfoOptimizer{
	
	public SchedulerInfo opt(SchedulerInfo info){
		SchedulerInfo result = info.getSameInfo();
		for(SchedulerBoard b: info.getBoardsList()){
			result.addBoard(conv(b));
		}
		return result;
	}
	
	public String getKey(){
		return "reduce_redundant_jump";
	}
	
	public SchedulerBoard conv(SchedulerBoard src){
		SchedulerBoard ret = src.genSameEnvBoard();
		for(SchedulerSlot slot: src.getSlots()){
			if(hasRedandantState(src, slot) == false){
				ret.addSlot(slot); // as is
			}else{
				int[] id = new int[slot.getNextStep().length];
				for(int i = 0; i < id.length; i++){
					id[i] = getTargetState(src, slot.getNextStep()[i]);
				}
				SchedulerSlot newSlot = new SchedulerSlot(slot.getStepId());
				for(SchedulerItem item: slot.getItems()){
					newSlot.addItem(item);
					item.setBranchIds(id);
				}
				ret.addSlot(newSlot);
			}
		}
		return ret;
	}
	
	public int getTargetState(SchedulerBoard b, int id){
		SchedulerSlot slot = b.getSlot(id);
		if(slot.getItems().length == 1 && slot.getItems()[0].getOp() == Op.JP){
			return getTargetState(b, slot.getNextStep()[0]);
		}
		return id;
	}

	public boolean hasRedandantState(SchedulerBoard b, SchedulerSlot slot){
		for(int i: slot.getNextStep()){
			SchedulerSlot s = b.getSlot(i);
			if(s.getItems().length == 1 && s.getItems()[0].getOp() == Op.JP){
				return true;
			}
		}
		return false;
	}

	
}
