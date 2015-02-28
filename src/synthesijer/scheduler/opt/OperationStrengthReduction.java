package synthesijer.scheduler.opt;

import java.util.ArrayList;

import synthesijer.scheduler.ConstantOperand;
import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;

public class OperationStrengthReduction implements SchedulerInfoOptimizer{

	public SchedulerInfo opt(SchedulerInfo info){
		SchedulerInfo result = new SchedulerInfo(info.getName());
		ArrayList<VariableOperand>[] vars = info.getVarTableList();
		for(ArrayList<VariableOperand> v: vars){
			result.addVarTable(v);
		}
		for(SchedulerBoard b: info.getBoardsList()){
			result.addBoard(conv(b));
		}
		return result;
	}

	
	public String getKey(){
		return "strength_reduction";
	}
	
	public SchedulerBoard conv(SchedulerBoard src){
		SchedulerBoard ret = new SchedulerBoard(src.getName(), src.getMethod());
		for(SchedulerSlot slot: src.getSlots()){
			SchedulerSlot newSlot = new SchedulerSlot(slot.getStepId());
			for(SchedulerItem item: slot.getItems()){
				newSlot.addItem(conv(src, item));
			}
			ret.addSlot(newSlot);
		}
		return ret;
	}
	
	private double log2(double x){
		return Math.log(x) / Math.log(2);
	}
	
	private boolean isPowerOfTwo(long x){
		return ((int)log2(x)) == log2(x);
	}
	
	public SchedulerItem conv(SchedulerBoard board, SchedulerItem item){
		if(item.getOp() != Op.MUL32){
			return item;
		}
		Operand[] src = item.getSrcOperand();
		if(src[0] instanceof VariableOperand && src[1] instanceof VariableOperand){
			return item;
		}
		if(src[0] instanceof ConstantOperand && src[1] instanceof ConstantOperand){
			return item;
		}
		ConstantOperand k = (src[0] instanceof ConstantOperand) ? (ConstantOperand)src[0] : (ConstantOperand)src[1];
		VariableOperand v = (src[0] instanceof VariableOperand) ? (VariableOperand)src[0] : (VariableOperand)src[1];
		long value = Long.parseLong(k.getValue());
		if(isPowerOfTwo(value) == false){
			return item;
		}
		int shift = (int)log2(value);
		item.overwriteOp(Op.SIMPLE_LSHIFT);
		item.overwriteSrc(0, v);
		item.overwriteSrc(1, new ConstantOperand(String.valueOf(shift), k.getType()));
		return item;
	}
}
