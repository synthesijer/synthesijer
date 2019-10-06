package synthesijer.hdl.vhdl;

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
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.hdl.sequencer.SequencerState;
import synthesijer.hdl.sequencer.StateTransitCondition;

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
		for(HDLInstance.ParamPair pair: o.getParameterPairs()){
			if(pair.getValue() != null){
				HDLUtils.print(dest, 0, sep);
				HDLUtils.print(dest, offset+2, String.format("%s => %s", pair.param.getName(), pair.getValue().getVHDL()));
			}else{
				HDLUtils.print(dest, 0, sep);
				HDLUtils.print(dest, offset+2, String.format("%s => %s", pair.param.getName(), pair.param.getValue().getVHDL()));
			}
			sep = "," + Constant.BR;
		}
		HDLUtils.println(dest, 0, "");
		HDLUtils.println(dest, offset, ")");
	}

	private void genPortMap(HDLInstance o){
		HDLUtils.println(dest, offset, String.format("port map("));
		String sep = "";
		for(HDLInstance.PortPair pair: o.getPairs()){
			HDLUtils.print(dest, 0, sep);
			if(o.getSubModule().getSysClkPairItem() != null && pair.port.getName().equals(o.getSubModule().getSysClkPairItem().getName())){
				// target port to connect is system clock, the port should be connected system clock in this module directly.
				if(o.getModule().getSysClkPairItem() == null){
					SynthesijerUtils.warn(o.getModule().getName() + " does not have system clock, but sub-module requires system clock");
					SynthesijerUtils.warn("system clock of sub-module is remained as open, so the sub-module will not work well.");
				}else{
					HDLUtils.print(dest, offset+2, String.format("%s => %s", pair.port.getName(), o.getModule().getSysClkPairItem().getName()));
				}
			}else if(o.getSubModule().getSysResetPairItem() != null && pair.port.getName().equals(o.getSubModule().getSysResetPairItem().getName())){
				// target port to connect is system reset, the port should be connected system reset in this module directly.
				if(o.getModule().getSysResetPairItem() == null){
					SynthesijerUtils.warn(o.getModule().getName() + " does not have system reset, but sub-module requires system reset");
					SynthesijerUtils.warn("system reset of sub-module is remained as open, so the sub-module will not work well.");
				}else{
					HDLUtils.print(dest, offset+2, String.format("%s => %s", pair.port.getName(), o.getModule().getSysResetPairItem().getName()));
				}
			}else{
				HDLUtils.print(dest, offset+2, String.format("%s => %s", pair.port.getName(), pair.item.getName()));
			}
			sep = "," + Constant.BR;
		}
		HDLUtils.println(dest, 0, "");
		HDLUtils.println(dest, offset, ");");
	}

	@Override
	public void visitHDLInstance(HDLInstance o) {
		HDLUtils.println(dest, offset, String.format("inst_%s : %s", o.getName(), o.getSubModule().getName()));
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

		HDLModule.LibrariesInfo[] libraries = o.getLibraries();
		for(HDLModule.LibrariesInfo lib: libraries){
			HDLUtils.println(dest, offset, String.format("library " + lib.libName + ";"));
			for(String s: lib.useName){
				HDLUtils.println(dest, offset, String.format("use " + s + ";"));
			}
			HDLUtils.nl(dest);
		}

		// entity
		o.accept(new GenerateVHDLDefVisitor(dest, offset));

		// architecture body
		HDLUtils.println(dest, offset, String.format("begin"));
		HDLUtils.nl(dest);
		for(HDLPort p: o.getPorts()){ p.accept(new GenerateVHDLVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		HDLUtils.println(dest, offset+2, "-- expressions");
		for(HDLExpr expr : o.getExprs()){ expr.accept(new GenerateVHDLVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		HDLUtils.println(dest, offset+2, "-- sequencers");
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
		if(o.isSet(HDLPort.OPTION.NO_SIG) || o.isSet(HDLPort.OPTION.EXPORT_PATH)) return; // nothing to do
		if(o.getDir() == HDLPort.DIR.INOUT){
			return;
		}else if(o.isOutput()){
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
			for(SequencerState s: o.getStates()){
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
		for(HDLSequencer.Pair p: o.getSeqExprList()){
			HDLUtils.println(dest, offset, String.format("%s <= %s;", p.dest.getVHDL(), p.expr.getResultExpr().getVHDL()));
		}
		for(HDLSequencer.Triple t: o.getSeqCondExprList()){
			HDLUtils.println(dest, offset, String.format("if (%s) = '1' then", t.cond.getVHDL()));
			HDLUtils.println(dest, offset + 2, String.format("%s <= %s;", t.dest.getVHDL(), t.expr.getResultExpr().getVHDL()));
			HDLUtils.println(dest, offset, String.format("end if;"));
		}
	}

	private void genSyncSequencerBody(HDLSequencer o, int offset){
		HDLUtils.println(dest, offset, String.format("if %s'event and %s = '1' then", o.getModule().getSysClkName(), o.getModule().getSysClkName()));
		// reset
		if(o.getModule().isNegativeReset()){
			HDLUtils.println(dest, offset+2, String.format("if %s = '0' then", o.getModule().getSysResetName()));
		}else{
			HDLUtils.println(dest, offset+2, String.format("if %s = '1' then", o.getModule().getSysResetName()));
		}
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
			for(SequencerState s: o.getStates()){
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

	public void genStateTransition(PrintWriter dest, int offset, SequencerState s){
		if(s.getTransitions().size() > 0){
			String sep = "if";
			int cnt = 0;
			for(StateTransitCondition c: s.getTransitions()){
				String str = String.format("%s <= %s;",
						s.getKey().getName(),
						c.getDestState().getStateId().getVHDL());
				if(c.hasCondition()){
					HDLUtils.println(dest, offset, String.format("%s %s then", sep, c.getCondExprAsVHDL()));
					HDLUtils.println(dest, offset+2, str);
					sep = "elsif";
					cnt++;
				}else{
					if(cnt == 0){
						HDLUtils.println(dest, offset, str);
					}else{
						HDLUtils.println(dest, offset, "else");
						HDLUtils.println(dest, offset+2, str);
					}
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
		if(o.getModule().isNegativeReset()){
			HDLUtils.println(dest, offset+2, String.format("if %s = '0' then", o.getModule().getSysResetName()));
		}else{
			HDLUtils.println(dest, offset+2, String.format("if %s = '1' then", o.getModule().getSysResetName()));
		}
		if(o.getResetValue() != null){
			HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getName(), adjustTypeFor(o, o.getResetValue())));
		}
		HDLUtils.println(dest, offset+2, String.format("else"));
		if(o.getConditions().length > 0){
			String sep = "if";
			for(HDLSignal.AssignmentCondition c: o.getConditions()){
				HDLUtils.println(dest, offset+4, String.format("%s %s then", sep, c.getCondExprAsVHDL()));
				if(c.getValue() != null){
					HDLUtils.println(dest, offset+6, String.format("%s <= %s;", o.getName(), adjustTypeFor(o, c.getValue())));
				}else{
					SynthesijerUtils.warn("Assignment value is not found for " + o.getName());
				}
				sep = "elsif";
			}
			if(o.hasDefaultValue()){
				HDLUtils.println(dest, offset+4, String.format("else"));
				HDLUtils.println(dest, offset+6, String.format("%s <= %s;", o.getName(), adjustTypeFor(o,  o.getDefaultValue())));
			}
			HDLUtils.println(dest, offset+4, String.format("end if;"));
		}else{
			if(o.hasDefaultValue()){
				HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getName(), adjustTypeFor(o,  o.getDefaultValue())));
			}else{
				HDLUtils.println(dest, offset+4, String.format("null;"));
			}
		}
		HDLUtils.println(dest, offset+2, String.format("end if;"));
		HDLUtils.println(dest, offset, String.format("end if;"));
	}

	private void genAsyncProcess(HDLSignal o, int offset){
		/*
		  if(o.getConditions().length == 1){
		  HDLUtils.println(dest, offset, String.format("%s <= %s;", o.getName(), adjustTypeFor(o, o.getConditions()[0].getValue())));
		  }else if(o.getConditions().length > 1){
		*/
		if(o.getConditions().length > 0){
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
		for(HDLSignal src: o.getDriveSignals()){
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
		if(expr instanceof HDLValue && !expr.getType().isDigit()) return expr.getVHDL();
		if(expr instanceof HDLPreDefinedConstant) return expr.getVHDL();
		if(dest.getType().getKind() == expr.getType().getKind()){
			return expr.getVHDL();
		}else{
			String src = expr.getVHDL();
			if(expr instanceof HDLValue &&
					expr.getType().isDigit() &&
					dest.getType() instanceof HDLPrimitiveType &&
					(dest.getType().isVector() || dest.getType().isSigned())){
				src = String.format("to_signed(%s, %d)", src, ((HDLPrimitiveType)dest.getType()).getWidth());
			}
			if(dest.getType().isBit()){
				return String.format("std_logic(%s)", src);
			}else if(dest.getType().isVector()){
				if(dest.getType().getWidth() != expr.getType().getWidth()){
					SynthesijerUtils.warn(String.format("Destination(%s) is %dbit, but source(%s) is %dbit. It is forced for destination.",
							dest.getVHDL(), dest.getType().getWidth(), src, expr.getType().getWidth()));
					if(dest.getType().getWidth() < expr.getType().getWidth()){
						src = String.format("%s(%d-1 downto %d)", src, dest.getType().getWidth(), 0);
					}else if(dest.getType().getWidth() > expr.getType().getWidth()){
						String s = "";
						for(int i = 0; i < dest.getType().getWidth()-expr.getType().getWidth(); i++){
							s += src + "(" + (expr.getType().getWidth()-1) + ")" + " & ";
						}
						src = s + src;
					}
				}
				return String.format("std_logic_vector(%s)", src);
			}else if(dest.getType().isSigned()){

				if(dest.getType().getWidth() != expr.getType().getWidth()){
					SynthesijerUtils.warn(String.format("Destination(%s) is %dbit, but source(%s) is %dbit. It is forced for destination.",
							dest.getVHDL(), dest.getType().getWidth(), src, expr.getType().getWidth()));
					if(dest.getType().getWidth() < expr.getType().getWidth()){
						src = String.format("%s(%d-1 downto %d)", src, dest.getType().getWidth(), 0);
					}else if(dest.getType().getWidth() > expr.getType().getWidth()){
						String s = "";
						for(int i = 0; i < dest.getType().getWidth()-expr.getType().getWidth(); i++){
							s += src + "(" + (expr.getType().getWidth()-1) + ")" + " & ";
						}
						src = s + src;
					}
				}
				return String.format("signed(%s)", src);
			}else{
				SynthesijerUtils.error("cannot assign:" + dest + " <- " + expr);
				throw new RuntimeException("cannot assign:" + dest + " <- " + expr);
			}
		}
	}

	@Override
	public void visitHDLSignal(HDLSignal o) {
		if(o.isRegister() && o.isAssignSignalEvent()){
			HDLUtils.println(dest, offset, String.format("process(%s)", o.getAssignSignalEventSignal().getName()));
			HDLUtils.println(dest, offset, "begin");
			HDLUtils.println(dest, offset+2, String.format("if %s'event and %s = '1' then", o.getAssignSignalEventSignal().getName(), o.getAssignSignalEventSignal().getName()));
			HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getName(), adjustTypeFor(o, o.getAssignSignalEventExpr().getResultExpr())));
			HDLUtils.println(dest, offset+2, "end if;");
			HDLUtils.println(dest, offset, "end process;");
			HDLUtils.nl(dest);
		}else if(o.isRegister() && o.isAssignPortEvent()){
			HDLUtils.println(dest, offset, String.format("process(%s)", o.getAssignPortEventPort().getName()));
			HDLUtils.println(dest, offset, "begin");
			HDLUtils.println(dest, offset+2, String.format("if %s'event and %s = '1' then", o.getAssignPortEventPort().getName(), o.getAssignPortEventPort().getName()));
			HDLUtils.println(dest, offset+4, String.format("%s <= %s;", o.getName(), adjustTypeFor(o, o.getAssignPortEventExpr().getResultExpr())));
			HDLUtils.println(dest, offset+2, "end if;");
			HDLUtils.println(dest, offset, "end process;");
			HDLUtils.nl(dest);
		}else if(o.isRegister() && !o.isAssignAlways()){
			if(o.isIgnore()) return;
			if(o.getConditions().length == 0 && o.hasDefaultValue() == false) return;
			//			if(o.getConditions().length == 0) return;
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

	@Override
	public void visitHDLInstanceRef(HDLInstanceRef o){
	}
}
