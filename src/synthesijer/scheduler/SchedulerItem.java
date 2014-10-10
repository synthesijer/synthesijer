package synthesijer.scheduler;

/**
 * SchdulerItem is a unit of computation to be scheduled.
 * 
 * @author miyo
 *
 */
public class SchedulerItem {
	
	/**
	 * the index value in schedulerBoard. 
	 */
	private int stepId;

	/**
	 * operator
	 */
	private Op op;
	
	/**
	 * source variables
	 */
	private Variable[] src;
	
	/**
	 * a destination variable
	 */
	private Variable dest;
	
	/**
	 * the index values to jump, which available when this is a kind of jump operations. 
	 */
	private int branchId;
	
	/**
	 * 
	 * @param op operator
	 * @param src source variables
	 * @param dest destination variables
	 */
	public SchedulerItem(Op op, Variable[] src, Variable dest){
		this.op = op;
		this.src = src;
		this.dest = dest;
	}
	
	public void setStepId(int id){
		this.stepId = id;
	}

	public int getStepId(){
		return this.stepId;
	}

	public void setBranchId(int id){
		this.branchId = id;
	}

	public int getBranchId(){
		return this.branchId;
	}

}

class MethodEntryItem extends SchedulerItem{
	
	public final String name;
	
	public MethodEntryItem(String name){
		super(Op.METHOD_ENTRY, null, null);
		this.name = name;
	}
	
}
