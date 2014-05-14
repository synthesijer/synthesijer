package synthesijer.hdl;

import java.util.ArrayList;

import synthesijer.SynthesijerUtils;
import synthesijer.hdl.expr.HDLCombinationExpr;

public class HDLSequencer implements HDLTree{
	
	private final HDLModule module;
	
	private final HDLSignal stateKey;
	private final HDLUserDefinedType stateType;
	private ArrayList<SequencerState> states;
	private SequencerState idle;
	private int timestep = -1;
	
	public HDLSequencer(HDLModule module, String key){
		this.module = module;
		this.stateType = module.newUserDefinedType(key, null, 0);
		this.stateKey = module.newSignal(key, stateType);
		this.idle = new SequencerState(stateKey, stateKey.getName() + "_IDLE");
		stateType.addItem(stateKey.getName() + "_IDLE");
		states = new ArrayList<SequencerState>();
		states.add(idle);
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
		SequencerState s = new SequencerState(stateKey, id);
		states.add(s);
		stateType.addItem(id);
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
	
	public class SequencerState{
		
		private ArrayList<StateTransitCondition> transitions = new ArrayList<StateTransitCondition>();
		
		private final HDLSignal key;
		private final String id;
		
		public SequencerState(HDLSignal key, String id){
			this.key = key;
			this.id = id;
		}
		
		public String getStateId(){
			return id;
		}
		
		public HDLSignal getKey(){
			return key;
		}
		
		public void addStateTransit(HDLExpr expr, SequencerState d){
			transitions.add(new StateTransitCondition(key, id, expr, d));
			stateType.addItem(id);
			if(expr == null) return;
			if(expr.getType().isBit()) return;
			SynthesijerUtils.error(String.format("%s is not allowed, only bit type is allowd", expr));
		}

		public void addStateTransit(SequencerState dest){
			transitions.add(new StateTransitCondition(key, id, null, dest));
			stateType.addItem(id);
		}

		public ArrayList<StateTransitCondition> getTransitions(){
			return transitions;
		}
		
	}
	
	public class StateTransitCondition{
		final HDLSignal stateKey;
		final String stateId;
		final HDLExpr cond;
		final SequencerState destState;
		
		StateTransitCondition(HDLSignal stateKey, String stateId, HDLExpr cond, SequencerState dest){
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
	}

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLSequencer(this);
	}
}