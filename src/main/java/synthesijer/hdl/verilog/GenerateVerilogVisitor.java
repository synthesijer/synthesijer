package synthesijer.hdl.verilog;

import java.io.PrintWriter;

import synthesijer.Constant;
import synthesijer.SynthesijerUtils;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLInstanceRef;
import synthesijer.hdl.HDLLiteral;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLParameter;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLTreeVisitor;
import synthesijer.hdl.HDLUserDefinedType;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.sequencer.SequencerState;
import synthesijer.hdl.sequencer.StateTransitCondition;

public class GenerateVerilogVisitor implements HDLTreeVisitor{

	private final PrintWriter dest;
	private int offset;

	public GenerateVerilogVisitor(PrintWriter dest, int offset){
		this.dest = dest;
		this.offset = offset;
	}

	@Override
	public void visitHDLExpr(HDLExpr o) {
		String str = "";
		str = String.format("assign %s = %s;", o.getResultExpr().getVerilogHDL(), o.getVerilogHDL());
		HDLUtils.println(dest, offset, str);
	}

	private void genParameterMap(HDLInstance o){
		HDLUtils.println(dest, offset, String.format("#("));
		String sep = "";

		for(HDLInstance.ParamPair pair: o.getParameterPairs()){
			if(pair.getValue() != null){
				HDLUtils.print(dest, 0, sep);
				HDLUtils.print(dest, offset+2, String.format(".%s(%s)", pair.param.getName(), pair.getValue().getVerilogHDL()));
			}else{
				HDLUtils.print(dest, 0, sep);
				HDLUtils.print(dest, offset+2, String.format(".%s(%s)", pair.param.getName(), pair.param.getValue().getVerilogHDL()));
			}
			sep = "," + Constant.BR;
		}

		HDLUtils.println(dest, 0, "");
		HDLUtils.println(dest, offset, ")");
	}

	private void genPortMap(HDLInstance o){
		HDLUtils.println(dest, offset, String.format("("));
		String sep = "";
		for(HDLInstance.PortPair pair: o.getPairs()){
			HDLUtils.print(dest, 0, sep);
			if(o.getSubModule().getSysClkPairItem() != null && pair.port.getName().equals(o.getSubModule().getSysClkPairItem().getName())){
				// target port to connect is system clock, the port should be connected system clock in this module directly.
				if(o.getModule().getSysClkPairItem() == null){
					SynthesijerUtils.warn(o.getModule().getName() + " does not have system clock, but sub-module requires system clock");
					SynthesijerUtils.warn("system clock of sub-module is remained as open, so the sub-module will not work well.");
				}else{
					HDLUtils.print(dest, offset+2, String.format(".%s(%s)", pair.port.getName(), o.getModule().getSysClkPairItem().getName()));
				}
			}else if(o.getSubModule().getSysResetPairItem() != null && pair.port.getName().equals(o.getSubModule().getSysResetPairItem().getName())){
				// target port to connect is system reset, the port should be connected system reset in this module directly.
				if(o.getModule().getSysResetPairItem() == null){
					SynthesijerUtils.warn(o.getModule().getName() + " does not have system reset, but sub-module requires system reset");
					SynthesijerUtils.warn("system reset of sub-module is remained as open, so the sub-module will not work well.");
				}else{
					HDLUtils.print(dest, offset+2, String.format(".%s(%s)", pair.port.getName(), o.getModule().getSysResetPairItem().getName()));
				}
			}else{
				HDLUtils.print(dest, offset+2, String.format(".%s(%s)", pair.port.getName(), pair.item.getName()));
			}
			sep = "," + Constant.BR;
		}
		HDLUtils.println(dest, 0, "");
		HDLUtils.println(dest, offset, ");");
	}

	@Override
	public void visitHDLInstance(HDLInstance o) {
		HDLUtils.println(dest, offset, o.getSubModule().getName());
		if(o.getSubModule().getParameters().length > 0){
			genParameterMap(o);
		}
		HDLUtils.println(dest, offset, o.getName());
		genPortMap(o);
		HDLUtils.nl(dest);
	}

	@Override
	public void visitHDLLitral(HDLLiteral o) {
		// TODO Auto-generated method stub

	}

	private void genParameterList(HDLModule o){
		HDLUtils.println(dest, offset, String.format("#("));
		String sep = "";
		for(HDLParameter p: o.getParameters()){
			dest.print(sep);
			p.accept(new GenerateVerilogDefVisitor(dest, offset+2));
			sep = "," + Constant.BR;
		}
		HDLUtils.println(dest, offset, Constant.BR + ")");
	}

	private void genPortList(HDLModule o){
		HDLUtils.println(dest, offset, String.format("("));
		String sep = "";
		for(HDLPort p: o.getPorts()){
			dest.print(sep);
			p.accept(new GenerateVerilogDefVisitor(dest, offset+2));
			sep = "," + Constant.BR;
		}
		HDLUtils.println(dest, offset, Constant.BR + ");");
	}

	@Override
	public void visitHDLModule(HDLModule o) {

		HDLUtils.println(dest, offset, String.format("module %s", o.getName()));
		if(o.getParameters().length > 0){
			genParameterList(o);
		}
		genPortList(o);
		HDLUtils.nl(dest);

		// definitions
		GenerateVerilogDefVisitor defVisitor = new GenerateVerilogDefVisitor(dest, offset+2);
		o.accept(defVisitor);

		// body
		for(HDLPort p: o.getPorts()){
			offset += 2;
			p.accept(this);
			offset -= 2;
		}
		HDLUtils.nl(dest);
		for(HDLExpr expr : o.getExprs()){
			offset += 2;
			expr.accept(this);
			offset -= 2;
		}
		HDLUtils.nl(dest);
		for(HDLSequencer m: o.getSequencers()){
			offset += 2;
			m.accept(this);
			offset -= 2;
		}
		HDLUtils.nl(dest);
		for(HDLSignal s: o.getSignals()){
			offset += 2;
			s.accept(this);
			offset -= 2;
		}
		HDLUtils.nl(dest);
		for(HDLInstance i: o.getModuleInstances()){
			offset += 2;
			i.accept(this);
			offset -= 2;
		}
		HDLUtils.nl(dest);
		HDLUtils.println(dest, offset, "endmodule");
	}

	@Override
	public void visitHDLPort(HDLPort o) {
		if(o.isSet(HDLPort.OPTION.NO_SIG) || o.isSet(HDLPort.OPTION.EXPORT_PATH)) return; // nothing to do
		if(o.getDir() == HDLPort.DIR.INOUT){
			return; // nothing to do
		}else if(o.isOutput()){
			HDLUtils.println(dest, offset, String.format("assign %s = %s;", o.getName(), o.getSignal().getName()));
		}else{
			HDLUtils.println(dest, offset, String.format("assign %s = %s;", o.getSignal().getName(), o.getName()));
		}
		o.getSignal().accept(this);
	}

	@Override
	public void visitHDLParameter(HDLParameter o) {
	}

	private void genSyncSequencerHeader(HDLSequencer o, int offset){
		HDLUtils.println(dest, offset, String.format("always @(posedge %s) begin", o.getModule().getSysClkName()));
	}

	private void genAsyncSequencerHeader(HDLSequencer o, int offset){
		HDLUtils.println(dest, offset, String.format("always begin"));
	}

	private void genSyncSequencerSwitch(HDLSequencer o, int offset){
		if(o.getStates().size() > 0){
			HDLUtils.println(dest, offset, String.format("case (%s)", o.getStateKey().getName()));
			for(SequencerState s: o.getStates()){
				HDLUtils.println(dest, offset+2, String.format("%s : begin", s.getStateId().getVerilogHDL()));

				if(s.hasExitCondition()){
					HDLUtils.println(dest, offset+4, String.format("if (%s) begin", s.getExitConditionAsVerilogHDL()));
					HDLUtils.println(dest, offset+6, String.format("%s <= 32'h0;", o.getDelayCounter().getName()));
					HDLUtils.println(dest, offset+6, String.format("%s <= %s;", o.getDelayCounter().getName(), o.getDelayCounter().getDefaultValue().getVerilogHDL()));
					genStateTransition(dest, offset+6, s);
					HDLUtils.println(dest, offset+4, String.format("end else begin"));
					HDLUtils.println(dest, offset+6, String.format("%s <= %s + 1;", o.getDelayCounter().getName(), o.getDelayCounter().getName()));
					HDLUtils.println(dest, offset+4, String.format("end"));
				}else{
					genStateTransition(dest, offset+4, s);
				}

				HDLUtils.println(dest, offset+2, String.format("end"));
			}
			HDLUtils.println(dest, offset, String.format("endcase"));
		}
		for(HDLSequencer.Pair p: o.getSeqExprList()){
			HDLUtils.println(dest, offset, String.format("%s <= %s;", p.dest.getVerilogHDL(), p.expr.getResultExpr().getVerilogHDL()));
		}
		for(HDLSequencer.Triple t: o.getSeqCondExprList()){
			HDLUtils.println(dest, offset, String.format("if(%s == 1'b1) begin", t.cond.getVerilogHDL()));
			HDLUtils.println(dest, offset + 2, String.format("%s <= %s;", t.dest.getVerilogHDL(), t.expr.getResultExpr().getVerilogHDL()));
			HDLUtils.println(dest, offset, String.format("end"));
		}
		for(HDLSequencer.Quatro t: o.getSeqCondExprList2()){
			HDLUtils.println(dest, offset, String.format("if(%s == %s) begin", t.a.getVerilogHDL(), t.b.getVerilogHDL()));
			HDLUtils.println(dest, offset + 2, String.format("%s <= %s;", t.dest.getVerilogHDL(), t.expr.getResultExpr().getVerilogHDL()));
			HDLUtils.println(dest, offset, String.format("end"));
		}
	}

	private void genSyncSequencerBody(HDLSequencer o, int offset){
		// reset
		if(o.getModule().isNegativeReset()){
			HDLUtils.println(dest, offset+2, String.format("if(%s == 1'b0) begin", o.getModule().getSysResetName()));
		}else{
			HDLUtils.println(dest, offset+2, String.format("if(%s == 1'b1) begin", o.getModule().getSysResetName()));
		}
		HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getStateKey().getName(), o.getIdleState().getStateId().getVerilogHDL()));
		HDLUtils.println(dest, offset+4, String.format("%s <= 32'h0;", o.getDelayCounter().getName()));

		HDLUtils.println(dest, offset+2, String.format("end else begin"));

		genSyncSequencerSwitch(o, offset+4);

		HDLUtils.println(dest, offset+2, String.format("end"));
	}

	private void genAsyncSequencerBody(HDLSequencer o, int offset){
		if(o.getStates().size() > 0){
			for(SequencerState s: o.getStates()){
				HDLUtils.println(dest, offset, String.format("// state %s = %s", o.getStateKey().getName(), s.getStateId().getVerilogHDL()));
				if(o.getTransitionTime() > 0) HDLUtils.println(dest, offset, String.format("#%d", o.getTransitionTime()));
				genStateTransition(dest, offset, s);
			}
		}
	}

	@Override
	public void visitHDLSequencer(HDLSequencer o) {
		if(o.getModule().isSynchronous()){
			genSyncSequencerHeader(o, offset);
		}else{
			genAsyncSequencerHeader(o, offset);
		}

		if(o.getModule().isSynchronous()){
			genSyncSequencerBody(o, offset);
		}else{
			genAsyncSequencerBody(o, offset);
		}

		HDLUtils.println(dest, offset, String.format("end"));
		HDLUtils.nl(dest);
	}

	public void genStateTransition(PrintWriter dest, int offset, SequencerState s){
		if(s.getTransitions().size() > 0){
			String sep = "";
			int cnt = 0;
			for(StateTransitCondition c: s.getTransitions()){
				String str = String.format("%s <= %s;", s.getKey().getName(), c.getDestState().getStateId().getVerilogHDL());
				if(c.hasCondition()){
					HDLUtils.println(dest, offset, String.format("%sif (%s) begin", sep, c.getCondExprAsVerilogHDL()));
					HDLUtils.println(dest, offset+2, str);
					sep = "end else ";
					cnt++;
				}else{
					if(cnt == 0){
						HDLUtils.println(dest, offset, str);
					}else{
						HDLUtils.println(dest, offset, "end else begin");
						HDLUtils.println(dest, offset+2, str);
					}
				}
			}
			if(cnt > 0){
				HDLUtils.println(dest, offset, String.format("end"));
			}
		}else{
		}
	}

	private void genSyncProcess(HDLSignal o, int offset){
		if(o.getModule().isNegativeReset()){
			HDLUtils.println(dest, offset, String.format("if(%s == 1'b0) begin", o.getModule().getSysResetName()));
		}else{
			HDLUtils.println(dest, offset, String.format("if(%s == 1'b1) begin", o.getModule().getSysResetName()));
		}
		if(o.getResetValue() != null){
			HDLUtils.println(dest, offset+2, String.format("%s <= %s;", o.getName(), o.getResetValue().getVerilogHDL()));
		}
		HDLUtils.println(dest, offset, String.format("end else begin"));
		if(o.getConditions().length > 0){
			String sep = "";
			for(HDLSignal.AssignmentCondition c: o.getConditions()){
				HDLUtils.println(dest, offset+2, String.format("%sif (%s) begin", sep, c.getCondExprAsVerilogHDL()));
				if(c.getValue() != null){
					HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getName(), c.getValue().getVerilogHDL()));
				}else{
					SynthesijerUtils.warn("Assignment value is not found for " + o.getName());
				}
				sep = "end else ";
			}
			if(o.hasDefaultValue()){
				HDLUtils.println(dest, offset+2, String.format("end else begin"));
				HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getName(), o.getDefaultValue().getVerilogHDL()));
			}
			HDLUtils.println(dest, offset+2, String.format("end"));
		}else{
			if(o.hasDefaultValue()){
				HDLUtils.println(dest, offset+2, String.format("%s <= %s;", o.getName(), o.getDefaultValue().getVerilogHDL()));
			}
		}
		HDLUtils.println(dest, offset, String.format("end"));
	}

	private void genAsyncProcess(HDLSignal o, int offset){
	/*
	  if(o.getConditions().length == 1){
	  HDLUtils.println(dest, offset, String.format("%s <= %s;", o.getName(), o.getConditions()[0].getValue().getVerilogHDL()));
	  }else if(o.getConditions().length > 1){
	*/
		if(o.getConditions().length > 0){
			String sep = "";
			for(HDLSignal.AssignmentCondition c: o.getConditions()){
				HDLUtils.println(dest, offset, String.format("%s if (%s) begin", sep, c.getCondExprAsVerilogHDL()));
				HDLUtils.println(dest, offset+2, String.format("%s <= %s;", o.getName(), c.getValue().getVerilogHDL()));
				sep = "end else";
			}
			HDLUtils.println(dest, offset, String.format("end"));
		}else{
			HDLUtils.println(dest, offset, String.format(""));
		}
	}

	private void genSyncProcessHeader(HDLSignal o){
		HDLUtils.println(dest, offset, String.format("always @(posedge %s) begin", o.getModule().getSysClkName()));
	}

	private void genAsyncProcessHeader(HDLSignal o){
		String s = "";
		String sep = "";
		for(HDLSignal src: o.getDriveSignals()){
			s += sep + src.getName();
			sep = " or ";
		}
		HDLUtils.println(dest, offset, String.format("always @(%s) begin", s));
	}

	private void genSignalRegisterProcess(HDLSignal o){
		if(o.getModule().isSynchronous()){
			genSyncProcessHeader(o);
		}else{
			genAsyncProcessHeader(o);
		}
		if(o.getModule().isSynchronous()){
			genSyncProcess(o, offset+2);
		}else{
			genAsyncProcess(o, offset+2);
		}
		HDLUtils.println(dest, offset, "end");
		HDLUtils.nl(dest);
	}


	@Override
	public void visitHDLSignal(HDLSignal o) {
		if(o.isRegister() && o.isAssignSignalEvent()){
			HDLUtils.println(dest, offset, String.format("always @(posedge %s) begin", o.getAssignSignalEventSignal().getName()));
			HDLUtils.println(dest, offset+2, String.format("%s <= %s;", o.getName(), o.getAssignSignalEventExpr().getResultExpr().getVerilogHDL()));
			HDLUtils.println(dest, offset, "end");
			HDLUtils.nl(dest);
		}else if(o.isRegister() && o.isAssignPortEvent()){
			HDLUtils.println(dest, offset, String.format("always @(posedge %s) begin", o.getAssignPortEventPort().getName()));
			HDLUtils.println(dest, offset+2, String.format("%s <= %s;", o.getName(), o.getAssignPortEventExpr().getResultExpr().getVerilogHDL()));
			HDLUtils.println(dest, offset, "end");
			HDLUtils.nl(dest);
		}else if(o.isRegister()){
			if(o.isIgnore()) return;
			if(o.getConditions().length == 0 && o.hasDefaultValue() == false) return;
			genSignalRegisterProcess(o);
		}else if(o.isAssignAlways()){
			HDLUtils.println(dest, offset, String.format("assign %s = %s;", o.getName(), o.getAssignAlwaysExpr().getResultExpr().getVerilogHDL()));
			HDLUtils.nl(dest);
		}
	}

	@Override
	public void visitHDLType(HDLPrimitiveType o) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitHDLUserDefinedType(HDLUserDefinedType o) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitHDLInstanceRef(HDLInstanceRef o){
	}
}
