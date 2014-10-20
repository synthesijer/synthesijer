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
	private Operand[] src;
	
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
	public SchedulerItem(Op op, Operand[] src, Variable dest){
		this.op = op;
		this.src = src;
		this.dest = dest;
	}
	
	public void setStepId(int id){
		this.stepId = id;
		if(op.isBranch == false){
			this.branchId = id + 1; 
		}
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
	
	private String srcInfo(){
		if(src == null) return "";
		String s = "";
		String sep = "";
		for(Operand o: src){
			s += sep + o.info();
			sep = ", ";
		}
		return s;
	}

	private String destInfo(){
		if(dest == null) return "";
		return dest.info();
	}

	public String info(){
		String s = String.format("%04d: op=%s src=%s, dest=%s [%d]", stepId, op, srcInfo(), destInfo(), branchId);
		return s;
	}

}

class MethodEntryItem extends SchedulerItem{
	
	public final String name;
	
	public MethodEntryItem(String name){
		super(Op.METHOD_ENTRY, null, null);
		this.name = name;
	}
	
	public String info(){
		String s = super.info();
		s += " (name=" + name + ")";
		return s;
	}
	
}
