package synthesijer.hdl;

import java.util.ArrayList;

import synthesijer.SynthesijerUtils;
import synthesijer.hdl.expr.HDLConstant;
import synthesijer.hdl.expr.HDLValue;

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
		states = new ArrayList<SequencerState>();
		states.add(idle);
		delayCounter = module.newSignal(key + "_delay", HDLPrimitiveType.genSignedType(32), HDLSignal.ResourceKind.REGISTER);
		delayCounter.setDefaultValue(HDLConstant.INTEGER_ZERO);
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
		HDLValue value = stateType.addItem(id);
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
	
	public class SequencerState{
		
		private ArrayList<StateTransitCondition> transitions = new ArrayList<StateTransitCondition>();
		
		private final HDLSignal key;
		private final HDLValue id;
				
		int constantDelay = 0;

		public SequencerState(HDLSequencer seq, HDLSignal key, HDLValue id){
			this.key = key;
			this.id = id;
		}
		
		public HDLValue getStateId(){
			return id;
		}
		
		public HDLSignal getKey(){
			return key;
		}

		public void addStateTransit(HDLExpr expr, SequencerState d){
			transitions.add(new StateTransitCondition(key, id, expr, d));
			if(expr == null) return;
			if(expr.getType().isBit()) return;
			SynthesijerUtils.error(String.format("%s is not allowed, only bit type is allowd", expr));
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
		
		public boolean hasDelay(){
			return (constantDelay > 0);
		}
		
	}
	
	public class StateTransitCondition{
		final HDLSignal stateKey;
		final HDLValue stateId;
		final HDLExpr cond;
		final SequencerState destState;
				
		StateTransitCondition(HDLSignal stateKey, HDLValue stateId, HDLExpr cond, SequencerState dest){
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

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLSequencer(this);
	}
}