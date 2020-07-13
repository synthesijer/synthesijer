package synthesijer.scheduler;

import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashMap;

import synthesijer.ast.Method;
import synthesijer.ast.Type;

/**
 * SchdulerBoard manages scheduling table of instances of SchdulerItem. 
 *
 * @author miyo
 *
 */
public class SchedulerBoard {

	private final String name;

//	private final Method method;

	/**
	 * container of slots.
	 * the index in this array corresponds to computation step. 
	 */
	private ArrayList<SchedulerSlot> slots;

	//private final ArrayList<VariableOperand> varList;
	private final ArrayList<Operand> varList;

	private final Type returnType;

	private final boolean privateFlag;

	private final boolean autoFlag;

	private final boolean callStackFlag;

	private final int callStackSize;

	private final boolean hasWaitDependsBoardFlag;

	private final String waitDependsBoardName;

	SchedulerBoard(
			String name,
			Type returnType,
			boolean privateFlag,
			boolean autoFlag,
			boolean callStackFlag,
			int callStackSize,
			boolean hasWaitWithMethod,
			String waitMethodName){
		this.name = name;
		this.slots = new ArrayList<>();
		varList = new ArrayList<>();
		this.returnType = returnType;
		this.privateFlag = privateFlag;
		this.autoFlag = autoFlag;
		this.callStackFlag = callStackFlag;
		this.callStackSize = callStackSize;
		this.hasWaitDependsBoardFlag = hasWaitWithMethod;
		this.waitDependsBoardName = waitMethodName;
	}

	SchedulerBoard(String name, Method m){
		this.name = name;
		this.slots = new ArrayList<>();
		varList = new ArrayList<>();
		this.returnType = m.getType();
		this.privateFlag = m.isPrivate();
		this.autoFlag = m.isAuto();
		this.callStackFlag = m.hasCallStack();
		this.callStackSize = m.getCallStackSize();
		if(m.getWaitWithMethod() != null){
			this.hasWaitDependsBoardFlag = true;
			this.waitDependsBoardName = m.getWaitWithMethod().getName();
		}else{
			this.hasWaitDependsBoardFlag = false;
			this.waitDependsBoardName = null;
		}
	}

	private SchedulerBoard(SchedulerBoard b){
		this.name = b.name;
		this.slots = new ArrayList<SchedulerSlot>();
		this.varList = new ArrayList<>();
		for(var v: b.varList){
			this.varList.add(v);
		}
		this.returnType = b.returnType;
		this.privateFlag = b.privateFlag;
		this.autoFlag = b.autoFlag;
		this.callStackFlag = b.callStackFlag;
		this.callStackSize = b.callStackSize;
		this.hasWaitDependsBoardFlag = b.hasWaitDependsBoardFlag;
		this.waitDependsBoardName = b.waitDependsBoardName;
	}

	public SchedulerBoard genSameEnvBoard() {
		return new SchedulerBoard(this);
	}

	@Deprecated
	public SchedulerBoard copyBoard(){
		SchedulerBoard ret = this.genSameEnvBoard();
		for(var s: slots){
			SchedulerSlot ns = new SchedulerSlot(s.getStepId());
			for(SchedulerItem item: s.getItems()){
				ns.addItem(item.copy(ret, ns));
			}
			ret.addSlot(ns);
		}
		return ret;
	}

	public String getName(){
		return name;
	}

	public Type getReturnType(){
		return returnType;
	}

	public boolean isPrivate(){
		return privateFlag;
	}

	public boolean isAuto(){
		return autoFlag;
	}

	public boolean hasCallStack(){
		return callStackFlag;
	}

	public int getCallStackSize(){
		return callStackSize;
	}

	public boolean hasWaitDependsBoard(){
		return hasWaitDependsBoardFlag;
	}

	public String getWaitDependsBoardName(){
		return waitDependsBoardName;
	}

	public SchedulerSlot[] getSlots(){
		return slots.toArray(new SchedulerSlot[]{});
	}

	//	public ArrayList<VariableOperand> getVarList(){
	public ArrayList<Operand> getVarList(){
		return varList;
	}

	public ArrayList<VariableOperand> getDestinationVariables(){
		ArrayList<VariableOperand> list = new ArrayList<>();
		for(var s: slots){
			for(var d: s.getDestOperands()){
				if(d != null && d instanceof VariableOperand){
					list.add((VariableOperand)d);
				}
			}
		}
		return list;
	}

	/**
	 * adds an item into the container with a new slot, and set stepId into the item.
	 * @param item
	 * @return the added item
	 */
	public SchedulerItem addItemInNewSlot(SchedulerItem item){
		SchedulerSlot slot = new SchedulerSlot(slots.size());
		if(item.isBranchOp() == false){
			item.setBranchId(slot.getStepId() + 1);
		}
		//item.setStepId(slots.size());
		slot.addItem(item);
		item.setSlot(slot);
		slots.add(slot);
		return item;
	}

	public void addSlot(SchedulerSlot slot){
		slots.add(slot);
	}

	public SchedulerSlot getSlot(int id){
		for(SchedulerSlot s: slots){
			if(s.getStepId() == id) return s;
		}
		return null;
	}

	public HashMap<SchedulerSlot, Integer> getEntryDegrees(){
		HashMap<SchedulerSlot, Integer> degrees = new HashMap<>();
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

	/**
	 * convert Slot-ID for all PHI-op in this board
	 * @param convTable slot-id conversion table (from the original id to the new id)
	 * @TOOD : too ad-hoc
	 */ 
	public void convPhiSlotIdAll(HashMap<Integer, Integer> convTable){
		for(var slot : slots){
			for(var item: slot.getItems()){
				if(item instanceof PhiSchedulerItem){
					PhiSchedulerItem phi = (PhiSchedulerItem)item;
					for(int i = 0; i < phi.pat.length; i++){
						if(convTable.containsKey(phi.pat[i].getStepId())){
							int v = convTable.get(phi.pat[i].getStepId());
							phi.pat[i] = getSlot(v);
						}
					}
				}
			}
		}
	}
	
	public void dump(PrintStream out){
		for(SchedulerSlot s: slots){
			s.dump(out);
		}
	}

	public void dumpDot(PrintStream out){
		for(int i = 1; i < slots.size(); i++){
			slots.get(i).dumpDot(out);
		}
		slots.get(0).dumpDot(out);
	}


}

