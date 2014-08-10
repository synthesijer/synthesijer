package synthesijer.hdl;

import synthesijer.hdl.sequencer.SequencerState;

public interface HDLVariable extends HDLExpr{
	
	public void setAssign(SequencerState s, HDLExpr expr);
	
	public void setAssign(SequencerState s, int count, HDLExpr expr);
	
	public void setResetValue(HDLExpr s);
	
	public void setDefaultValue(HDLExpr s);

}
