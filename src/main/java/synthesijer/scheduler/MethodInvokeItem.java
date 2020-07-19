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

public class MethodInvokeItem extends SchedulerItem {

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
