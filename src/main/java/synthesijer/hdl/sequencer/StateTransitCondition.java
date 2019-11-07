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

	public String toLabel(){
		String s = "";
		if(cond != null){
			s = String.format("%s = '1'", cond.getResultExpr().getVHDL());
		}
		return s;
	}

	public String getCondExprAsVHDL(){
		String s = "";
		if(cond != null){
			if(cond.getResultExpr().getType().isBit()){
				s = String.format("%s = '1'", cond.getResultExpr().getVHDL());
			}else{
				if(cond.getResultExpr().getType().isVector()){
					s = String.format("signed(%s) /= 0", cond.getResultExpr().getVHDL());
				}else{
					s = String.format("%s /= 0", cond.getResultExpr().getVHDL());
				}
			}
		}
		return s;
	}

	public String getCondExprAsVerilogHDL(){
		String s = "";
		if(cond != null){
			if(cond.getResultExpr().getType().isBit()){
				s = String.format("%s == 1'b1", cond.getResultExpr().getVerilogHDL());
			}else{
				s = String.format("%s != 0", cond.getResultExpr().getVerilogHDL());
			}
		}
		return s;
	}

	public boolean hasCondition(){
		return (cond != null);
	}

	public HDLValue getStateId(){
		return stateId;
	}
}
