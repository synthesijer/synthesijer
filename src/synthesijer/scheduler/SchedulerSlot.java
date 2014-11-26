package synthesijer.scheduler;

import java.io.PrintStream;
import java.util.ArrayList;

public class SchedulerSlot {
	

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
