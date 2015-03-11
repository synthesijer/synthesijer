package synthesijer.scheduler.opt;

import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;

public class SimpleChaining implements SchedulerInfoOptimizer{

	@Override
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
	
	@Override
	public String getKey(){
		return "simple_chaining";
	}
	
	private SchedulerSlot copySlots(SchedulerSlot slot){
		SchedulerSlot newSlot = new SchedulerSlot(slot.getStepId()); 
		for(SchedulerItem item: slot.getItems()){
			newSlot.addItem(item);
			item.setSlot(newSlot);
		}
		return newSlot;
	}
	
	private Hashtable<SchedulerSlot, Integer> getEntryDegrees(SchedulerSlot[] slots){
		Hashtable<SchedulerSlot, Integer> degrees = new Hashtable<>();
		Hashtable<Integer, SchedulerSlot> map = new Hashtable<>();
		for(SchedulerSlot s: slots){
			map.put(s.getStepId(), s);
		}
		for(SchedulerSlot s: slots){
			int[] ids = s.getNextStep();
			for(int id: ids){
				SchedulerSlot target = map.get(id);
				//SchedulerSlot target = map.get(s.getStepId());
				Integer v = degrees.get(target);
				if(v == null){
					degrees.put(target, 1);
				}else{
					degrees.put(target, v+1);
				}
			}
		}
		return degrees;
	}
	
	private SchedulerSlot chaining(ArrayList<SchedulerSlot> bb){

		Hashtable<Operand, SchedulerItem> predItem = new Hashtable<>();
		SchedulerSlot newSlot = null;
		
		int last = 0;
		for(SchedulerSlot s: bb){
			last = s.getNextStep()[0];
			for(SchedulerItem item : s.getItems()){
				int num = item.getSrcOperand().length;
				for(int i = 0; i < num; i++){
					Operand o = item.getSrcOperand()[i];
					if((o instanceof VariableOperand) && predItem.containsKey(o)){
						((VariableOperand)o).setChaining(item, predItem.get(o));
					}
				}
				predItem.put(item.getDestOperand(), item);				
			}
			if(newSlot == null){
				newSlot = copySlots(s);
			}else{
				for(SchedulerItem item: s.getItems()){
					newSlot.addItem(item);
					item.setSlot(newSlot);
				}
			}
		}
		for(SchedulerItem item: newSlot.getItems()){
			item.setBranchId(last);
		}
		return newSlot;
	}
	
	private boolean isExcept(SchedulerItem item){
		Op op = item.getOp();
		switch(op){
		case METHOD_ENTRY:
		case METHOD_EXIT:
		case MUL32:
		case MUL64:
		case DIV32:
		case DIV64:
		case MOD32:
		case MOD64:
		case LSHIFT32:
		case LOGIC_RSHIFT32:
		case ARITH_RSHIFT32:
		case LSHIFT64:
		case LOGIC_RSHIFT64:
		case ARITH_RSHIFT64:
		case JP:
		case JT:
		case RETURN:
		case SELECT:
		case ARRAY_ACCESS:
		case ARRAY_INDEX:
		case CALL:
		case EXT_CALL:
		case FIELD_ACCESS:
		case BREAK:
		case CONTINUE:
		case FADD32:
		case FSUB32:
		case FMUL32:
		case FDIV32:
		case FADD64:
		case FSUB64:
		case FMUL64:
		case FDIV64:
		case CONV_F2I:
		case CONV_I2F:
		case CONV_D2L:
		case CONV_L2D:
		case CONV_F2D:
		case CONV_D2F:
		case FLT32:
		case FLEQ32:
		case FGT32:
		case FGEQ32:
		case FCOMPEQ32:
		case FNEQ32:
		case FLT64:
		case FLEQ64:
		case FGT64:
		case FGEQ64:
		case FCOMPEQ64:
		case FNEQ64:
		case UNDEFINED:
			return true;
		default:
			return false;
		}
	}
	
	public SchedulerBoard conv(SchedulerBoard src){
		SchedulerBoard ret = src.genSameEnvBoard();
		SchedulerSlot[] slots = src.getSlots();
		Hashtable<SchedulerSlot, Integer> degrees = getEntryDegrees(slots);
		ArrayList<SchedulerSlot> bb = null;
		for(int i = 0; i < slots.length; i++){
			SchedulerSlot slot = slots[i];
			Integer d = degrees.get(slot);
			if(slot.hasBranchOp() || slot.getNextStep().length > 1 || slot.getLatency() > 0 || d > 1 || isExcept(slot.getItems()[0])){
				if(bb != null && bb.size() > 0){
					ret.addSlot(chaining(bb));
				}
				ret.addSlot(slot);
				bb = null; // reset
			}else{
				if(bb == null){
					bb = new ArrayList<>();
				}
				bb.add(slot);
			}
		}
		if(bb != null && bb.size() > 0){
			ret.addSlot(chaining(bb));
		}
		return ret;
	}

}
