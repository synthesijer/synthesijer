package synthesijer.hdl.vhdl;

import java.io.PrintWriter;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLIdent;
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

public class GenerateVHDLVisitor implements HDLTreeVisitor{
	
	private final PrintWriter dest;
	private final int offset;
	
	public GenerateVHDLVisitor(PrintWriter dest, int offset){
		this.dest = dest;
		this.offset = offset;
	}

	@Override
	public void visitHDLExpr(HDLExpr o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitHDLIdent(HDLIdent o) {
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
		HDLUtils.println(dest, offset, String.format("entity %s is", o.getName()));
		HDLUtils.println(dest, offset+2, "port (");
		String sep = "";
		for(HDLPort p: o.getPorts()){
			dest.print(sep);
			p.accept(new GenerateVHDLDefVisitor(dest, offset+4));
			sep = ";\n";
		}
		HDLUtils.println(dest, offset, "\n  );");
		HDLUtils.println(dest, offset, String.format("end %s;", o.getName()));
		HDLUtils.nl(dest);
		HDLUtils.println(dest, offset, String.format("architecture RTL of %s is", o.getName()));
		for(HDLSignal s: o.getSignals()){ s.accept(new GenerateVHDLDefVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		for(HDLSequencer m: o.getSequencers()){ m.accept(new GenerateVHDLDefVisitor(dest, offset+2)); };
		HDLUtils.println(dest, offset, String.format("begin"));
		HDLUtils.nl(dest);
		for(HDLPort p: o.getPorts()){ p.accept(new GenerateVHDLVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		for(HDLSequencer m: o.getSequencers()){ m.accept(new GenerateVHDLVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		for(HDLSignal s: o.getSignals()){ s.accept(new GenerateVHDLVisitor(dest, offset+2)); }
		HDLUtils.println(dest, offset, String.format("end RTL;"));
	}

	@Override
	public void visitHDLPort(HDLPort o) {
		if(o.isOutput()){
			HDLUtils.println(dest, offset, String.format("%s <= %s;", o.getName(), o.getSrcSignal().getName()));
		}
	}

	@Override
	public void visitHDLSequencer(HDLSequencer o) {
		HDLUtils.println(dest, offset, String.format("process (%s)", o.getModule().getSysClkName()));
		HDLUtils.println(dest, offset, String.format("begin"));
		HDLUtils.println(dest, offset+2, String.format("if %s'event and %s = '1' then", o.getModule().getSysClkName(), o.getModule().getSysClkName()));
		
		// reset
		HDLUtils.println(dest, offset+4, String.format("if %s = '1' then", o.getModule().getSysResetName()));
		HDLUtils.println(dest, offset+6, String.format("%s <= %s;", o.getStateKey(), o.getIdleState().getStateId()));
		
		HDLUtils.println(dest, offset+4, String.format("else"));
		
		// state-machine body
		if(o.getStates().size() > 0){
			HDLUtils.println(dest, offset+6, String.format("case (%s) is", o.getStateKey()));
			for(HDLSequencer.SequencerState s: o.getStates()){
				HDLUtils.println(dest, offset+8, String.format("when %s => ", s.getStateId()));
				generateStateTransition(dest, offset+10, s);
			}
			HDLUtils.println(dest, offset+8, String.format("when others => null;"));
			HDLUtils.println(dest, offset+6, String.format("end case;"));
		}
		
		HDLUtils.println(dest, offset+4, String.format("end if;"));
		HDLUtils.println(dest, offset+2, String.format("end if;"));
		HDLUtils.println(dest, offset, String.format("end process;"));
		HDLUtils.nl(dest);
	}
	
	public void generateStateTransition(PrintWriter dest, int offset, HDLSequencer.SequencerState s){
		if(s.getTransitions().size() > 0){
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


	@Override
	public void visitHDLSignal(HDLSignal o) {
		HDLUtils.println(dest, offset, String.format("process(%s)", o.getModule().getSysClkName()));
		HDLUtils.println(dest, offset, "begin");
		HDLUtils.println(dest, offset+2, String.format("if %s'event and %s = '1' then", o.getModule().getSysClkName(), o.getModule().getSysClkName()));
		HDLUtils.println(dest, offset+4, String.format("if %s = '1' then", o.getModule().getSysResetName()));
		HDLUtils.println(dest, offset+6, String.format("%s <= %s;", o.getName(), o.getResetValue().getVHDL()));
		HDLUtils.println(dest, offset+4, String.format("else"));
		if(o.getConditions().size() > 0){
			String sep = "if";
			for(HDLSignal.AssignmentCondition c: o.getConditions()){
				HDLUtils.println(dest, offset+6, String.format("%s %s then", sep, c.getCondExprAsVHDL()));
				HDLUtils.println(dest, offset+8, String.format("%s <= %s;", o.getName(), c.getValue().getVHDL()));
				sep = "elsif";
			}
			HDLUtils.println(dest, offset+6, String.format("end if;"));
		}else{
			HDLUtils.println(dest, offset+6, String.format("null;"));
		}
		HDLUtils.println(dest, offset+4, String.format("end if;"));
		HDLUtils.println(dest, offset+2, String.format("end if;"));
		HDLUtils.println(dest, offset, "end process;");
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
