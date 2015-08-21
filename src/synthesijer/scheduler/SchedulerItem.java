package synthesijer.scheduler;

import java.util.Hashtable;

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
	 * operator
	 */
	private Op op;
	
	/**
	 * source variables
	 */
	private Operand[] src;
	
	public final Hashtable<Operand, SchedulerItem> pred = new Hashtable<>();
	
	/**
	 * a destination variable
	 */
	private VariableOperand dest;
	
	/**
	 * the index values to jump, which available when this is a kind of jump operations. 
	 */
	private int[] branchIDs;
	
	private SchedulerSlot slot;
	
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
	
	public void setSlot(SchedulerSlot slot){
		this.slot = slot;
	}

	public SchedulerSlot getSlot(){
		return slot;
	}
	
	public Op getOp(){
		return op;
	}
	
	public void overwriteOp(Op op){
		this.op = op;
	}

	public void overwriteSrc(int index, Operand o){
		this.src[index] = o;
	}

	public String getBoardName(){
		return board.getName();
	}
	
	public int getStepId(){
		return getSlot().getStepId();
	}
	
/*
	public void setStepId(int id){
		this.stepID = id;
		if(op.isBranch == false){
			branchIDs = new int[]{id + 1}; 
		}
	}
*/
	
	public void setBranchId(int id){
		branchIDs = new int[]{id}; 
	}
	
	public void setBranchIds(int[] ids){
		branchIDs = ids; 
	}
	
	public void remapBranchIds(Hashtable<Integer, Integer> map){
		for(int i = 0; i < branchIDs.length; i++){
			Integer r = map.get(branchIDs[i]);
			if(r != null) branchIDs[i] = r;
		}
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
			if(o.isChaining(this)) s+= ":chain";
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
		//for(int id: branchIDs){
		for(int id: getSlot().getNextStep()){
			//s += String.format("%s%s_%04d", sep, getBoardName(), id);
			s += String.format("%s%04d", sep, id);
			sep = ", ";
		}
		return s;
	}

	public String info(){
		String s = String.format("%s_%04d: op=%s, src=%s, dest=%s, next=%s", getBoardName(), getStepId(), op, srcInfo(), destInfo(), branchList());
		return s;
	}
	
	public String toSexp0(){
		String s = op.toString();
		
		// source operands
		//s += " (" + srcInfo() + ")";
		if(src != null){
			for(Operand o: src){
				s += " " + o.getName();
			}
		}
		
		if(this instanceof MethodInvokeItem){
			s += ((MethodInvokeItem)this).addInfo();
		}
		if(this instanceof SelectItem){
			s += ((SelectItem)this).addInfo();
		}
		if(this instanceof FieldAccessItem){
			s += ((FieldAccessItem)this).addInfo();
		}
		if(this instanceof TypeCastItem){
			s += ((TypeCastItem)this).addInfo();
		}

		return s;
	}
	
	public String nextList(){ 
		// next
		String s = "";
		String sep = "";
		s += ":next";
		s += " (";
		for(int id: getSlot().getNextStep()){
			s += sep + id; sep = " ";
		}
		s += ")";
		return s;

	}

	public String toSexp(){
		String s = "";
		// destination operand
		if(dest == null){
			s = "(" + toSexp0() + " " + nextList() + ")";
		}else{
			//s += " " + destInfo();
			s = "(SET " + dest.getName() + " (" + toSexp0() + ")" + " " + nextList() + ")";
		}
		
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
		String argsStr = "";
		for(String a: args) argsStr += " " + a;
		if(obj == null){
			s += " (name=" + name + ", args=" + argsStr + ")";
		}else{
			s += " (obj = " + obj.getName() + ", name=" + name + ", args=" + argsStr + ")";
		}
		return s;
	}
	
	public void setNoWait(boolean flag){
		noWaitFlag = flag;
	}
	
	public boolean isNoWait(){
		return noWaitFlag;
	}
	
	public String addInfo(){
		String s = "";
		if(obj != null){
			s = " :obj " + obj.getName();
		}
		s += " :no_wait " + noWaitFlag;
		s += " :name " + name;
		s += " :args (";
		for(int i = 0; i < args.length; i++){
			s += " " + args[i];
		}
		s += ")";
		return s;
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

	public String addInfo(){
		String s = "";
		s += " :obj " + obj.getName() + " :name " + name;
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
	
	public String addInfo(){
		String s = "";
		s += " :orig " + orig + " :target " + target;
		return s;
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
		s += " ( target=" + target.info();
		s += " pat=";
		String sep = "";
		for(Operand o: pat){
			s += sep + o.info();
			sep = ", ";
		}
		return s;
	}
	
	public String addInfo(){
		String s = "";
		s += " :target " + target.getName();
		s += " :patterns (";
		for(Operand o: pat){
			s += " " + o.getName();	
		}
		s += ")";
		return s;
	}
	
}
