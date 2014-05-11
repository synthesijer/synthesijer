package synthesijer.hdl.vhdl;

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

public class GenerateVHDLVisitor implements HDLTreeVisitor{
	
	private final PrintWriter dest;
	private final int offset;
	
	public GenerateVHDLVisitor(PrintWriter dest, int offset){
		this.dest = dest;
		this.offset = offset;
	}

	@Override
	public void visitHDLExpr(HDLExpr o) {
		String str = String.format("%s <= %s;", o.getResultExpr().getVHDL(), o.getVHDL());
		HDLUtils.println(dest, offset, str);
	}

	@Override
	public void visitHDLInstance(HDLInstance o) {
		HDLUtils.println(dest, offset, String.format("%s : %s port map(", o.getName(), o.getSubModule().getName()));
		String sep = "";
		for(HDLInstance.Pair pair: o.getPairs()){
			HDLUtils.print(dest, 0, sep);
			HDLUtils.print(dest, offset+2, String.format("%s => %s", pair.port.getName(), pair.signal.getName()));
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
		// library import
		HDLUtils.println(dest, offset, String.format("library IEEE;"));
		HDLUtils.println(dest, offset, String.format("use IEEE.std_logic_1164.all;"));
		HDLUtils.println(dest, offset, String.format("use IEEE.numeric_std.all;"));
		HDLUtils.nl(dest);
		
		// entity
		o.accept(new GenerateVHDLDefVisitor(dest, offset));
		
		// architecture body
		HDLUtils.println(dest, offset, String.format("begin"));
		HDLUtils.nl(dest);
		for(HDLPort p: o.getPorts()){ p.accept(new GenerateVHDLVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		for(HDLExpr expr : o.getExprs()){ expr.accept(new GenerateVHDLVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		for(HDLSequencer m: o.getSequencers()){ m.accept(new GenerateVHDLVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		for(HDLSignal s: o.getSignals()){ s.accept(new GenerateVHDLVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		for(HDLInstance i: o.getModuleInstances()){ i.accept(new GenerateVHDLVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		HDLUtils.println(dest, offset, String.format("end RTL;"));
	}

	@Override
	public void visitHDLPort(HDLPort o) {
		if(o.isOutput()){
			HDLUtils.println(dest, offset, String.format("%s <= %s;", o.getName(), o.getSrcSignal().getName()));
			o.getSrcSignal().accept(this);
		}
	}

	private void genSyncSequencerHeader(HDLSequencer o, int offset){
		HDLUtils.println(dest, offset, String.format("process (%s)", o.getModule().getSysClkName()));
	}
	
	private void genAsyncSequencerHeader(HDLSequencer o, int offset){
		HDLUtils.println(dest, offset, String.format("process"));
	}
	
	private void genSyncSequencerBody(HDLSequencer o, int offset){
		HDLUtils.println(dest, offset, String.format("if %s'event and %s = '1' then", o.getModule().getSysClkName(), o.getModule().getSysClkName()));
		// reset
		HDLUtils.println(dest, offset+2, String.format("if %s = '1' then", o.getModule().getSysResetName()));
		HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getStateKey().getName(), o.getIdleState().getStateId()));
		
		HDLUtils.println(dest, offset+2, String.format("else"));
		
		// state-machine body
		if(o.getStates().size() > 0){
			HDLUtils.println(dest, offset+4, String.format("case (%s) is", o.getStateKey().getName()));
			for(HDLSequencer.SequencerState s: o.getStates()){
				HDLUtils.println(dest, offset+6, String.format("when %s => ", s.getStateId()));
				genStateTransition(dest, offset+8, s);
			}
			HDLUtils.println(dest, offset+6, String.format("when others => null;"));
			HDLUtils.println(dest, offset+4, String.format("end case;"));
		}
		
		if(o.getModule().isSynchronous()){
			HDLUtils.println(dest, offset+2, String.format("end if;"));
			HDLUtils.println(dest, offset, String.format("end if;"));
		}
	}
	
	private void genAsyncSequencerBody(HDLSequencer o, int offset){
		if(o.getStates().size() > 0){
			for(HDLSequencer.SequencerState s: o.getStates()){
				HDLUtils.println(dest, offset, String.format("-- state %s = %s", o.getStateKey().getName(), s.getStateId()));
				genStateTransition(dest, offset, s);
				if(o.hasTransitionTime()) HDLUtils.println(dest, offset, String.format("wait for %d ns;", o.getTransitionTime()));
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
		HDLUtils.println(dest, offset, String.format("begin"));
		if(o.getModule().isSynchronous()){
			genSyncSequencerBody(o, offset+2);
		}else{
			genAsyncSequencerBody(o, offset+2);
		}
		
		
		HDLUtils.println(dest, offset, String.format("end process;"));
		HDLUtils.nl(dest);
	}
	
	public void genStateTransition(PrintWriter dest, int offset, HDLSequencer.SequencerState s){
		if(s.getTransitions().size() == 1){
			HDLUtils.println(dest, offset, String.format("%s <= %s;", s.getKey().getName(), s.getTransitions().get(0).getDestState().getStateId()));
		}else if(s.getTransitions().size() > 1){
			String sep = "if";
			for(HDLSequencer.StateTransitCondition c: s.getTransitions()){
				HDLUtils.println(dest, offset, String.format("%s %s then", sep, c.getCondExprAsVHDL()));
				HDLUtils.println(dest, offset+2, String.format("%s <= %s;", s.getKey().getName(), c.getDestState().getStateId()));
				sep = "elsif";
			}
			HDLUtils.println(dest, offset, String.format("end if;"));
		}else{
			HDLUtils.println(dest, offset, "null;");
		}
	}

	private void genSyncProcess(HDLSignal o, int offset){
		HDLUtils.println(dest, offset, String.format("if %s'event and %s = '1' then", o.getModule().getSysClkName(), o.getModule().getSysClkName()));
		HDLUtils.println(dest, offset+2, String.format("if %s = '1' then", o.getModule().getSysResetName()));
		if(o.getResetValue() != null){
			HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getName(), o.getResetValue().getVHDL()));
		}
		HDLUtils.println(dest, offset+2, String.format("else"));
		if(o.getConditions().length == 1){
			HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getName(), o.getConditions()[0].getValue().getVHDL()));
		}else if(o.getConditions().length > 1){
			String sep = "if";
			for(HDLSignal.AssignmentCondition c: o.getConditions()){
				HDLUtils.println(dest, offset+4, String.format("%s %s then", sep, c.getCondExprAsVHDL()));
				HDLUtils.println(dest, offset+6, String.format("%s <= %s;", o.getName(), c.getValue().getVHDL()));
				sep = "elsif";
			}
			HDLUtils.println(dest, offset+4, String.format("end if;"));
		}else{
			HDLUtils.println(dest, offset+4, String.format("null;"));
		}
		HDLUtils.println(dest, offset+2, String.format("end if;"));
		HDLUtils.println(dest, offset, String.format("end if;"));
	}

	private void genAsyncProcess(HDLSignal o, int offset){
		if(o.getConditions().length == 1){
			HDLUtils.println(dest, offset, String.format("%s <= %s;", o.getName(), o.getConditions()[0].getValue().getVHDL()));
		}else if(o.getConditions().length > 1){
			String sep = "if";
			for(HDLSignal.AssignmentCondition c: o.getConditions()){
				HDLUtils.println(dest, offset, String.format("%s %s then", sep, c.getCondExprAsVHDL()));
				HDLUtils.println(dest, offset+2, String.format("%s <= %s;", o.getName(), c.getValue().getVHDL()));
				sep = "elsif";
			}
			HDLUtils.println(dest, offset, String.format("end if;"));
		}else{
			HDLUtils.println(dest, offset, String.format("null;"));
		}
	}

	private void genSyncProcessHeader(HDLSignal o){
		HDLUtils.println(dest, offset, String.format("process(%s)", o.getModule().getSysClkName()));
	}
	
	private void genAsyncProcessHeader(HDLSignal o){
		String s = "";
		String sep = "";
		for(HDLSignal src: o.getSrcSignals()){
			s += sep + src.getName();
			sep = ", ";
		}
		HDLUtils.println(dest, offset, String.format("process(%s)", s));
	}
	
	private void genSignalRegisterProcess(HDLSignal o){
		if(o.getModule().isSynchronous()){
			genSyncProcessHeader(o);
		}else{
			genAsyncProcessHeader(o);
		}
		HDLUtils.println(dest, offset, "begin");
		if(o.getModule().isSynchronous()){
			genSyncProcess(o, offset+2);
		}else{
			genAsyncProcess(o, offset+2);
		}
		HDLUtils.println(dest, offset, "end process;");
		HDLUtils.nl(dest);
	}
		
	@Override
	public void visitHDLSignal(HDLSignal o) {
		if(o.isRegister() && !o.isAssignAlways()){
			if(o.getConditions().length == 0) return;
			genSignalRegisterProcess(o);
		}else if(o.isAssignAlways()){
			HDLUtils.println(dest, offset, String.format("%s <= %s;", o.getName(), o.getAssignAlwaysExpr().getResultExpr().getVHDL()));
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
