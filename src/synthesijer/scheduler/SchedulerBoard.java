package synthesijer.scheduler;

import java.io.PrintStream;
import java.util.ArrayList;

import synthesijer.ast.Method;

/**
 * SchdulerBoard manages scheduling table of instances of SchdulerItem. 
 * 
 * @author miyo
 *
 */
public class SchedulerBoard {

	private final String name;
	
	private final Method method;
	
	/**
	 * container of slots.
	 * the index in this array corresponds to computation step. 
	 */
	private ArrayList<SchedulerSlot> slots;
	
	private final ArrayList<VariableOperand> varList;
	
	SchedulerBoard(String name, Method m){
		this.name = name;
		this.method = m;
		this.slots = new ArrayList<>();
		varList = new ArrayList<>();
	}

	private SchedulerBoard(SchedulerBoard b){
		this.name = b.name;
		this.method = b.method;
		this.slots = new ArrayList<SchedulerSlot>();
		this.varList = b.varList;
	}

	public SchedulerBoard genSameEnvBoard() {
		return new SchedulerBoard(this);
	}
	
	public String getName(){
		return name;
	}

	public Method getMethod(){
		return method;
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

