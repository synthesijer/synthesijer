package synthesijer.hdl;

import java.io.PrintWriter;
import java.util.ArrayList;

public class HDLSequencer {
	
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
	
	public SequencerState getIdleState(){
		return idle;
	}
	
	public void genStateDefinitionAsVHDL(PrintWriter dest, int offset){
		HDLUtils.println(dest, offset, String.format("type StateType_%s is (", stateKey));
		String sep = "";
		for(SequencerState s: states){
			HDLUtils.print(dest, 0, sep);
			HDLUtils.print(dest, offset+2, String.format("%s", s.getStateId()));
			sep = ",\n";
		}
		HDLUtils.println(dest, offset, String.format("\n  );", stateKey));
		HDLUtils.println(dest, offset, String.format("signal %s : StateType_%s := %s;", stateKey, stateKey, idle.getStateId()));
		HDLUtils.nl(dest);
	}
	
	public void genStateDefinitionAsVerilogHDL(PrintWriter dest, int offset){
		for(int i = 0; i < states.size(); i++){
			HDLUtils.println(dest, offset, String.format("parameter %s = 32'd%d;", states.get(i).getStateId(), i));
		}
		HDLUtils.println(dest, offset, String.format("reg [31:0] %s;", stateKey));
		HDLUtils.nl(dest);
	}
	
	public void genSequencerAsVHDL(PrintWriter dest, int offset){
		HDLUtils.println(dest, offset, String.format("process (%s)", module.getSysClkName()));
		HDLUtils.println(dest, offset, String.format("begin"));
		HDLUtils.println(dest, offset+2, String.format("if %s'event and %s = '1' then", module.getSysClkName(), module.getSysClkName()));
		
		// reset
		HDLUtils.println(dest, offset+4, String.format("if %s = '1' then", module.getSysResetName()));
		HDLUtils.println(dest, offset+6, String.format("%s <= %s;", stateKey, idle.getStateId()));
		
		HDLUtils.println(dest, offset+4, String.format("else"));
		
		// state-machine body
		if(states.size() > 0){
			HDLUtils.println(dest, offset+6, String.format("case (%s) is", stateKey));
			for(SequencerState s: states){
				HDLUtils.println(dest, offset+8, String.format("when %s => ", s.getStateId()));
				s.generateStateTransitionAsVHDL(dest, offset+10);
			}
			HDLUtils.println(dest, offset+8, String.format("when others => null;"));
			HDLUtils.println(dest, offset+6, String.format("end case;"));
		}
		
		HDLUtils.println(dest, offset+4, String.format("end if;"));
		HDLUtils.println(dest, offset+2, String.format("end if;"));
		HDLUtils.println(dest, offset, String.format("end process;"));
		HDLUtils.nl(dest);
	}
	
	public void genSequencerAsVerilogHDL(PrintWriter dest, int offset){
		HDLUtils.println(dest, offset, String.format("always @(posedge %s) begin", module.getSysClkName()));
		
		// reset
		HDLUtils.println(dest, offset+2, String.format("if(%s == 1'b1) begin", module.getSysResetName()));
		HDLUtils.println(dest, offset+4, String.format("%s <= %s;", stateKey, idle.getStateId()));
		
		HDLUtils.println(dest, offset+2, String.format("end else begin"));
		
		// state-machine body
		if(states.size() > 0){
			HDLUtils.println(dest, offset+4, String.format("case (%s)", stateKey));

			for(SequencerState s: states){
				HDLUtils.println(dest, offset+6, String.format("%s : begin", s.getStateId()));
				s.generateStateTransitionAsVerilogHDL(dest, offset+8);
				HDLUtils.println(dest, offset+6, String.format("end"));
			}
			HDLUtils.println(dest, offset+4, String.format("endcase"));
		}
		
		HDLUtils.println(dest, offset+2, String.format("end"));
		HDLUtils.println(dest, offset, String.format("end"));
		HDLUtils.nl(dest);
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
		
		public void addStateTransit(SequencerState dest, String phaseKey, String phaseId, HDLExpr cond, HDLExpr condValue){
			transitions.add(new StateTransitCondition(key, id, phaseKey, phaseId, cond, condValue, dest));
		}
		
		public void generateStateTransitionAsVHDL(PrintWriter dest, int offset){
			if(transitions.size() > 0){
				String sep = "if";
				for(StateTransitCondition c: transitions){
					HDLUtils.println(dest, offset, String.format("%s %s then", sep, c.getCondExprAsVHDL()));
					HDLUtils.println(dest, offset+2, String.format("%s <= %s;", key, c.destState.getStateId()));
					sep = "elsif";
				}
				HDLUtils.println(dest, offset, String.format("end if;"));
			}else{
				HDLUtils.println(dest, offset, "null;");
			}
		}
		
		public void generateStateTransitionAsVerilogHDL(PrintWriter dest, int offset){
			if(transitions.size() > 0){
				String sep = "";
				for(StateTransitCondition c: transitions){
					HDLUtils.println(dest, offset, String.format("%sif (%s) begin", sep, c.getCondExprAsVerilogHDL()));
					HDLUtils.println(dest, offset+2, String.format("%s <= %s;", key, c.destState.getStateId()));
					sep = "end else ";
				}
				HDLUtils.println(dest, offset, String.format("end"));
			}else{
			}
		}
	}
	
	class StateTransitCondition{
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
		
		public String getCondExprAsVHDL(){
			String s = stateKey + " = " + stateId;
			if(phaseId != null) s += " and " + phaseKey + " = " + phaseId;
			if(cond != null) s += " and " + cond.getVHDL() + " = " + condValue.getVHDL();
			return s;
		}
		
		public String getCondExprAsVerilogHDL(){
			String s = stateKey + " == " + stateId;
			if(phaseId != null) s += " && " + phaseKey + " == " + phaseId;
			if(cond != null) s += " && " + cond.getVerilogHDL() + " == " + condValue.getVerilogHDL();
			return s;
		}
	}
}