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
public class PhiSchedulerItem extends SchedulerItem {

	public final SchedulerSlot[] pat;

	public PhiSchedulerItem(SchedulerBoard board, SchedulerSlot[] pat, Operand[] src, VariableOperand dest) {
		super(board, Op.PHI, src, dest);
		this.pat = pat;
	}

	public PhiSchedulerItem copy(SchedulerBoard board, SchedulerSlot slot) {
		Operand[] newSrc = null;
		Operand[] origSrc = this.getSrcOperand();
		if(origSrc != null){
			newSrc = new Operand[origSrc.length];
			for(int i = 0; i < this.getSrcOperand().length; i++){
				newSrc[i] = this.getSrcOperand()[i];
			}
		}
		SchedulerSlot[] newPat = new SchedulerSlot[pat.length];
		for(int i = 0; i < this.pat.length; i++){
			newPat[i] = pat[i];
		}
		PhiSchedulerItem item = new PhiSchedulerItem(board, newPat, newSrc, this.getDestOperand());
		item.copyEnvFrom(this);
		return item;
	}


	public String info() {
		String s = super.info();
		s += " (";
		s += " pat=";
		String sep = "";
		for (SchedulerSlot slot: pat) {
			s += sep + slot.getStepId();
			sep = ", ";
		}
		s += ")";
		return s;
	}

	public String addInfo() {
		String s = "";
		s += " :patterns (";
		for (SchedulerSlot slot : pat) {
			s += " " + slot.getStepId();
		}
		s += ")";
		return s;
	}

}
