package synthesijer.hdl.verilog;

import java.io.PrintWriter;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLLiteral;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLTreeVisitor;
import synthesijer.hdl.HDLType;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitHDLInstance(HDLInstance o) {
		// TODO Auto-generated method stub
		
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
		for(HDLSignal s: o.getSignals()){ s.accept(new GenerateVerilogDefVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		for(HDLSequencer m: o.getSequencers()){ m.accept(new GenerateVerilogDefVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		// body
		for(HDLPort p: o.getPorts()){ p.accept(new GenerateVerilogVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		for(HDLSequencer m: o.getSequencers()){ m.accept(new GenerateVerilogVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		for(HDLSignal s: o.getSignals()){ s.accept(new GenerateVerilogVisitor(dest, offset+2)); }
		HDLUtils.println(dest, offset, "endmodule");
	}

	@Override
	public void visitHDLPort(HDLPort o) {
		if(o.isOutput()){
			HDLUtils.println(dest, offset, String.format("assign %s = %s;", o.getName(), o.getSrcSignal().getName()));
		}
	}

	@Override
	public void visitHDLSequencer(HDLSequencer o) {
		HDLUtils.println(dest, offset, String.format("always @(posedge %s) begin", o.getModule().getSysClkName()));
		
		// reset
		HDLUtils.println(dest, offset+2, String.format("if(%s == 1'b1) begin", o.getModule().getSysResetName()));
		HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getStateKey(), o.getIdleState().getStateId()));
		
		HDLUtils.println(dest, offset+2, String.format("end else begin"));
		
		// state-machine body
		if(o.getStates().size() > 0){
			HDLUtils.println(dest, offset+4, String.format("case (%s)", o.getStateKey()));

			for(HDLSequencer.SequencerState s: o.getStates()){
				HDLUtils.println(dest, offset+6, String.format("%s : begin", s.getStateId()));
				generateStateTransition(dest, offset+8, s);
				HDLUtils.println(dest, offset+6, String.format("end"));
			}
			HDLUtils.println(dest, offset+4, String.format("endcase"));
		}
		
		HDLUtils.println(dest, offset+2, String.format("end"));
		HDLUtils.println(dest, offset, String.format("end"));
		HDLUtils.nl(dest);
	}
	
	public void generateStateTransition(PrintWriter dest, int offset, HDLSequencer.SequencerState s){
		if(s.getTransitions().size() > 0){
			String sep = "";
			for(HDLSequencer.StateTransitCondition c: s.getTransitions()){
				HDLUtils.println(dest, offset, String.format("%sif (%s) begin", sep, c.getCondExprAsVerilogHDL()));
				HDLUtils.println(dest, offset+2, String.format("%s <= %s;", s.getKey(), c.getDestState().getStateId()));
				sep = "end else ";
			}
			HDLUtils.println(dest, offset, String.format("end"));
		}else{
		}
	}


	@Override
	public void visitHDLSignal(HDLSignal o) {
		HDLUtils.println(dest, offset, String.format("always @(posedge %s) begin", o.getModule().getSysClkName()));
		HDLUtils.println(dest, offset+2, String.format("if(%s == 1'b1) begin", o.getModule().getSysResetName()));
		HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getName(), o.getResetValue().getVerilogHDL()));
		HDLUtils.println(dest, offset+2, String.format("end else begin"));
		if(o.getConditions().size() > 0){
			String sep = "";
			for(HDLSignal.AssignmentCondition c: o.getConditions()){
				HDLUtils.println(dest, offset+4, String.format("%s if (%s) begin", sep, c.getCondExprAsVerilogHDL()));
				HDLUtils.println(dest, offset+6, String.format("%s <= %s;", o.getName(), c.getValue().getVerilogHDL()));
				sep = "end else";
			}
			HDLUtils.println(dest, offset+4, String.format("end"));
		}
		HDLUtils.println(dest, offset+2, String.format("end"));
		HDLUtils.println(dest, offset, "end");
		HDLUtils.nl(dest);
	}

	@Override
	public void visitHDLType(HDLType o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitHDLUserDefinedType(HDLUserDefinedType o) {
		// TODO Auto-generated method stub
		
	}

}
