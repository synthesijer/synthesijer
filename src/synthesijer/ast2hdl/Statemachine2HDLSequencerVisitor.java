package synthesijer.ast2hdl;

import synthesijer.SynthesijerUtils;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.sequencer.SequencerState;
import synthesijer.model.State;
import synthesijer.model.Statemachine;
import synthesijer.model.StatemachineVisitor;
import synthesijer.model.Transition;

class Statemachine2HDLSequencerVisitor implements StatemachineVisitor {
	
	private final GenerateHDLModuleVisitor parent;
	private final HDLExpr kickExpr;
	private final HDLSignal busySig;
	private HDLSequencer hs; 
	
	public Statemachine2HDLSequencerVisitor(GenerateHDLModuleVisitor parent, HDLExpr reqExpr, HDLSignal busySig) {
		this.parent = parent;
		this.kickExpr = reqExpr;
		this.busySig = busySig;
	}

	public Statemachine2HDLSequencerVisitor(GenerateHDLModuleVisitor parent){
		this(parent, null, null);
	}

	private void addStateTransition(SequencerState ss, Transition t){
		if(t.getCondition() == null){
			if(t.getDestination() != null){
				ss.addStateTransit(parent.stateTable.get(t.getDestination()));
			}else{
				SynthesijerUtils.warn(String.format("destination state missing from %s_%s", ss.getKey().getName(), ss.getStateId().getValue()));
			}
		}else{
			HDLExpr expr0, expr1;
			if(t.getPattern() != null){
				//GenerateHDLExprVisitor v = new GenerateHDLExprVisitor(parent, null);
				GenerateHDLExprVisitor v = new GenerateHDLExprVisitor(parent, ss);
				t.getPattern().accept(v);
				expr1 = v.getResult();
			}else{
				expr1 = t.getFlag() ? HDLPreDefinedConstant.HIGH : HDLPreDefinedConstant.LOW;
			}
			//GenerateHDLExprVisitor v = new GenerateHDLExprVisitor(parent, null);
			GenerateHDLExprVisitor v = new GenerateHDLExprVisitor(parent, ss);
			t.getCondition().accept(v);
			expr0 = v.getResult();
			HDLExpr expr = parent.module.newExpr(HDLOp.EQ, expr0, expr1);
			ss.addStateTransit(expr, parent.stateTable.get(t.getDestination()));
		}
	}
	
	private boolean isAuto(){
		return (kickExpr == null && busySig == null);
	}

	@Override
	public void visitStatemachine(Statemachine o) {
		hs = parent.module.newSequencer(o.getKey());
		for(State s: o.getStates()){
			parent.stateTable.put(s, hs.addSequencerState(s.getId()));
		}
		for(State s: o.getStates()){
			SequencerState ss = parent.stateTable.get(s);
			for(Transition t: s.getTransitions()){
				//System.out.println("state=" + s + " => " + t);
				addStateTransition(ss, t);
			}
			if(s.isTerminate()){
				ss.addStateTransit(hs.getIdleState());
			}
		}
		SequencerState entryState = parent.stateTable.get(o.getEntryState());
		if(isAuto() == false){
			hs.getIdleState().addStateTransit(kickExpr, entryState);
			busySig.setAssign(null,
					parent.module.newExpr(HDLOp.IF,
							parent.module.newExpr(HDLOp.EQ, hs.getStateKey(), hs.getIdleState().getStateId()),
							HDLPreDefinedConstant.LOW,
							HDLPreDefinedConstant.HIGH));
		}else{
			hs.getIdleState().addStateTransit(entryState);
		}
	}
	
	public HDLSequencer getHDLSequencer(){
		return hs;
	}
	
	@Override
	public void visitState(State o) {
		// TODO Auto-generated method stub
		
	}

}
