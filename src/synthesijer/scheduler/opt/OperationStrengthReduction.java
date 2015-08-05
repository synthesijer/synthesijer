package synthesijer.scheduler.opt;

import synthesijer.IdentifierGenerator;
import synthesijer.scheduler.ConstantOperand;
import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;

public class OperationStrengthReduction implements SchedulerInfoOptimizer{
	
	private final IdentifierGenerator idGen = new IdentifierGenerator();

	public SchedulerInfo opt(SchedulerInfo info){
		SchedulerInfo result = info.getSameInfo();
		for(SchedulerBoard b: info.getBoardsList()){
			result.addBoard(conv(b));
		}
		return result;
	}

	
	public String getKey(){
		return "strength_reduction";
	}
	
	public SchedulerBoard conv(SchedulerBoard src){
		SchedulerBoard ret = src.genSameEnvBoard();
		for(SchedulerSlot slot: src.getSlots()){
			SchedulerSlot newSlot = new SchedulerSlot(slot.getStepId());
			for(SchedulerItem item: slot.getItems()){
				newSlot.addItem(conv(item));
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
	
	private SchedulerItem convMulToShift(SchedulerItem item){
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
		if(value < 0 || isPowerOfTwo(value) == false){
			return item;
		}
		int shift = (int)log2(value);
		item.overwriteOp(Op.SIMPLE_LSHIFT32);
		item.overwriteSrc(0, v);
		item.overwriteSrc(1, new ConstantOperand(String.format("reduction_constant_%05d", idGen.id()), String.valueOf(shift), k.getType()));
		return item;
	}
	
	private SchedulerItem convDivToShift(SchedulerItem item){
		Operand[] src = item.getSrcOperand();
		if((src[0] instanceof VariableOperand && src[1] instanceof ConstantOperand) == false){
			return item;
		}
		ConstantOperand k = (ConstantOperand)src[1];
		long value = Long.parseLong(k.getValue());
		if(value < 0 || isPowerOfTwo(value) == false){
			return item;
		}
		int shift = (int)log2(value);
		item.overwriteOp(Op.SIMPLE_ARITH_RSHIFT32);
		item.overwriteSrc(1, new ConstantOperand(String.format("reduction_constant_%05d", idGen.id()), String.valueOf(shift), k.getType()));
		return item;
	}
	
	public SchedulerItem conv(SchedulerItem item){
		if(item.getOp() == Op.MUL32 || item.getOp() == Op.MUL64){
			return convMulToShift(item);
		}else if(item.getOp() == Op.DIV32 || item.getOp() == Op.DIV64){
			return convDivToShift(item);
		}else{
			return item;
		}
	}
}
