package synthesijer.ast2hdl;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.model.State;
import synthesijer.model.Statemachine;
import synthesijer.model.StatemachineVisitor;
import synthesijer.model.Transition;

class Statemachine2HDLSequencerVisitor implements StatemachineVisitor {
	
	private final GenerateHDLModuleVisitor parent;
	private final HDLPort req;
	private final HDLSignal req_local;
	private final HDLPort busy;
	
	public Statemachine2HDLSequencerVisitor(GenerateHDLModuleVisitor parent, HDLPort req, HDLSignal req_local, HDLPort busy) {
		this.parent = parent;
		this.req = req;
		this.req_local = req_local;
		this.busy = busy;
	}
	
	private void addStateTransition(HDLSequencer.SequencerState ss, Transition t){
		if(t.getCondition() == null){
			ss.addStateTransit(parent.stateTable.get(t.getDestination()));
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
		HDLExpr kickExpr = parent.module.newExpr(
				HDLOp.OR,
				parent.module.newExpr(HDLOp.EQ, req.getSignal(), HDLPreDefinedConstant.HIGH),
				parent.module.newExpr(HDLOp.EQ, req_local, HDLPreDefinedConstant.HIGH));
		HDLSequencer.SequencerState entryState = parent.stateTable.get(o.getEntryState()); 
		hs.getIdleState().addStateTransit(kickExpr, entryState);
		busy.getSignal().setAssign(null,
				parent.module.newExpr(HDLOp.IF,
						parent.module.newExpr(HDLOp.EQ, hs.getStateKey(), hs.getIdleState().getStateId()),
						HDLPreDefinedConstant.LOW,
						HDLPreDefinedConstant.HIGH));
	}
	
	@Override
	public void visitState(State o) {
		// TODO Auto-generated method stub
		
	}

}
