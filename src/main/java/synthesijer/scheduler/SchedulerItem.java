package synthesijer.scheduler;

import java.util.Hashtable;
import java.util.HashMap;

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
	public Op op;

	/**
	 * source variables
	 */
	public Operand[] src;

	public final Hashtable<Operand, SchedulerItem> pred = new Hashtable<>();

	/**
	 * a destination variable
	 */
	public VariableOperand dest;

	/**
	 * the index values to jump, which available when this is a kind of jump
	 * operations.
	 */
	private int[] branchIDs;

	private SchedulerSlot slot;

	/**
	 *
	 * @param op
	 *            operator
	 * @param src
	 *            source variables
	 * @param dest
	 *            destination variables
	 */
	public SchedulerItem(SchedulerBoard board, Op op, Operand[] src, VariableOperand dest) {
		this.board = board;
		this.op = op;
		this.src = src;
		this.dest = dest;
	}
	
	public SchedulerItem copy(SchedulerBoard board, SchedulerSlot slot) {
		Operand[] src = null;
		if(this.src != null){
			src = new Operand[this.src.length];
			for(int i = 0; i < this.src.length; i++){
				src[i] = this.src[i];
			}
		}
		SchedulerItem item = new SchedulerItem(board, this.op, src, this.dest);
		item.copyEnvFrom(this, slot);
		return item;
	}

	protected void copyEnvFrom(SchedulerItem orig, SchedulerSlot slot){
		int[] ids = new int[orig.getBranchId().length];
		for(int i = 0; i < orig.getBranchId().length; i++){
			ids[i] = orig.getBranchId()[i];
		}
		this.setBranchIds(ids);
		this.setSlot(slot);
	}


	public void setSlot(SchedulerSlot slot) {
		this.slot = slot;
	}

	public SchedulerSlot getSlot() {
		return slot;
	}

	public Op getOp() {
		return op;
	}

	public void overwriteOp(Op op) {
		this.op = op;
	}

	public void overwriteSrc(int index, Operand o) {
		this.src[index] = o;
	}

	public String getBoardName() {
		return board.getName();
	}

	public int getStepId() {
		return getSlot().getStepId();
	}

	/*
	 * public void setStepId(int id){ this.stepID = id; if(op.isBranch ==
	 * false){ branchIDs = new int[]{id + 1}; } }
	 */

	public void setBranchId(int id) {
		branchIDs = new int[] { id };
	}

	public void setBranchIds(int[] ids) {
		branchIDs = ids;
	}

	public void remapBranchIds(HashMap<Integer, Integer> map) {
		for (int i = 0; i < branchIDs.length; i++) {
			Integer r = map.get(branchIDs[i]);
			if (r != null)
				branchIDs[i] = r;
		}
	}

	public int[] getBranchId() {
		return branchIDs;
	}

	public boolean isBranchOp() {
		return op.isBranch;
	}

	public Operand[] getSrcOperand() {
		return src;
	}
	
	public boolean hasSrcOperand() {
		return src != null && src.length > 0;
	}

	public VariableOperand getDestOperand() {
		return dest;
	}
	
	public void setDestOperand(VariableOperand v) {
		this.dest = v;
	}

	private String srcInfo() {
		if (src == null)
			return "";
		String s = "";
		String sep = "";
		for (Operand o : src) {
			s += sep + o.info();
			if (o.isChaining(this))
				s += ":chain";
			sep = ", ";
		}
		return s;
	}

	private String destInfo() {
		if (dest == null)
			return "";
		return dest.info();
	}

	private String branchList() {
		String s = "";
		String sep = "";
		// for(int id: branchIDs){
		for (int id : getSlot().getNextStep()) {
			// s += String.format("%s%s_%04d", sep, getBoardName(), id);
			s += String.format("%s%04d", sep, id);
			sep = ", ";
		}
		return s;
	}

	public String info() {
		String s = String.format("%s_%04d: op=%s, src=%s, dest=%s, next=%s", getBoardName(), getStepId(), op, srcInfo(),
				destInfo(), branchList());
		return s;
	}

	public String toSexp0() {
		String s = op.toString();

		// source operands
		// s += " (" + srcInfo() + ")";
		if (src != null) {
			for (Operand o : src) {
				s += " " + o.getName();
			}
		}

		s += addInfo();

		return s;
	}

	public String addInfo(){
		return "";
	}

	public String nextList() {
		// next
		String s = "";
		String sep = "";
		s += ":next";
		s += " (";
		for (int id : getSlot().getNextStep()) {
			s += sep + id;
			sep = " ";
		}
		s += ")";
		return s;

	}

	public String toSexp() {
		String s = "";
		// destination operand
		if (dest == null) {
			s = "(" + toSexp0() + " " + nextList() + ")";
		} else {
			// s += " " + destInfo();
			s = "(SET " + dest.getName() + " (" + toSexp0() + ")" + " " + nextList() + ")";
		}

		return s;
	}

	private boolean isArrayAccess(){
		switch(op){
			case ARRAY_ACCESS:
			case ARRAY_ACCESS_WAIT:
			case ARRAY_ACCESS0:
			case ARRAY_INDEX:
				return true;
			default:
				return false;
		}
	}

	public boolean isConflicted(SchedulerItem prev){
		if(this.getDestOperand() != null){
			for(Operand src: prev.getSrcOperand()){
				if(src == this.getDestOperand()) return true; // write after read
			}
			if(this.getDestOperand() instanceof VariableRefOperand && prev.isArrayAccess()){
				if(((VariableRefOperand)getDestOperand()).getRef() == prev.getSrcOperand()[0]){
					return true; // index operation dependency	
				}
			}
		}
		if(prev.getDestOperand() != null){
			for(Operand src: this.getSrcOperand()){
				if(src == prev.getDestOperand()) return true; // read after write
			}
			if(prev.getDestOperand() instanceof VariableRefOperand && this.isArrayAccess()){
				if(((VariableRefOperand)prev.getDestOperand()).getRef() == this.getSrcOperand()[0]){
					return true; // index operation dependency	
				}
			}
		}
		if(this.getDestOperand() != null && prev.getDestOperand() != null){
			if(prev.getDestOperand() == this.getDestOperand()) return true; // write after write
		}
		if(isArrayAccess() && prev.isArrayAccess()){
			if(getSrcOperand()[0] == prev.getSrcOperand()[0]) return true;
		}
		return false;
	}


}

class MethodEntryItem extends SchedulerItem {

	public final String name;

	public MethodEntryItem(SchedulerBoard board, String name) {
		super(board, Op.METHOD_ENTRY, null, null);
		this.name = name;
	}

	public String info() {
		String s = super.info();
		s += " (name=" + name + ")";
		return s;
	}

	public MethodEntryItem copy(SchedulerBoard board, SchedulerSlot slot) {
		MethodEntryItem item = new MethodEntryItem(board, this.name);
		item.copyEnvFrom(this, slot);
		return item;
	}

}

class MethodInvokeItem extends SchedulerItem {

	public final VariableOperand obj;
	public final String name;
	public final String[] args;

	private boolean noWaitFlag = false;

	public MethodInvokeItem(SchedulerBoard board, Op op, VariableOperand obj, String name, Operand[] src, VariableOperand dest, String[] args) {
		super(board, op, src, dest);
		this.name = name;
		this.obj = obj;
		this.args = args;
	}

	public MethodInvokeItem(SchedulerBoard board, String name, Operand[] src, VariableOperand dest, String[] args) {
		this(board, Op.CALL, null, name, src, dest, args);
	}

	public MethodInvokeItem(SchedulerBoard board, VariableOperand obj, String name, Operand[] src, VariableOperand dest,
							String[] args) {
		this(board, Op.EXT_CALL, obj, name, src, dest, args);
	}

	public MethodInvokeItem copy(SchedulerBoard board, SchedulerSlot slot) {
		Operand[] src = null;
		Operand[] orig = this.getSrcOperand();
		if(orig != null){
			src = new Operand[orig.length];
			for(int i = 0; i < this.getSrcOperand().length; i++){
				src[i] = this.getSrcOperand()[i];
			}
		}
		MethodInvokeItem item = new MethodInvokeItem(board, this.getOp(), this.obj, this.name, src, this.getDestOperand(), this.args);
		item.copyEnvFrom(this, slot);
		return item;
	}

	public String info() {
		String s = super.info();
		String argsStr = "";
		for (String a : args)
			argsStr += " " + a;
		if (obj == null) {
			s += " (name=" + name + ", args=" + argsStr + ")";
		} else {
			s += " (obj = " + obj.getName() + ", name=" + name + ", args=" + argsStr + ")";
		}
		return s;
	}

	public void setNoWait(boolean flag) {
		noWaitFlag = flag;
	}

	public boolean isNoWait() {
		return noWaitFlag;
	}

	public String addInfo() {
		String s = "";
		if (obj != null) {
			s = " :obj " + obj.getName();
		}
		s += " :no_wait " + noWaitFlag;
		s += " :name " + name;
		s += " :args (";
		for (int i = 0; i < args.length; i++) {
			s += " " + args[i];
		}
		s += ")";
		return s;
	}

}

class FieldAccessItem extends SchedulerItem {

	public final VariableOperand obj;
	public final String name;

	public FieldAccessItem(SchedulerBoard board, VariableOperand obj, String name, Operand[] src,
						   VariableOperand dest) {
		super(board, Op.FIELD_ACCESS, src, dest);
		this.name = name;
		this.obj = obj;
	}

	public FieldAccessItem copy(SchedulerBoard board, SchedulerSlot slot) {
		Operand[] src = null;
		Operand[] orig = this.getSrcOperand();
		if(orig != null){
			src = new Operand[orig.length];
			for(int i = 0; i < this.getSrcOperand().length; i++){
				src[i] = this.getSrcOperand()[i];
			}
		}
		FieldAccessItem item = new FieldAccessItem(board, this.obj, this.name, src, this.getDestOperand());
		item.copyEnvFrom(this, slot);
		return item;
	}

	public String info() {
		String s = super.info();
		if (obj == null) {
			s += " (name=" + name + ")";
		} else {
			s += " (obj=" + obj.getName() + ", name=" + name + ")";
		}
		return s;
	}

	public String addInfo() {
		String s = "";
		s += " :obj " + obj.getName() + " :name " + name;
		return s;
	}

}

class TypeCastItem extends SchedulerItem {

	public final Type orig;
	public final Type target;

	private TypeCastItem(SchedulerBoard board, Op op, Operand src, VariableOperand dest, Type orig, Type target) {
		super(board, op, new Operand[] { src }, dest);
		this.orig = orig;
		this.target = target;
	}

	public TypeCastItem copy(SchedulerBoard board, SchedulerSlot slot) {
		Operand[] src = null;
		Operand[] origSrc = this.getSrcOperand();
		if(origSrc != null){
			src = new Operand[origSrc.length];
			for(int i = 0; i < this.getSrcOperand().length; i++){
				src[i] = this.getSrcOperand()[i];
			}
		}
		TypeCastItem item = new TypeCastItem(board, this.getOp(), src[0], this.getDestOperand(), this.orig, this.target);
		item.copyEnvFrom(this, slot);
		return item;
	}

	public String info() {
		String s = super.info();
		s += " (" + orig + "->" + target + ")";
		return s;
	}

	private static boolean isFloat(Type t) {
		return t == PrimitiveTypeKind.FLOAT;
	}

	private static boolean isDouble(Type t) {
		return t == PrimitiveTypeKind.DOUBLE;
	}

	private static boolean isFloating(Type t) {
		return isFloat(t) || isDouble(t);
	}

	public static TypeCastItem newCastItem(SchedulerBoard board, Operand src, VariableOperand dest, Type orig,
										   Type target) {
		Op op;
		if (isFloating(orig) == true && isFloating(target) == false) { // floating
			// ->
			// integer
			op = isFloat(orig) ? Op.CONV_F2I : Op.CONV_D2L;
		} else if (isFloating(orig) == false && isFloating(target) == true) { // integer
			// ->
			// floating
			op = isFloat(target) ? Op.CONV_I2F : Op.CONV_L2D;
		} else if (isFloating(orig) == true && isFloating(target) == true) { // floating
			// ->
			// floating
			op = isFloat(orig) ? Op.CONV_F2D : Op.CONV_D2F;
		} else {
			op = Op.CAST;
		}
		return new TypeCastItem(board, op, src, dest, orig, target);
	}

	public String addInfo() {
		String s = "";
		s += " :orig " + orig + " :target " + target;
		return s;
	}

}

class SelectItem extends SchedulerItem {

	public final Operand target;
	public final Operand[] pat;

	public SelectItem(SchedulerBoard board, Operand target, Operand[] pat) {
		super(board, Op.SELECT, new Operand[] { target }, null);
		this.target = target;
		this.pat = pat;
	}

	public SelectItem copy(SchedulerBoard board, SchedulerSlot slot) {
		Operand[] newPat = null;
		Operand[] origPat = this.pat;
		if(origPat != null){
			newPat = new Operand[origPat.length];
			for(int i = 0; i < origPat.length; i++){
				newPat[i] = origPat[i];
			}
		}
		SelectItem item = new SelectItem(board, this.target, newPat);
		item.copyEnvFrom(this, slot);
		return item;
	}


	public String info() {
		String s = super.info();
		s += " ( target=" + target.info();
		s += " pat=";
		String sep = "";
		for (Operand o : pat) {
			s += sep + o.info();
			sep = ", ";
		}
		return s;
	}

	public String addInfo() {
		String s = "";
		s += " :target " + target.getName();
		s += " :patterns (";
		for (Operand o : pat) {
			s += " " + o.getName();
		}
		s += ")";
		return s;
	}

}
