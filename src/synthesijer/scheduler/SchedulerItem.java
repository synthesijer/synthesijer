package synthesijer.scheduler;

import synthesijer.ast.Type;
import synthesijer.ast.type.PrimitiveTypeKind;


/**
 * SchdulerItem is a unit of computation to be scheduled.
 * 
 * @author miyo
 *
 */
public class SchedulerItem {
	
	/**
	 * the board where this item belongs. 
	 */
	private final SchedulerBoard board;
	
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
	private VariableOperand dest;
	
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
	public SchedulerItem(SchedulerBoard board, Op op, Operand[] src, VariableOperand dest){
		this.board = board;
		this.op = op;
		this.src = src;
		this.dest = dest;
	}
	
	public Op getOp(){
		return op;
	}
	
	public void overwriteOp(Op op){
		this.op = op;
	}
	
	public String getBoardName(){
		return board.getName();
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
	
	public Operand[] getSrcOperand(){
		return src;
	}

	public boolean hasSrcOperand(){
		return src != null && src.length > 0;
	}

	public VariableOperand getDestOperand(){
		return dest;
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
			//s += String.format("%s%s_%04d", sep, getBoardName(), id);
			s += String.format("%s%04d", sep, id);
			sep = ", ";
		}
		return s;
	}

	public String info(){
		String s = String.format("%s_%04d: op=%s, src=%s, dest=%s, next=%s", getBoardName(), stepID, op, srcInfo(), destInfo(), branchList());
		return s;
	}

}

class MethodEntryItem extends SchedulerItem{
	
	public final String name;
	
	public MethodEntryItem(SchedulerBoard board, String name){
		super(board, Op.METHOD_ENTRY, null, null);
		this.name = name;
	}
	
	public String info(){
		String s = super.info();
		s += " (name=" + name + ")";
		return s;
	}
	
}

class MethodInvokeItem extends SchedulerItem{
	
	public final VariableOperand obj;
	public final String name;
	public final String[] args;
	
	private boolean noWaitFlag = false;
	
	public MethodInvokeItem(SchedulerBoard board, String name, Operand[] src, VariableOperand dest, String[] args){
		super(board, Op.CALL, src, dest);
		this.name = name;
		this.obj = null;
		this.args = args;
	}

	public MethodInvokeItem(SchedulerBoard board, VariableOperand obj, String name, Operand[] src, VariableOperand dest, String[] args){
		super(board, Op.EXT_CALL, src, dest);
		this.name = name;
		this.obj = obj;
		this.args = args;
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
	
	public void setNoWait(boolean flag){
		noWaitFlag = flag;
	}
	
	public boolean isNoWait(){
		return noWaitFlag;
	}
	
}

class FieldAccessItem extends SchedulerItem{
	
	public final VariableOperand obj;
	public final String name;
	
	public FieldAccessItem(SchedulerBoard board, VariableOperand obj, String name, Operand[] src, VariableOperand dest){
		super(board, Op.FIELD_ACCESS, src, dest);
		this.name = name;
		this.obj = obj;
	}

	public String info(){
		String s = super.info();
		if(obj == null){
			s += " (name=" + name + ")";
		}else{
			s += " (obj=" + obj.getName() + ", name=" + name + ")";
		}
		return s;
	}
	
}

class TypeCastItem extends SchedulerItem{
	
	public final Type orig;
	public final Type target;
	
	private TypeCastItem(SchedulerBoard board, Op op, Operand src, VariableOperand dest, Type orig, Type target){
		super(board, op, new Operand[]{src}, dest);
		this.orig = orig;
		this.target = target;
	}

	public String info(){
		String s = super.info();
		s += " (" + orig + "->" + target + ")";
		return s;
	}
	
	private static boolean isFloat(Type t){
		return t == PrimitiveTypeKind.FLOAT;
	}
	
	private static boolean isDouble(Type t){
		return t == PrimitiveTypeKind.DOUBLE;
	}
	
	private static boolean isFloating(Type t){
		return isFloat(t) || isDouble(t);
	}

	public static TypeCastItem newCastItem(SchedulerBoard board, Operand src, VariableOperand dest, Type orig, Type target){
		Op op;
		if(isFloating(orig) == true && isFloating(target) == false){ // floating -> integer
			op = isFloat(orig) ? Op.CONV_F2I : Op.CONV_D2L;
		}else if(isFloating(orig) == false && isFloating(target) == true){ // integer -> floating
			op = isFloat(target) ? Op.CONV_I2F : Op.CONV_L2D;
		}else if(isFloating(orig) == true && isFloating(target) == true){ // floating -> floating
			op = isFloat(orig) ? Op.CONV_F2D : Op.CONV_D2F;
		}else{
			op = Op.CAST;
		}
		return new TypeCastItem(board, op, src, dest, orig, target);
	}
	
}

class SelectItem extends SchedulerItem{
	
	public final Operand target;
	public final Operand[] pat;
	
	public SelectItem(SchedulerBoard board, Operand target, Operand[] pat){
		super(board, Op.SELECT, new Operand[]{target}, null);
		this.target = target;
		this.pat = pat;
	}

	public String info(){
		String s = super.info();
		s += " (" + target + "->" + target + ")";
		return s;
	}
	
}
