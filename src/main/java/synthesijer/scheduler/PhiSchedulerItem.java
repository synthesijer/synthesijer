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
public class PhiSchedulerItem extends SchedulerItem {

	public final SchedulerSlot[] pat;
	private String[] tmp_pat;

	public PhiSchedulerItem(SchedulerBoard board, SchedulerSlot[] pat, Operand[] src, VariableOperand dest) {
		super(board, Op.PHI, src, dest);
		this.pat = pat;
		this.tmp_pat = null;
	}

	public PhiSchedulerItem(SchedulerBoard board, String[] tmp_pat, Operand[] src, VariableOperand dest) {
		super(board, Op.PHI, src, dest);
		this.pat = new SchedulerSlot[tmp_pat.length];
		this.tmp_pat = tmp_pat;
	}

	/**
	 * update pat with Slot-ID in tmp_pat
	 * @TOOD : too ad-hoc
	 */ 
	public void updatePhiPattern(){
		if(tmp_pat == null) return;
		for(int i = 0; i < tmp_pat.length; i++){
			int id = Integer.parseInt(tmp_pat[i]);
			SchedulerSlot slot = board.getSlot(id);
			if(slot == null){
				throw new RuntimeException("undefined slot for PHI-op: " + id);
			}
			pat[i] = slot;
		}
		tmp_pat = null;
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
		item.copyEnvFrom(this, slot);
		return item;
	}

	public void remap(HashMap<Integer, SchedulerSlot> table) {
		for(int i = 0; i < pat.length; i++){
			pat[i] = table.get(pat[i].getStepId());
		}
	}
		
	public String info() {
		String s = super.info();
		s += " (";
		s += " pat=";
		String sep = "";
		for (SchedulerSlot slot: pat) {
			if(slot != null) s += sep + slot.getStepId();
			sep = ", ";
		}
		s += ")";
		return s;
	}

	public String dot() {
		String s0 = "";
		s0 += " (";
		String sep = "";
		for (SchedulerSlot slot: pat) {
			if(slot != null) s0 += sep + slot.getStepId();
			sep = ", ";
		}
		s0 += ")";
		
		String s = "";
		s += String.format("[%s %s]", "PHI", s0);
		if(!srcInfo().equals("")) s += String.format("\\lsrc=%s", srcInfo());
		if(!destInfo().equals("")) s += String.format("\\ldest=%s", destInfo());
		return s;
	}

	public String addInfo() {
		String s = "";
		s += " :patterns (";
		for (SchedulerSlot slot : pat) {
			if(slot != null) s += " " + slot.getStepId();
		}
		s += ")";
		return s;
	}

}
