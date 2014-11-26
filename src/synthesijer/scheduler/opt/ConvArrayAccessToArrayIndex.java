package synthesijer.scheduler.opt;

import java.util.ArrayList;

import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;

public class ConvArrayAccessToArrayIndex {
	
	private final SchedulerInfo orig;
	private final SchedulerInfo result;
	
	public ConvArrayAccessToArrayIndex(SchedulerInfo info){
		this.orig = info;
		result = new SchedulerInfo(info.getName());
		ArrayList<VariableOperand>[] vars = info.getVarTableList();
		for(ArrayList<VariableOperand> v: vars){
			result.addVarTable(v);
		}
	}

	public SchedulerInfo opt(){
		for(SchedulerBoard b: orig.getBoardsList()){
			result.addBoard(conv(b));
		}
		return result;
	}
	
	public SchedulerBoard conv(SchedulerBoard src){
		SchedulerBoard ret = new SchedulerBoard(src.getName(), src.getMethod());
		for(SchedulerSlot slot: src.getSlots()){
			SchedulerSlot newSlot = new SchedulerSlot();
			for(SchedulerItem item: slot.getItems()){
				SchedulerItem newItem = conv(src, item);
				newSlot.addItem(conv(src, newItem));
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
