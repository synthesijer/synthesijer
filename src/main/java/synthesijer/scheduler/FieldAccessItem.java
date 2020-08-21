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
public class FieldAccessItem extends SchedulerItem {

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

	public String dot() {
		String s = super.dot();
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
