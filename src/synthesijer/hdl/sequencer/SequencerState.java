package synthesijer.hdl.sequencer;

import java.util.ArrayList;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.expr.HDLValue;

public class SequencerState{
	
	private ArrayList<StateTransitCondition> transitions = new ArrayList<>();
	
	private final HDLSignal key;
	private final HDLValue id;
	private final HDLSequencer seq;
			
	private int constantDelay = 0;
	private HDLSignal exitFlag = null;
	
	public SequencerState(HDLSequencer seq, HDLSignal key, HDLValue id){
		this.key = key;
		this.id = id;
		this.seq = seq;
	}
	
	public HDLSequencer getSequencer(){
		return seq;
	}
	
	public HDLValue getStateId(){
		return id;
	}
	
	public HDLSignal getKey(){
		return key;
	}

	public void addStateTransit(HDLExpr expr, SequencerState d){
		transitions.add(new StateTransitCondition(key, id, expr, d));
		//if(expr == null) return;
		//if(expr.getType().isBit()) return;
		//SynthesijerUtils.error(String.format("%s is not allowed, only bit type is allowd", expr));
	}

	public void addStateTransit(SequencerState dest){
		transitions.add(new StateTransitCondition(key, id, null, dest));
	}

	public ArrayList<StateTransitCondition> getTransitions(){
		return transitions;
	}
	
	public void setMaxConstantDelay(int v){
		if(constantDelay < v){
			constantDelay = v;
		}
	}
	
	public int getConstantDelay(){
		return constantDelay;
	}
	
	public void setStateExitFlag(HDLSignal expr){
		this.exitFlag = expr;
	}
	
	public String getExitConditionAsVHDL(){
		String s = "";
		String sep = "";
		if(constantDelay > 0){
			s += String.format("%s >= %d", seq.getDelayCounter().getName(), getConstantDelay());
			sep = " and ";
		}
		if(exitFlag != null){
			//System.out.println(exitFlag);
			if(exitFlag.getType().isBit()){
				s += sep + String.format("%s = '1'", exitFlag.getVHDL());
			}else{
				if(exitFlag.getType().isVector()){
					s += sep + String.format("signed(%s) /= 0", exitFlag.getVHDL());
				}else{
					s += sep + String.format("%s /= 0", exitFlag.getVHDL());
				}
			}
		}
		return s;
	}

	public String getExitConditionAsVerilogHDL(){
		String s = "";
		String sep = "";
		if(constantDelay > 0){
			s += String.format("%s >= %d", seq.getDelayCounter().getName(), getConstantDelay());
			sep = " && ";
		}
		if(exitFlag != null){
			if(exitFlag.getType().isBit()){
				s += sep + String.format("%s == 1'b1", exitFlag.getVerilogHDL());
			}else{
				s += sep + String.format("%s != 0", exitFlag.getVerilogHDL());
			}
		}
		return s;
	}

	public boolean hasExitCondition(){
		return (exitFlag != null) || (constantDelay > 0);
	}
}
