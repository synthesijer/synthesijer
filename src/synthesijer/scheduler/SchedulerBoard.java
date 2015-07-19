package synthesijer.scheduler;

import java.io.PrintStream;

import java.util.ArrayList;

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
	
	private final ArrayList<VariableOperand> varList;
	
	private final Type returnType;
	
	private final boolean privateFlag;
	
	private final boolean autoFlag;
	
	private final boolean callStackFlag;
	
	private final int callStackSize;
	
	private final boolean hasWaitDependsBoardFlag;
	
	private final String waitDependsBoardName;
	
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

	public ArrayList<VariableOperand> getVarList(){
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

