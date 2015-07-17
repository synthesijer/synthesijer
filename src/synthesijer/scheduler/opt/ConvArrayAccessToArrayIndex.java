package synthesijer.scheduler.opt;

import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;

public class ConvArrayAccessToArrayIndex implements SchedulerInfoOptimizer{
	
	public SchedulerInfo opt(SchedulerInfo info){
		SchedulerInfo result = info.getSameInfo();
		for(SchedulerBoard b: info.getBoardsList()){
			result.addBoard(conv(b));
		}
		return result;
	}
	
	public String getKey(){
		return "conv_array_access";
	}
	
	public SchedulerBoard conv(SchedulerBoard src){
		SchedulerBoard ret = src.genSameEnvBoard();
		for(SchedulerSlot slot: src.getSlots()){
			SchedulerSlot newSlot = new SchedulerSlot(slot.getStepId());
			for(SchedulerItem item: slot.getItems()){
				newSlot.addItem(conv(src, item));
			}
			ret.addSlot(newSlot);
		}
		return ret;
	}
	
	public SchedulerItem conv(SchedulerBoard board, SchedulerItem item){
		if(item.getOp() != Op.ARRAY_ACCESS){
			// nothing to do
			return item;
		}
		VariableOperand dest = item.getDestOperand();
		if(isUsedAsSrc(board, dest)){
			return item;
		}else{
			item.overwriteOp(Op.ARRAY_INDEX);
			return item;
		}
	}
	
	private boolean isUsedAsSrc(SchedulerBoard board, VariableOperand op){
		for(SchedulerSlot slot: board.getSlots()){
			for(SchedulerItem item: slot.getItems()){
				if(item.hasSrcOperand() == false) continue;
				for(Operand srcOp : item.getSrcOperand()){
					if(srcOp == op) return true;
				}
			}
		}
		return false;
	}
	
}
