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
	private int stepID;

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
	private int[] branchIDs;
	
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
		this.stepID = id;
		if(op.isBranch == false){
			branchIDs = new int[]{id + 1}; 
		}
	}

	public int getStepId(){
		return this.stepID;
	}

	public void setBranchId(int id){
		branchIDs = new int[]{id}; 
	}
	
	public void setBranchIds(int[] ids){
		branchIDs = ids; 
	}

	public int[] getBranchId(){
		return branchIDs;
	}
	
	public boolean isBranchOp(){
		return op.isBranch;
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
	
	private String branchList(){
		String s = "";
		String sep = "";
		for(int id: branchIDs){
			s += sep + id;
			sep = ", ";
		}
		return s;
	}

	public String info(){
		String s = String.format("%04d: op=%s src=%s, dest=%s [%s]", stepID, op, srcInfo(), destInfo(), branchList());
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

class MethodInvokeItem extends SchedulerItem{
	
	public final Variable obj;
	public final String name;
	
	public MethodInvokeItem(String name, Operand[] src, Variable dest){
		super(Op.CALL, src, dest);
		this.name = name;
		this.obj = null;
	}

	public MethodInvokeItem(Variable obj, String name, Operand[] src, Variable dest){
		super(Op.EXT_CALL, src, dest);
		this.name = name;
		this.obj = obj;
	}

	public String info(){
		String s = super.info();
		if(obj == null){
			s += " (name=" + name + ")";
		}else{
			s += " (obj = " + obj.getName() + ", name=" + name + ")";
		}
		return s;
	}
	
}

class FieldAccessItem extends SchedulerItem{
	
	public final Variable obj;
	public final String name;
	
	public FieldAccessItem(Variable obj, String name, Operand[] src, Variable dest){
		super(Op.FIELD_ACCESS, src, dest);
		this.name = name;
		this.obj = obj;
	}

	public String info(){
		String s = super.info();
		if(obj == null){
			s += " (name=" + name + ")";
		}else{
			s += " (obj = " + obj.getName() + ", name=" + name + ")";
		}
		return s;
	}
	
}
