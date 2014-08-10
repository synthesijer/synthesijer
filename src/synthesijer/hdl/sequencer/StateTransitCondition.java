package synthesijer.hdl.sequencer;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.expr.HDLValue;

public class StateTransitCondition{
	final HDLSignal stateKey;
	final HDLValue stateId;
	final HDLExpr cond;
	final SequencerState destState;
			
	public StateTransitCondition(HDLSignal stateKey, HDLValue stateId, HDLExpr cond, SequencerState dest){
		this.stateKey = stateKey;
		this.stateId = stateId;
		this.cond = cond;
		this.destState = dest;
	}
	
	public SequencerState getDestState(){
		return destState;
	}
	
	public String getCondExprAsVHDL(){
		String s = "";
		if(cond != null){
			s = String.format("%s = '1'", cond.getResultExpr().getVHDL());
		}
		return s;
	}
	
	public String getCondExprAsVerilogHDL(){
		String s = "";
		if(cond != null){
			s = String.format("%s == 1'b1", cond.getResultExpr().getVHDL());
		}
		return s;
	}
	
	public boolean hasCondition(){
		return (cond != null);
	}
}
