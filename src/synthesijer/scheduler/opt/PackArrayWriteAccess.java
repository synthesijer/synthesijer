package synthesijer.scheduler.opt;

import synthesijer.scheduler.Op;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;

public class PackArrayWriteAccess implements SchedulerInfoOptimizer{

	public SchedulerInfo opt(SchedulerInfo info){
		SchedulerInfo result = info.getSameInfo();
		for(SchedulerBoard b: info.getBoardsList()){
			result.addBoard(conv(b));
		}
		return result;
	}
	
	public String getKey(){
		return "pack_array_write";
	}
	
	private SchedulerSlot copySlots(SchedulerSlot slot){
		SchedulerSlot newSlot = new SchedulerSlot(slot.getStepId()); 
		for(SchedulerItem item: slot.getItems()){
			newSlot.addItem(item);
			item.setSlot(newSlot);
		}
		return newSlot;
	}
	
	public SchedulerBoard conv(SchedulerBoard src){
		SchedulerBoard ret = src.genSameEnvBoard();
		SchedulerSlot[] slots = src.getSlots();
		int i = 0;
		// search the sequence of Op.ARRAY_INDEX and Op.ASSIGN to pack them int a slot
		while(i < slots.length){
			SchedulerSlot slot = slots[i];
			i++;
			SchedulerSlot newSlot = copySlots(slot);
			ret.addSlot(newSlot);
			SchedulerItem[] items = slot.getItems();
			if(items.length > 1){ continue; /* skip */ }
			if(items[0].getOp() != Op.ARRAY_INDEX){ continue; /* skip */ }
			SchedulerSlot candidate = slots[i];
			SchedulerItem[] candidate_items = candidate.getItems();
			if(candidate_items.length > 1){ continue; /* skip */ }
			if(candidate_items[0].getOp() != Op.ASSIGN){ continue; /* skip */ }
			if(items[0].getDestOperand() != candidate_items[0].getDestOperand()){ continue; /* skip */ }
			newSlot.addItem(candidate_items[0]);
			candidate_items[0].setSlot(newSlot);
			// to force next state is after Op.ASSIGN item
			for(SchedulerItem item: newSlot.getItems()){
				item.setBranchIds(candidate_items[0].getBranchId());
			}
			i++;
		}
		return ret;
	}

}
