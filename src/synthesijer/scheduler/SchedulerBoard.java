package synthesijer.scheduler;

import java.util.ArrayList;

/**
 * SchdulerBoard manages scheduling table of instances of SchdulerItem. 
 * 
 * @author miyo
 *
 */
public class SchedulerBoard {

	/**
	 * container of items.
	 * the index in this array corresponds to computation step. 
	 */
	private ArrayList<SchedulerItem> items;
	
	public SchedulerBoard(){
		this.items = new ArrayList<SchedulerItem>();
	}
	
	/**
	 * adds an item into the container, and set stepId into the item.
	 * @param item
	 */
	public void addItem(SchedulerItem item){
		items.add(item);
		item.setStepId(items.size()-1);
	}
	
	
}
