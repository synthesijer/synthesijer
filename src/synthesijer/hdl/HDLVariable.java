package synthesijer.hdl;

public interface HDLVariable extends HDLExpr{
	
	public void setAssign(HDLSequencer.SequencerState s, HDLExpr expr);
	
	public void setAssign(HDLSequencer.SequencerState s, int count, HDLExpr expr);
	
	public void setResetValue(HDLExpr s);
	
	public void setDefaultValue(HDLExpr s);

}
