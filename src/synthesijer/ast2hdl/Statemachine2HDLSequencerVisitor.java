package synthesijer.ast2hdl;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.expr.HDLConstant;
import synthesijer.model.State;
import synthesijer.model.Statemachine;
import synthesijer.model.StatemachineVisitor;
import synthesijer.model.Transition;

class Statemachine2HDLSequencerVisitor implements StatemachineVisitor {
	
	private final GenerateHDLModuleVisitor parent;
	private final HDLPort req;
	private final HDLPort busy;
	
	public Statemachine2HDLSequencerVisitor(GenerateHDLModuleVisitor parent, HDLPort req, HDLPort busy) {
		this.parent = parent;
		this.req = req;
		this.busy = busy;
	}
	
	private void addStateTransition(HDLSequencer.SequencerState ss, Transition t){
		if(t.getCondition() == null){
			ss.addStateTransit(parent.stateTable.get(t.getDestination()));
		}else{
			HDLExpr expr0, expr1;
			if(t.getPattern() != null){
				GenerateHDLExprVisitor v = new GenerateHDLExprVisitor(parent, null);
				t.getPattern().accept(v);
				expr1 = v.getResult();
			}else{
				expr1 = t.getFlag() ? HDLConstant.HIGH : HDLConstant.LOW;
			}
			GenerateHDLExprVisitor v = new GenerateHDLExprVisitor(parent, null);
			t.getCondition().accept(v);
			expr0 = v.getResult();
			HDLExpr expr = parent.module.newExpr(HDLOp.EQ, expr0, expr1);
			ss.addStateTransit(expr, parent.stateTable.get(t.getDestination()));
		}
	}

	@Override
	public void visitStatemachine(Statemachine o) {
		HDLSequencer hs = parent.module.newSequencer(o.getKey());
		for(State s: o.getStates()){
			parent.stateTable.put(s, hs.addSequencerState(s.getId()));
		}
		for(State s: o.getStates()){
			HDLSequencer.SequencerState ss = parent.stateTable.get(s);
			for(Transition t: s.getTransitions()){
				addStateTransition(ss, t);
			}
			if(s.isTerminate()){
				ss.addStateTransit(hs.getIdleState());
			}
		}
		HDLExpr kickExpr = parent.module.newExpr(HDLOp.EQ, req.getSignal(), HDLConstant.HIGH);
		HDLSequencer.SequencerState entryState = parent.stateTable.get(o.getEntryState()); 
		hs.getIdleState().addStateTransit(kickExpr, entryState);
		busy.getSignal().setAssign(null,
				parent.module.newExpr(HDLOp.IF,
						parent.module.newExpr(HDLOp.EQ, hs.getStateKey(), hs.getIdleState().getStateId()),
						HDLConstant.LOW,
						HDLConstant.HIGH));
	}
	
	@Override
	public void visitState(State o) {
		// TODO Auto-generated method stub
		
	}

}
