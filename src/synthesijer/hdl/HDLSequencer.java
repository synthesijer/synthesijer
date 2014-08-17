package synthesijer.hdl;

import java.util.ArrayList;

import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.hdl.sequencer.SequencerState;

public class HDLSequencer implements HDLTree{
	
	private final HDLModule module;
	
	private final HDLSignal stateKey;
	private final HDLUserDefinedType stateType;
	private ArrayList<SequencerState> states;
	private SequencerState idle;
	private int timestep = -1;
	
	private final HDLSignal delayCounter; 

	public HDLSequencer(HDLModule module, String key){
		this.module = module;
		this.stateType = module.newUserDefinedType(key, null, 0);
		this.stateKey = module.newSignal(key, stateType);
		HDLValue idleId = stateType.addItem(stateKey.getName() + "_IDLE");
		this.idle = new SequencerState(this, stateKey, idleId);
		states = new ArrayList<>();
		states.add(idle);
		delayCounter = module.newSignal(key + "_delay", HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		delayCounter.setDefaultValue(HDLPreDefinedConstant.VECTOR_ZERO);
		delayCounter.setIgnore(true);
	}
	
	public void setTransitionTime(int step){
		this.timestep = step;
	}
	
	public int getTransitionTime(){
		return timestep;
	}
	
	public boolean hasTransitionTime(){
		return (timestep > 0);
	}
	
	public SequencerState addSequencerState(String id){
		HDLValue value = stateType.addItem(stateKey.getName()+"_"+id);
		SequencerState s = new SequencerState(this, stateKey, value);
		states.add(s);
		return s;
	}
	
	public HDLModule getModule(){
		return module;
	}
	
	public HDLSignal getStateKey(){
		return stateKey;
	}
	
	public ArrayList<SequencerState> getStates(){
		return states;
	}
	
	public SequencerState getIdleState(){
		return idle;
	}
	
	public HDLSignal getDelayCounter(){
		return delayCounter;
	}
		
	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLSequencer(this);
	}
}