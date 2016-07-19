package synthesijer.scheduler;

import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Hashtable;

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
		this.varList = b.varList;
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

	public Hashtable<SchedulerSlot, Integer> getEntryDegrees(){
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

	public void dump(PrintStream out){
		for(SchedulerSlot s: slots){
			s.dump(out);
		}
	}

	public void dumpDot(PrintStream out){
		for(SchedulerSlot slot: slots){
			slot.dumpDot(out);
		}
	}

	
}

