package synthesijer.scheduler;

import java.io.PrintStream;
import java.util.ArrayList;

/**
 * SchdulerBoard manages scheduling table of instances of SchdulerItem. 
 * 
 * @author miyo
 *
 */
public class SchedulerBoard {

	/**
	 * container of slots.
	 * the index in this array corresponds to computation step. 
	 */
	private ArrayList<SchedulerSlot> slots;
	
	public SchedulerBoard(){
		this.slots = new ArrayList<SchedulerSlot>();
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
		out.println("digraph{");
		for(SchedulerSlot slot: slots){
			slot.dumpDot(out);
		}
		out.println("}");
	}

	
}

class SchedulerSlot {
	

	/**
	 * container of items.
	 * the index in this array corresponds to computation step. 
	 */
	private ArrayList<SchedulerItem> items = new ArrayList<>();

	
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
				out.printf("%d [shape = box, label = \"%s\"];", item.getStepId(), item.info());
				out.println();
				out.printf("%d -> %d;", item.getStepId(), n);
				out.println();
			}
		}
	}
}
