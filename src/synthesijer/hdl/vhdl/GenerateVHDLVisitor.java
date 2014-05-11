package synthesijer.hdl.vhdl;

import java.io.PrintWriter;
import java.util.ArrayList;

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

	public static void genPortList(PrintWriter dest, int offset, ArrayList<HDLPort> ports){
		HDLUtils.println(dest, offset, "port (");
		String sep = "";
		for(HDLPort p: ports){
			dest.print(sep);
			p.accept(new GenerateVHDLDefVisitor(dest, offset+2));
			sep = ";\n";
		}
		HDLUtils.println(dest, 0, "");
		HDLUtils.println(dest, offset, ");");
	}
		
	@Override
	public void visitHDLModule(HDLModule o) {
		// library import
		HDLUtils.println(dest, offset, String.format("library IEEE;"));
		HDLUtils.println(dest, offset, String.format("use IEEE.std_logic_1164.all;"));
		HDLUtils.println(dest, offset, String.format("use IEEE.numeric_std.all;"));
		HDLUtils.nl(dest);
		
		// entity
		HDLUtils.println(dest, offset, String.format("entity %s is", o.getName()));
		if(o.getPorts().size() > 0){
			genPortList(dest, offset+2, o.getPorts());
		}
		HDLUtils.println(dest, offset, String.format("end %s;", o.getName()));
		HDLUtils.nl(dest);
		
		// architecture
		HDLUtils.println(dest, offset, String.format("architecture RTL of %s is", o.getName()));
		HDLUtils.nl(dest);
		for(HDLInstance i: o.getModuleInstances()){ i.accept(new GenerateVHDLDefVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		for(HDLPort p: o.getPorts()){
			if(p.isOutput()) p.getSrcSignal().accept(new GenerateVHDLDefVisitor(dest, offset+2));
		}
		HDLUtils.nl(dest);
		for(HDLSignal s: o.getSignals()){ s.accept(new GenerateVHDLDefVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		for(HDLSequencer m: o.getSequencers()){ m.accept(new GenerateVHDLDefVisitor(dest, offset+2)); };
		
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

	private void genSequencerSyncHeader(HDLSequencer o, int offset){
		HDLUtils.println(dest, offset, String.format("process (%s)", o.getModule().getSysClkName()));
	}
	
	private void genSequencerAsyncHeader(HDLSequencer o, int offset){
		HDLUtils.println(dest, offset, String.format("process"));
	}
	
	@Override
	public void visitHDLSequencer(HDLSequencer o) {
		if(o.getModule().isSynchronous()){
			genSequencerSyncHeader(o, offset);
		}else{
			genSequencerAsyncHeader(o, offset);
		}
		HDLUtils.println(dest, offset, String.format("begin"));
		
		int offset1 = offset + 2; 
		if(o.getModule().isSynchronous()){
			HDLUtils.println(dest, offset+2, String.format("if %s'event and %s = '1' then", o.getModule().getSysClkName(), o.getModule().getSysClkName()));
			// reset
			HDLUtils.println(dest, offset+4, String.format("if %s = '1' then", o.getModule().getSysResetName()));
			HDLUtils.println(dest, offset+6, String.format("%s <= %s;", o.getStateKey(), o.getIdleState().getStateId()));
		
			HDLUtils.println(dest, offset+4, String.format("else"));
			offset1 = offset + 6;
		}
		
		// state-machine body
		if(o.getStates().size() > 0){
			HDLUtils.println(dest, offset1, String.format("case (%s) is", o.getStateKey()));
			for(HDLSequencer.SequencerState s: o.getStates()){
				HDLUtils.println(dest, offset1+2, String.format("when %s => ", s.getStateId()));
				if(o.hasTransitionTime()) HDLUtils.println(dest, offset1+4, String.format("wait for %d ns;", o.getTransitionTime()));
				genStateTransition(dest, offset1+4, s);
			}
			HDLUtils.println(dest, offset1+2, String.format("when others => null;"));
			HDLUtils.println(dest, offset1, String.format("end case;"));
		}
		
		if(o.getModule().isSynchronous()){
			HDLUtils.println(dest, offset+4, String.format("end if;"));
			HDLUtils.println(dest, offset+2, String.format("end if;"));
		}
		
		HDLUtils.println(dest, offset, String.format("end process;"));
		HDLUtils.nl(dest);
	}
	
	public void genStateTransition(PrintWriter dest, int offset, HDLSequencer.SequencerState s){
		if(s.getTransitions().size() == 1){
			HDLUtils.println(dest, offset, String.format("%s <= %s;", s.getKey(), s.getTransitions().get(0).getDestState().getStateId()));
		}else if(s.getTransitions().size() > 1){
			String sep = "if";
			for(HDLSequencer.StateTransitCondition c: s.getTransitions()){
				HDLUtils.println(dest, offset, String.format("%s %s then", sep, c.getCondExprAsVHDL()));
				HDLUtils.println(dest, offset+2, String.format("%s <= %s;", s.getKey(), c.getDestState().getStateId()));
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
		HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getName(), o.getResetValue().getVHDL()));
		HDLUtils.println(dest, offset+2, String.format("else"));
		if(o.getConditions().size() == 1){
			HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getName(), o.getConditions().get(0).getValue().getVHDL()));
		}else if(o.getConditions().size() > 1){
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
		if(o.getConditions().size() == 1){
			HDLUtils.println(dest, offset, String.format("%s <= %s;", o.getName(), o.getConditions().get(0).getValue().getVHDL()));
		}else if(o.getConditions().size() > 1){
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
		HDLUtils.println(dest, offset, String.format("process"));
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
