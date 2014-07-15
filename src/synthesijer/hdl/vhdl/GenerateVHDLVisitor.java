package synthesijer.hdl.vhdl;

import java.io.PrintWriter;

import synthesijer.SynthesijerUtils;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLInstance.ParamPair;
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
import synthesijer.hdl.expr.HDLValue;

public class GenerateVHDLVisitor implements HDLTreeVisitor{
	
	private final PrintWriter dest;
	private final int offset;
	
	public GenerateVHDLVisitor(PrintWriter dest, int offset){
		this.dest = dest;
		this.offset = offset;
	}

	@Override
	public void visitHDLExpr(HDLExpr o) {
		String str = String.format("%s <= %s;", o.getResultExpr().getVHDL(), adjustTypeFor((HDLSignal)o.getResultExpr(), o));
		HDLUtils.println(dest, offset, str);
	}

	private void genGenericMap(HDLInstance o){
		HDLUtils.println(dest, offset, String.format("generic map("));
		String sep = "";
		for(ParamPair pair: o.getParameterPairs()){
			if(pair.getValue() != null){
				HDLUtils.print(dest, 0, sep);
				HDLUtils.print(dest, offset+2, String.format("%s => %s", pair.param.getName(), pair.getValue()));
			}else{
				HDLUtils.print(dest, 0, sep);
				HDLUtils.print(dest, offset+2, String.format("%s => %s", pair.param.getName(), pair.param.getValue()));
			}
			sep = ",\n";
		}
		HDLUtils.println(dest, 0, "");
		HDLUtils.println(dest, offset, ")");
	}

	private void genPortMap(HDLInstance o){
		HDLUtils.println(dest, offset, String.format("port map("));
		String sep = "";
		for(HDLInstance.PortPair pair: o.getPairs()){
			HDLUtils.print(dest, 0, sep);
			HDLUtils.print(dest, offset+2, String.format("%s => %s", pair.port.getName(), pair.item.getName()));
			sep = ",\n";
		}
		HDLUtils.println(dest, 0, "");
		HDLUtils.println(dest, offset, ");");
	}

	@Override
	public void visitHDLInstance(HDLInstance o) {
		HDLUtils.println(dest, offset, String.format("%s : %s", o.getName(), o.getSubModule().getName()));
		if(o.getSubModule().getParameters().length > 0){
			genGenericMap(o);
		}
		genPortMap(o);
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
		if(o.isSet(HDLPort.OPTION.NO_SIG)) return; // nothing to do
		if(o.isOutput()){
			HDLUtils.println(dest, offset, String.format("%s <= %s;", o.getName(), o.getSignal().getName()));
		}else{
			HDLUtils.println(dest, offset, String.format("%s <= %s;", o.getSignal().getName(), o.getName()));
		}
		o.getSignal().accept(this);
	}

	@Override
	public void visitHDLParameter(HDLParameter o) {
		// nothing to do
	}

	private void genSyncSequencerHeader(HDLSequencer o, int offset){
		HDLUtils.println(dest, offset, String.format("process (%s)", o.getModule().getSysClkName()));
	}
	
	private void genAsyncSequencerHeader(HDLSequencer o, int offset){
		HDLUtils.println(dest, offset, String.format("process"));
	}
	
	private void genSyncSequencerSwitch(HDLSequencer o, int offset){
		if(o.getStates().size() > 0){
			HDLUtils.println(dest, offset, String.format("case (%s) is", o.getStateKey().getName()));
			for(HDLSequencer.SequencerState s: o.getStates()){
				HDLUtils.println(dest, offset+2, String.format("when %s => ", s.getStateId().getVHDL()));
				if(s.hasExitCondition()){
					HDLUtils.println(dest, offset+4, String.format("if %s then", s.getExitConditionAsVHDL()));
					HDLUtils.println(dest, offset+6, String.format("%s <= (others => '0');", o.getDelayCounter().getName()));
					genStateTransition(dest, offset+6, s);
					HDLUtils.println(dest, offset+4, String.format("else"));
					HDLUtils.println(dest, offset+6, String.format("%s <= %s + 1;", o.getDelayCounter().getName(), o.getDelayCounter().getName()));
					HDLUtils.println(dest, offset+4, String.format("end if;"));
				}else{
					genStateTransition(dest, offset+4, s);
				}
			}
			HDLUtils.println(dest, offset+2, String.format("when others => null;"));
			HDLUtils.println(dest, offset, String.format("end case;"));
		}
	}
	
	private void genSyncSequencerBody(HDLSequencer o, int offset){
		HDLUtils.println(dest, offset, String.format("if %s'event and %s = '1' then", o.getModule().getSysClkName(), o.getModule().getSysClkName()));
		// reset
 		HDLUtils.println(dest, offset+2, String.format("if %s = '1' then", o.getModule().getSysResetName()));
 		HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getStateKey().getName(), o.getIdleState().getStateId().getVHDL()));
 		HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getDelayCounter().getName(), o.getDelayCounter().getDefaultValue().getVHDL()));
 		
		HDLUtils.println(dest, offset+2, String.format("else"));
		
		genSyncSequencerSwitch(o, offset+4);
		
		if(o.getModule().isSynchronous()){
			HDLUtils.println(dest, offset+2, String.format("end if;"));
			HDLUtils.println(dest, offset, String.format("end if;"));
		}
	}
	
	private void genAsyncSequencerBody(HDLSequencer o, int offset){
		if(o.getStates().size() > 0){
			for(HDLSequencer.SequencerState s: o.getStates()){
				HDLUtils.println(dest, offset, String.format("-- state %s = %s", o.getStateKey().getName(), s.getStateId().getVHDL()));
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
		if(s.getTransitions().size() > 0){
			String sep = "if";
			int cnt = 0;
			for(HDLSequencer.StateTransitCondition c: s.getTransitions()){
				if(c.hasCondition()){
					HDLUtils.println(dest, offset, String.format("%s %s then", sep, c.getCondExprAsVHDL()));
					HDLUtils.println(dest, offset+2, String.format("%s <= %s;", s.getKey().getName(), c.getDestState().getStateId().getVHDL()));
					sep = "elsif";
					cnt++;
				}else{
					HDLUtils.println(dest, offset, String.format("%s <= %s;", s.getKey().getName(), c.getDestState().getStateId().getVHDL()));
				}
			}
			if(cnt > 0){
				HDLUtils.println(dest, offset, String.format("end if;"));
			}
		}else{
			HDLUtils.println(dest, offset, "null;");
		}
	}

	private void genSyncProcess(HDLSignal o, int offset){
		HDLUtils.println(dest, offset, String.format("if %s'event and %s = '1' then", o.getModule().getSysClkName(), o.getModule().getSysClkName()));
		HDLUtils.println(dest, offset+2, String.format("if %s = '1' then", o.getModule().getSysResetName()));
		if(o.getResetValue() != null){
			HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getName(), adjustTypeFor(o, o.getResetValue())));
		}
		HDLUtils.println(dest, offset+2, String.format("else"));
		if(o.getConditions().length > 0){
			String sep = "if";
			for(HDLSignal.AssignmentCondition c: o.getConditions()){
				HDLUtils.println(dest, offset+4, String.format("%s %s then", sep, c.getCondExprAsVHDL()));
				HDLUtils.println(dest, offset+6, String.format("%s <= %s;", o.getName(), adjustTypeFor(o, c.getValue())));
				sep = "elsif";
			}
			if(o.hasDefaultValue()){
				HDLUtils.println(dest, offset+4, String.format("else"));
				HDLUtils.println(dest, offset+6, String.format("%s <= %s;", o.getName(), adjustTypeFor(o,  o.getDefaultValue())));
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
			HDLUtils.println(dest, offset, String.format("%s <= %s;", o.getName(), adjustTypeFor(o, o.getConditions()[0].getValue())));
		}else if(o.getConditions().length > 1){
			String sep = "if";
			for(HDLSignal.AssignmentCondition c: o.getConditions()){
				HDLUtils.println(dest, offset, String.format("%s %s then", sep, c.getCondExprAsVHDL()));
				HDLUtils.println(dest, offset+2, String.format("%s <= %s;", o.getName(), adjustTypeFor(o, c.getValue())));
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
	
	private String adjustTypeFor(HDLSignal dest, HDLExpr expr){
		if(expr instanceof HDLValue) return expr.getVHDL();
		if(dest.getType().getKind() == expr.getType().getKind()){
			return expr.getVHDL();
		}else{
			if(dest.getType().isBit()){
				return String.format("std_logic(%s)", expr.getVHDL());
			}else if(dest.getType().isVector()){
				return String.format("std_logic_vector(%s)", expr.getVHDL());
			}else if(dest.getType().isSigned()){
				return String.format("signed(%s)", expr.getVHDL());
			}else{
				SynthesijerUtils.error("cannot assign:" + dest + " <- " + expr);
				throw new RuntimeException("cannot assign:" + dest + " <- " + expr);
			}
		}
	}
	
	@Override
	public void visitHDLSignal(HDLSignal o) {
		if(o.isRegister() && !o.isAssignAlways()){
			if(o.getConditions().length == 0) return;
			genSignalRegisterProcess(o);
		}else if(o.isAssignAlways()){
			HDLUtils.println(dest, offset, String.format("%s <= %s;", o.getName(), adjustTypeFor(o, o.getAssignAlwaysExpr().getResultExpr())));
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
