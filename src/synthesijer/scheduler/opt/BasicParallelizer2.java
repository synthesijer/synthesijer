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

public class BasicParallelizer2 implements SchedulerInfoOptimizer{
	
	public static final boolean DEBUG = false;

	public SchedulerInfo opt(SchedulerInfo info){
//		SchedulerInfo result = new SchedulerInfo(info.getName());
//		result.addModuleVarList(info.getModuleVarList());
		SchedulerInfo result = info.getSameInfo();
		for(SchedulerBoard b: info.getBoardsList()){
			result.addBoard(conv(b));
		}
		return result;
	}
	
	public String getKey(){
		return "basic_parallelize2";
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
			degrees.put(s, 0);
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
	
	private Hashtable<SchedulerSlot, ArrayList<SchedulerSlot>> analyze(ArrayList<SchedulerSlot> bb){
		Hashtable<Operand, ArrayList<SchedulerSlot>> writing = new Hashtable<>();
		Hashtable<Operand, ArrayList<SchedulerSlot>> reading = new Hashtable<>();
		Hashtable<SchedulerSlot, ArrayList<SchedulerSlot>> dependents = new Hashtable<>();
		for(SchedulerSlot s: bb){
			ArrayList<SchedulerSlot> dependent = new ArrayList<>();
			dependents.put(s, dependent);
			for(Operand src: s.getSrcOperands()){ // should read after previous write
				if(writing.containsKey(src)){
					ArrayList<SchedulerSlot> l = writing.get(src);
					for(SchedulerSlot ll: l){
						dependent.add(ll);
					}
				}
			}
			for(Operand dest: s.getDestOperands()){ // should write after previous write
				if(dest != null && writing.containsKey(dest)){
					ArrayList<SchedulerSlot> l = writing.get(dest);
					for(SchedulerSlot ll: l){
						dependent.add(ll);
					}
				}
			}
			for(Operand dest: s.getDestOperands()){ // should write after previous read
				if(dest != null && reading.containsKey(dest)){
					ArrayList<SchedulerSlot> l = reading.get(dest);
					for(SchedulerSlot ll: l){
						dependent.add(ll);
					}
				}
			}
			
			for(Operand dest: s.getDestOperands()){
				if(dest == null) continue;
				ArrayList<SchedulerSlot> l = writing.get(dest);
				if(l == null) l = new ArrayList<>();
				if(dest instanceof VariableOperand){
					l.add(s);
					writing.put(dest, l);
				}
			}
			for(Operand src: s.getSrcOperands()){
				ArrayList<SchedulerSlot> l = reading.get(src);
				if(l == null) l = new ArrayList<>();
				if(src instanceof VariableOperand){
					l.add(s);
					reading.put(src, l);
				}
			}
		}
		return dependents;
	}
	
	private boolean isReady(SchedulerSlot slot, ArrayList<SchedulerSlot> dependent, ArrayList<SchedulerSlot> restList){
		if(dependent == null) return true;
		for(SchedulerSlot s: dependent){
			if(restList.contains(s) == true){
				return false;
			}
		}
		return true;
	}
	
	private SchedulerSlot parallelize(SchedulerBoard board, ArrayList<SchedulerSlot> bb, Hashtable<Integer, Integer> id_map){
		SchedulerSlot target = null;
		SchedulerSlot prev = null;
		Hashtable<SchedulerSlot, ArrayList<SchedulerSlot>> dependents = analyze(bb);
		ArrayList<SchedulerSlot> restList = bb;
		ArrayList<SchedulerSlot> genList = new ArrayList<>();
		while(restList.size() > 0){
			ArrayList<SchedulerSlot> tmpList = new ArrayList<>();
			for(SchedulerSlot s: restList){
				if(target == null){
					target = copySlots(s);
					board.addSlot(target);
					genList.add(target);
					if(s.getStepId() != target.getStepId()){
						id_map.put(s.getStepId(), target.getStepId());
					}
				}else{
					if(isReady(s, dependents.get(s), restList)){
						if(s.getStepId() != target.getStepId()){
							id_map.put(s.getStepId(), target.getStepId());
						}
						for(SchedulerItem item: s.getItems()){
							target.addItem(item);
							item.setSlot(target);
						}
					}else{
						tmpList.add(s);
					}
				}
			}
			restList = tmpList;
			if(prev != null && target != null) for(SchedulerItem item: prev.getItems()){ item.setBranchId(target.getStepId());}
			prev = target;
			target = null; // next
		}
//		for(SchedulerSlot s: genList){
//			for(SchedulerItem i: s.getItems()){
//				i.remapBranchIds(id_map);
//			}
//		}
		return prev;
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
//		case ARRAY_ACCESS:
//		case ARRAY_INDEX:
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
	
	private boolean isExcept(SchedulerItem[] items){
		boolean flag = false;
		for(SchedulerItem i: items){
			flag |= isExcept(i);
		}
		return flag;
	}
	
	private boolean isConflicted(SchedulerSlot s, SchedulerItem item){
		boolean f = false;
		for(SchedulerItem prev: s.getItems()){
			f |= item.isConflicted(prev);
		}
		return f;
	}
	
	private boolean isConflicted(ArrayList<SchedulerSlot> bb, SchedulerItem item){
		if(bb == null) return false;
		boolean f = false;
		for(SchedulerSlot s: bb){
			f |= isConflicted(s, item);
		}
		return f;
	}

	private boolean isConflicted(ArrayList<SchedulerSlot> bb, SchedulerSlot s){
		if(bb == null) return false;
		boolean flag = false;
		for(SchedulerItem item : s.getItems()){
			flag |= isConflicted(bb, item);
		}
		return flag;
	}

	private void dumpDegree(String name, Hashtable<SchedulerSlot, Integer> degrees){
		System.out.println("**** " + name);
		Enumeration<SchedulerSlot> e = degrees.keys();
		while(e.hasMoreElements()){
			SchedulerSlot s = e.nextElement();
			System.out.println(s.getStepId() + " -> " + degrees.get(s));
		}
	}
	
	public SchedulerBoard conv(SchedulerBoard src){
		SchedulerBoard ret = src.genSameEnvBoard();
		SchedulerSlot[] slots = src.getSlots();
		Hashtable<SchedulerSlot, Integer> degrees = getEntryDegrees(slots);
		if(DEBUG){
			dumpDegree(src.getName(), degrees);
		}
		ArrayList<SchedulerSlot> bb = null;
		Hashtable<Integer, Integer> id_map = new Hashtable<>();
		SchedulerSlot prev = null;
		for(int i = 0; i < slots.length; i++){
			SchedulerSlot slot = slots[i];
			Integer d = degrees.get(slot);
			if(d == null){
				System.out.println("d==null: " + slot.getItems()[0].toSexp() + "@" + slot);
			}
			if(slot.hasBranchOp()
					|| slot.getNextStep().length > 1
					//|| slot.getLatency() > 0
					|| d > 1
					|| slot.getItems().length > 1
					|| isExcept(slot.getItems())
					|| isConflicted(bb, slot)){
				if(bb != null && bb.size() > 0){
					prev = parallelize(ret, bb, id_map);
				}
				// the slot should be registered as a new slot
				SchedulerSlot newSlot = copySlots(slot);
				ret.addSlot(newSlot);
				if(bb != null && prev != null) for(SchedulerItem item: prev.getItems()){ item.setBranchId(newSlot.getStepId());}
				for(SchedulerItem item: newSlot.getItems()){
					item.remapBranchIds(id_map);
				}
				bb = null; // reset
				prev = newSlot;
			}else{
				if(bb == null){
					bb = new ArrayList<>();
				}
				bb.add(slot);
			}
		}
		if(bb != null && bb.size() > 0){
			parallelize(ret, bb, id_map);
		}
		return ret;
	}

}
