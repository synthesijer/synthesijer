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
	
	public SchedulerBoard(String name, Method m){
		this.name = name;
		this.method = m;
		this.slots = new ArrayList<SchedulerSlot>();
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
	
	/**
	 * adds an item into the container with a new slot, and set stepId into the item.
	 * @param item
	 * @return the added item
	 */
	public SchedulerItem addItemInNewSlot(SchedulerItem item){
		item.setStepId(slots.size());
		SchedulerSlot slot = new SchedulerSlot();
		slot.addItem(item);
		slots.add(slot);
		return item;
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

class SchedulerSlot {
	

	/**
	 * container of items.
	 * the index in this array corresponds to computation step. 
	 */
	private ArrayList<SchedulerItem> items = new ArrayList<>();
	
	public SchedulerItem[] getItems(){
		return items.toArray(new SchedulerItem[]{});
	}

	
	/**
	 * adds an item into the container with new slot, and set stepId into the item.
	 * @param item
	 * @return the added item
	 */
	public SchedulerItem addItem(SchedulerItem item){
		items.add(item);
		return item;
	}
	
	
	public void dump(PrintStream out){
		for(SchedulerItem item: items){
			out.println(item.info());
		}
	}

	public void dumpDot(PrintStream out){
		for(SchedulerItem item: items){
			for(int n: item.getBranchId()){
				out.printf("%s_%d [shape = box, label = \"%s\"];", item.getBoardName(), item.getStepId(), item.info());
				out.println();
				out.printf("%s_%d -> %s_%d;", item.getBoardName(), item.getStepId(), item.getBoardName(), n);
				out.println();
			}
		}
	}
}
