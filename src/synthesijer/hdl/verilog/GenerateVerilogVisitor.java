package synthesijer.hdl.verilog;

import java.io.PrintWriter;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLLiteral;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLTreeVisitor;
import synthesijer.hdl.HDLUserDefinedType;
import synthesijer.hdl.HDLUtils;

public class GenerateVerilogVisitor implements HDLTreeVisitor{

	private final PrintWriter dest;
	private final int offset;
	
	public GenerateVerilogVisitor(PrintWriter dest, int offset){
		this.dest = dest;
		this.offset = offset;
	}

	@Override
	public void visitHDLExpr(HDLExpr o) {
		String str = String.format("assign %s = %s;", o.getResultExpr().getVerilogHDL(), o.getVerilogHDL());
		HDLUtils.println(dest, offset, str);
	}

	@Override
	public void visitHDLInstance(HDLInstance o) {
		HDLUtils.println(dest, offset, String.format("%s %s(", o.getSubModule().getName(), o.getName()));
		String sep = "";
		for(HDLInstance.Pair pair: o.getPairs()){
			HDLUtils.print(dest, 0, sep);
			HDLUtils.print(dest, offset+2, String.format(".%s(%s)", pair.port.getName(), pair.signal.getName()));
			sep = ",\n";
		}
		HDLUtils.println(dest, 0, "");
		HDLUtils.println(dest, offset, ");");
		HDLUtils.nl(dest);
	}

	@Override
	public void visitHDLLitral(HDLLiteral o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitHDLModule(HDLModule o) {
		
		HDLUtils.println(dest, offset, String.format("module %s (", o.getName()));
		String sep = "";
		for(HDLPort p: o.getPorts()){
			dest.print(sep);
			p.accept(new GenerateVerilogDefVisitor(dest, offset+2));
			sep = ",\n";
		}
		HDLUtils.println(dest, offset, "\n);");
		HDLUtils.nl(dest);
		
		// definitions
		o.accept(new GenerateVerilogDefVisitor(dest, offset+2));
		
		// body
		for(HDLPort p: o.getPorts()){ p.accept(new GenerateVerilogVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		for(HDLExpr expr : o.getExprs()){ expr.accept(new GenerateVerilogVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		for(HDLSequencer m: o.getSequencers()){ m.accept(new GenerateVerilogVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		for(HDLSignal s: o.getSignals()){ s.accept(new GenerateVerilogVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		for(HDLInstance i: o.getModuleInstances()){ i.accept(new GenerateVerilogVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		HDLUtils.println(dest, offset, "endmodule");
	}

	@Override
	public void visitHDLPort(HDLPort o) {
		if(o.isOutput()){
			HDLUtils.println(dest, offset, String.format("assign %s = %s;", o.getName(), o.getSignal().getName()));
		}else{
			HDLUtils.println(dest, offset, String.format("assign %s = %s;", o.getSignal().getName(), o.getName()));
		}
		o.getSignal().accept(this);
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

			for(HDLSequencer.SequencerState s: o.getStates()){
				HDLUtils.println(dest, offset+2, String.format("%s : begin", s.getStateId().getVerilogHDL()));
				
				if(s.hasDelay()){
					HDLUtils.println(dest, offset+4, String.format("if (%s >= %d) begin", o.getDelayCounter().getName(), s.getConstantDelay()));
					HDLUtils.println(dest, offset+6, String.format("%s <= 32'h0;", o.getDelayCounter().getName()));
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
	}
	
	private void genSyncSequencerBody(HDLSequencer o, int offset){
		// reset
		HDLUtils.println(dest, offset+2, String.format("if(%s == 1'b1) begin", o.getModule().getSysResetName()));
		HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getStateKey().getName(), o.getIdleState().getStateId().getVerilogHDL()));
		
		HDLUtils.println(dest, offset+2, String.format("end else begin"));
		
		genSyncSequencerSwitch(o, offset+4);
		
		HDLUtils.println(dest, offset+2, String.format("end"));
	}
	
	private void genAsyncSequencerBody(HDLSequencer o, int offset){
		if(o.getStates().size() > 0){
			for(HDLSequencer.SequencerState s: o.getStates()){
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
	
	public void genStateTransition(PrintWriter dest, int offset, HDLSequencer.SequencerState s){
		if(s.getTransitions().size() > 0){
			String sep = "";
			int cnt = 0;
			for(HDLSequencer.StateTransitCondition c: s.getTransitions()){
				if(c.hasCondition()){
					HDLUtils.println(dest, offset, String.format("%sif (%s) begin", sep, c.getCondExprAsVerilogHDL()));
					HDLUtils.println(dest, offset+2, String.format("%s <= %s;", s.getKey().getName(), c.getDestState().getStateId().getVerilogHDL()));
					sep = "end else ";
					cnt++;
				}else{
					HDLUtils.println(dest, offset, String.format("%s <= %s;", s.getKey().getName(), c.getDestState().getStateId().getVerilogHDL()));
				}
			}
			if(cnt > 0){
				HDLUtils.println(dest, offset, String.format("end"));
			}
		}else{
		}
	}
	
	private void genSyncProcess(HDLSignal o, int offset){
		HDLUtils.println(dest, offset, String.format("if(%s == 1'b1) begin", o.getModule().getSysResetName()));
		if(o.getResetValue() != null){
			HDLUtils.println(dest, offset+2, String.format("%s <= %s;", o.getName(), o.getResetValue().getVerilogHDL()));
		}
		HDLUtils.println(dest, offset, String.format("end else begin"));
		if(o.getConditions().length > 0){
			String sep = "";
			for(HDLSignal.AssignmentCondition c: o.getConditions()){
				HDLUtils.println(dest, offset+2, String.format("%sif (%s) begin", sep, c.getCondExprAsVerilogHDL()));
				HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getName(), c.getValue().getVerilogHDL()));
				sep = "end else ";
			}
			if(o.hasDefaultValue()){
				HDLUtils.println(dest, offset+2, String.format("end else begin"));
				HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getName(), o.getDefaultValue().getVerilogHDL()));
			}
			HDLUtils.println(dest, offset+2, String.format("end"));
		}
		HDLUtils.println(dest, offset, String.format("end"));
	}
	
	private void genAsyncProcess(HDLSignal o, int offset){
		if(o.getConditions().length == 1){
			HDLUtils.println(dest, offset, String.format("%s <= %s;", o.getName(), o.getConditions()[0].getValue().getVerilogHDL()));
		}else if(o.getConditions().length > 1){
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
		for(HDLSignal src: o.getSrcSignals()){
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
		if(o.isRegister()){
			if(o.getConditions().length == 0) return;
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

}
