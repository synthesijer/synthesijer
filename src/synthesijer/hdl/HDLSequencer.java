package synthesijer.hdl;

import java.util.ArrayList;

public class HDLSequencer implements HDLTree{
	
	private final HDLModule module;
	
	private final String stateKey;
	private ArrayList<SequencerState> states;
	private SequencerState idle;
	
	public HDLSequencer(HDLModule module, String stateKey){
		this.module = module;
		this.stateKey = stateKey;
		this.idle = new SequencerState(stateKey, stateKey + "_IDLE");
		states = new ArrayList<SequencerState>();
		states.add(idle);
	}
	
	public SequencerState addSequencerState(String id){
		SequencerState s = new SequencerState(stateKey, id);
		states.add(s);
		return s;
	}
	
	public HDLModule getModule(){
		return module;
	}
	
	public String getStateKey(){
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
		
		private final String key;
		private final String id;
		
		public SequencerState(String key, String id){
			this.key = key;
			this.id = id;
		}
		
		public String getStateId(){
			return id;
		}
		
		public String getKey(){
			return key;
		}
		
		public void addStateTransit(SequencerState dest, String phaseKey, String phaseId, HDLExpr cond, HDLExpr condValue){
			transitions.add(new StateTransitCondition(key, id, phaseKey, phaseId, cond, condValue, dest));
		}

		public void addStateTransit(SequencerState dest){
			transitions.add(new StateTransitCondition(key, id, null, null, null, null, dest));
		}

		public ArrayList<StateTransitCondition> getTransitions(){
			return transitions;
		}
		
	}
	
	public class StateTransitCondition{
		final String stateKey;
		final String stateId;
		final String phaseKey;
		final String phaseId;
		final HDLExpr cond;
		final HDLExpr condValue;
		final SequencerState destState;
		
		StateTransitCondition(String stateKey, String stateId, String phaseKey, String phaseId, HDLExpr cond, HDLExpr condValue, SequencerState dest){
			this.stateKey = stateKey;
			this.stateId = stateId;
			this.phaseKey = phaseKey;
			this.phaseId = phaseId;
			this.cond = cond;
			this.condValue = condValue;
			this.destState = dest;
		}
		
		public SequencerState getDestState(){
			return destState;
		}
		
		public String getCondExprAsVHDL(){
			String s = "";
			String sep = "";
			if(phaseId != null){
				s += sep + phaseKey + " = " + phaseId;
				sep = " and ";
			}
			if(cond != null){
				s += sep + cond.getVHDL() + " = " + condValue.getVHDL();
				sep = " and ";
			}
			return s;
		}
		
		public String getCondExprAsVerilogHDL(){
			String s = "";
			String sep = "";
			if(phaseId != null){
				s += sep + phaseKey + " == " + phaseId;
				sep = " && ";
			}
			if(cond != null){
				s += sep + cond.getVerilogHDL() + " == " + condValue.getVerilogHDL();
				sep = " && ";
			}
			return s;
		}
	}

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLSequencer(this);
	}
}