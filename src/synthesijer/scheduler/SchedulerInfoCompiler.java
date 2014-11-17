package synthesijer.scheduler;

import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.CompileState;
import synthesijer.Manager;
import synthesijer.SynthesijerUtils;
import synthesijer.ast.Method;
import synthesijer.ast.Type;
import synthesijer.ast.type.ArrayRef;
import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.ComponentType;
import synthesijer.ast.type.MySelfType;
import synthesijer.ast.type.PrimitiveTypeKind;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLType;
import synthesijer.hdl.HDLVariable;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.hdl.sequencer.SequencerState;

public class SchedulerInfoCompiler {
	
	private SchedulerInfo info;
	private HDLModule hm;
	
	public SchedulerInfoCompiler(SchedulerInfo info, HDLModule hm){
		this.info = info;
		this.hm = hm;
	}
	
	public void compile(){
		System.out.println("Compile: " + info.getName());
		genDeclarations();
		genStatemachines();
	}
	
	private Hashtable<String, HDLVariable> varTable = new Hashtable<>();

	private void genDeclarations(){
		for(Hashtable<String, VariableOperand> t: info.getVarTableList()){
			for(VariableOperand v: t.values()){
				HDLVariable var = genHDLVariable(v);
				if(var != null) varTable.put(v.getName(), var);
			}
		}
	}
	
	private HDLVariable genHDLVariable(String name, ArrayType t){
		Manager.HDLModuleInfo info = null;
		Type t0 = t.getElemType();
		if(t0 instanceof PrimitiveTypeKind == false){
			throw new RuntimeException("unsupported type: " + t);
		}
		switch((PrimitiveTypeKind)t0){
		case BOOLEAN: info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM1");  break;
		case BYTE:    info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM8");  break;
		case SHORT:   info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM16"); break;
		case INT:     info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM32"); break;
		case LONG:    info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM64"); break;
		case FLOAT:   info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM32"); break;
		case DOUBLE:  info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM64"); break;
		default: throw new RuntimeException("unsupported type: " + t);
		}
		HDLInstance inst = hm.newModuleInstance(info.hm, name);
		inst.getSignalForPort("clk").setAssign(null, hm.getSysClk().getSignal());
		inst.getSignalForPort("reset").setAssign(null, hm.getSysReset().getSignal());
		return inst;
	}
	
	private class Pair{
		public final HDLPort port;
		public final HDLSignal sig;
		public Pair(HDLPort port, HDLSignal sig){
			this.port = port;
			this.sig = sig;
		}
	}
	
	private Hashtable<String, ArrayList<Pair>> paramListMap = new Hashtable<>();
	
	private ArrayList<Pair> getMethodParamPairList(String methodName){
		ArrayList<Pair> list;
		if(paramListMap.containsKey(methodName) == false){
			list = new ArrayList<>();
			paramListMap.put(methodName, list);
		}else{
			list = paramListMap.get(methodName);
		}
		return list;
	}
	
	private HDLVariable genHDLVariable(VariableOperand v){
		String name = v.getName();
		Type type = v.getType();
		if(type instanceof PrimitiveTypeKind){
			if(type == PrimitiveTypeKind.DECLARED){
				SynthesijerUtils.warn("Declaration is skipped: " + name + "::" + type);
				return null;
			}
			HDLSignal sig = hm.newSignal(name, getHDLType(type));
			if(v.getVariable() != null && v.getVariable().isMethodParam()){
				String prefix = v.getVariable().getMethod().getName();
				String n = prefix + "_" + v.getVariable().getName();
				HDLPort port = hm.newPort(n, HDLPort.DIR.IN, getHDLType(type));
				getMethodParamPairList(prefix).add(new Pair(port, sig));
			}
			return sig;
		}else if(type instanceof ArrayType){
			return genHDLVariable(name, (ArrayType)type);
		}else if(type instanceof ComponentType){
			ComponentType c = (ComponentType)type;
			Manager.HDLModuleInfo info = Manager.INSTANCE.searchHDLModuleInfo(c.getName());
			if(info == null){
				SynthesijerUtils.error(c.getName() + " is not found.");
				Manager.INSTANCE.HDLModuleInfoList();
				System.exit(0);
			}
			if(info.getCompileState().isBefore(CompileState.GENERATE_HDL)){
				SynthesijerUtils.info("enters into >>>");
				Manager.INSTANCE.compileSchedulerInfo(c.getName(), info);
				SynthesijerUtils.info("<<< return to compiling " + this.info.getName());
			}
			HDLInstance inst = hm.newModuleInstance(info.hm, name);
			inst.getSignalForPort(inst.getSubModule().getSysClkName()).setAssign(null, hm.getSysClk().getSignal());
			inst.getSignalForPort(inst.getSubModule().getSysResetName()).setAssign(null, hm.getSysReset().getSignal());
			return inst;
		}else if(type instanceof ArrayRef){
			Type t = ((ArrayRef) type).getRefType().getElemType();
			HDLSignal sig = hm.newSignal(name, getHDLType(t));
			return sig;
		}else{
			throw new RuntimeException("unsupported type: " + type + " of " + name);
		}
	}

	private HDLType getHDLType(Type type){
		if(type instanceof PrimitiveTypeKind){
			return getHDLType((PrimitiveTypeKind)type);
		}else if(type instanceof ArrayType){
			return getHDLType((ArrayType)type);
		}else if(type instanceof ComponentType){
			return getHDLType((ComponentType)type);
		}else if(type instanceof MySelfType){
			return getHDLType((MySelfType)type);
		}else{
			return null;
		}
	}

	private HDLPrimitiveType getHDLType(PrimitiveTypeKind t){
		switch(t){
		case BOOLEAN: return HDLPrimitiveType.genBitType(); 
		case BYTE: return HDLPrimitiveType.genSignedType(8); 
		case CHAR: return HDLPrimitiveType.genVectorType(16);
		case SHORT: return HDLPrimitiveType.genSignedType(16);
		case INT: return HDLPrimitiveType.genSignedType(32);
		case LONG: return HDLPrimitiveType.genSignedType(64);
		case FLOAT: return HDLPrimitiveType.genVectorType(32);
		case DOUBLE: return HDLPrimitiveType.genVectorType(64);
		default: return null; // return HDLPrimitiveType.genUnknowType();
		}
	}


	private HDLPrimitiveType getHDLType(MySelfType t){
		System.err.println("unsupported type: " + t);
		return null;
	}
	
	private HDLPrimitiveType getHDLType(ComponentType t){
		System.err.println("unsupported type: " + t.getName() + "::ComponentType");
		return null;
	}
	
	private HDLPrimitiveType getHDLType(ArrayType t){
		System.err.println("unsupported type: " + t);
		return null;
	}
	
	private void genStatemachines(){
		for(SchedulerBoard board: info.getBoardsList()){
			SequencerState[] states = genStatemachine(board);
			genExprs(board, states);
		}
	}
		
	private HDLExpr convOperandToHDLExpr(Operand o){
		HDLExpr ret;
		if(o instanceof VariableOperand){
			ret = varTable.get(((VariableOperand)o).getName());
		}else{ // instanceof ConstantOperand
			ConstantOperand c = (ConstantOperand)o;
			ret = new HDLValue(c.getValue(), (HDLPrimitiveType)getHDLType(c.getType()));
		}
		return ret;
	}
	
	private void genExprs(SchedulerBoard board, SequencerState[] states){
		Method m = board.getMethod();
		HDLSignal return_sig = null;
		if(m.getType() != PrimitiveTypeKind.VOID){
			HDLPort return_port = hm.newPort(board.getName() + "_return", HDLPort.DIR.OUT, getHDLType(m.getType()));
			return_sig = return_port.getSignal();
		}
		
		for(SchedulerSlot slot: board.getSlots()){
			for(SchedulerItem item: slot.getItems()){
				genExpr(item, states[item.getStepId()], return_sig, paramListMap.get(board.getName()));
			}
		}
		
	}
	
	private HDLOp convOp2HDLOp(Op op){
		HDLOp ret = HDLOp.UNDEFINED;
		switch(op){
		case METHOD_ENTRY : break;
		case METHOD_EXIT : break;
		case ASSIGN : break;
		case NOP : break;
		case ADD : ret = HDLOp.ADD;break;
		case SUB : ret = HDLOp.SUB;break;
		case MUL : ret = HDLOp.MUL;break;
		case DIV : break;
		case MOD : break;
		case LT : ret = HDLOp.LT;break;
		case LEQ : ret = HDLOp.LEQ;break;
		case GT : ret = HDLOp.GT;break;
		case GEQ : ret = HDLOp.GEQ;break;
		case COMPEQ : ret = HDLOp.EQ;break;
		case NEQ : ret = HDLOp.NEQ;break;
		case LSHIFT : ret = HDLOp.LSHIFT;break;
		case LOGIC_RSHIFT : ret = HDLOp.LOGIC_RSHIFT;break;
		case ARITH_RSHIFT : ret = HDLOp.ARITH_RSHIFT;break;
		case JP : break;
		case JT : break;
		case RETURN : break;
		case SELECT : break;
		case AND : ret = HDLOp.AND;break;
		case NOT : ret = HDLOp.NOT;break;
		case LAND : ret = HDLOp.AND;break;
		case LOR : ret = HDLOp.OR;break;
		case OR : ret = HDLOp.OR;break;
		case XOR : ret = HDLOp.XOR;break;
		case LNOT : ret = HDLOp.NOT;break;
		case ARRAY_ACCESS : break;
		case CALL : break;
		case EXT_CALL : break;
		case FIELD_ACCESS : break;
		case BREAK : break;
		case CONTINUE : break;
		case CAST : break;
		case UNDEFINED : break;
		default:
		}
		return ret;
	}

	private void genExpr(SchedulerItem item, SequencerState state, HDLSignal return_sig, ArrayList<Pair> paramList){
		switch(item.getOp()){
		case METHOD_ENTRY:{
			// TODO : to treat local method invocation
			if(paramList != null){
				for(Pair pair: paramList){
					pair.sig.setAssign(state, pair.port.getSignal());
				}
			}
			break;
		}
		case METHOD_EXIT:{
			break;
		}
		case ASSIGN : {
			Operand[] src = item.getSrcOperand();
			VariableOperand dest = item.getDestOperand();
			if(dest.getType() instanceof PrimitiveTypeKind){
				HDLVariable d = (HDLVariable)(convOperandToHDLExpr(dest));
				d.setAssign(state, convOperandToHDLExpr(src[0]));
			}else if(dest.getType() instanceof ArrayRef){
				VariableRefOperand d = (VariableRefOperand)dest;
				VariableOperand ref = d.getRef();
				
				HDLInstance array = (HDLInstance)(varTable.get(ref.getName()));
				// The address to access should be settled by ARRAY_ACCESS
				//HDLExpr index = ... 
				//HDLSignal addr = array.getSignalForPort("address_b");
				//addr.setAssign(state, index);
				
				HDLSignal we = array.getSignalForPort("we_b");
				HDLSignal din = array.getSignalForPort("din_b");
				we.setAssign(state, HDLPreDefinedConstant.HIGH);
				we.setDefaultValue(HDLPreDefinedConstant.LOW);
				din.setAssign(state, convOperandToHDLExpr(src[0]));
				
			}else{
				SynthesijerUtils.warn("Unsupported ASSIGN: " + item.info());
			}
		}
		case NOP :{
			break;
		}
		case DIV :{
			System.out.println("DIV is not implemented yet.");
			break;
		}
		case MOD :{
			System.out.println("MOD is not implemented yet.");
			break;
		}
		case JP :{
			break;
		}
		case JT :{
			break;
		}
		case RETURN : {
			if(return_sig == null) break;
			Operand[] src = item.getSrcOperand();
			return_sig.setAssign(state, convOperandToHDLExpr(src[0]));
		}
		case SELECT :{
			break;
		}
		case ARRAY_ACCESS :{
			state.setMaxConstantDelay(2);
			HDLSignal dest = (HDLSignal)convOperandToHDLExpr(item.getDestOperand());
			Operand src[] = item.getSrcOperand();
			HDLInstance array = (HDLInstance)(varTable.get(((VariableOperand)src[0]).getName()));
			HDLExpr index = convOperandToHDLExpr(src[1]);
			HDLSignal addr = array.getSignalForPort("address_b");
			HDLSignal oe = array.getSignalForPort("oe_b");
			HDLSignal dout = array.getSignalForPort("dout_b");
			
			addr.setAssign(state, 0, index);
			oe.setAssign(state, 0, HDLPreDefinedConstant.HIGH);
			oe.setDefaultValue(HDLPreDefinedConstant.LOW);
			
			dest.setAssign(state, 2, dout);
			
			break;
		}
		case CALL :{
			System.out.println("CALL is not implemented yet.");
			break;
		}
		case EXT_CALL :{
			MethodInvokeItem item0 = (MethodInvokeItem)item;
			HDLInstance obj = (HDLInstance)(varTable.get(item0.obj.getName()));
			Operand[] params = item0.getSrcOperand();
			for(int i = 0; i < item0.args.length; i++){
				//System.out.println(obj.getSignalForPort(item0.name + "_" + item0.args[i]));
				HDLSignal t = obj.getSignalForPort(item0.name + "_" + item0.args[i]);
				t.setAssign(state, 0, convOperandToHDLExpr(params[i]));
			}
			HDLSignal dest = (HDLSignal)convOperandToHDLExpr(item0.getDestOperand());
			//System.out.println(dest);
			HDLSignal ret = obj.getSignalForPort(item0.name + "_return");
			//System.out.println(ret);
			dest.setAssign(state, ret);
			break;
		}
		case FIELD_ACCESS :{
			FieldAccessItem item0 = (FieldAccessItem)item;
			HDLInstance obj = (HDLInstance)(varTable.get(item0.obj.getName()));
			HDLSignal src = obj.getSignalForPort(item0.name);
			HDLSignal dest = (HDLSignal)convOperandToHDLExpr(item0.getDestOperand());
			dest.setAssign(state, src);
			break;
		}
		case BREAK :{
			break;
		}
		case CONTINUE :{
			break;
		}
		case CAST:{
			System.out.println("CAST is not implemented yet.");
			break;
		}
		case UNDEFINED :{
			System.out.println("UNDEFINED : " + item.info());
			break;
		}
		default: {
			HDLOp op = convOp2HDLOp(item.getOp());
//			if(op == HDLOp.UNDEFINED) return;
			HDLVariable dest = (HDLVariable)(convOperandToHDLExpr(item.getDestOperand()));
			Operand[] src = item.getSrcOperand();
			int nums = op.getArgNums();
			if(nums == 1){
				dest.setAssign(state, hm.newExpr(op, convOperandToHDLExpr(src[0])));  
			}else{
				dest.setAssign(state, hm.newExpr(op, convOperandToHDLExpr(src[0]), convOperandToHDLExpr(src[1])));  
			}
		}
		}
	}

	private SequencerState[] genStatemachine(SchedulerBoard board){
		HDLSequencer seq = hm.newSequencer(board.getName());
		IdGen id = new IdGen("S");
		
		HDLSignal req_local = hm.newSignal(board.getName() + "_req_local", HDLPrimitiveType.genBitType());
		HDLPort req_port = hm.newPort(board.getName() + "_req", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		HDLPort busy_port = hm.newPort(board.getName() + "_busy", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		busy_port.getSignal().setResetValue(HDLPreDefinedConstant.HIGH);
		
		SequencerState[] states = new SequencerState[board.getSlots().length]; 
		
		for(SchedulerSlot slot: board.getSlots()){
			for(SchedulerItem item: slot.getItems()){
				states[item.getStepId()] = seq.addSequencerState(id.get(item.getStepId()));
			}
		}
		for(SchedulerSlot slot: board.getSlots()){
			for(SchedulerItem item: slot.getItems()){
				SequencerState s = states[item.getStepId()];
				switch(item.getOp()){
				case METHOD_EXIT:
					s.addStateTransit(states[item.getStepId()+1]);
					busy_port.getSignal().setAssign(s, HDLPreDefinedConstant.LOW);
					break;
				case METHOD_ENTRY:
					s.addStateTransit(hm.newExpr(HDLOp.OR, req_local, req_port.getSignal()), states[item.getBranchId()[0]]);
					busy_port.getSignal().setAssign(s, hm.newExpr(HDLOp.OR, req_local, req_port.getSignal()));
					break;
				case SELECT:{
					SelectItem item0 = (SelectItem)item;
					for(int i = 0; i < item0.pat.length; i++){
						HDLExpr cond = convOperandToHDLExpr(item0.target);
						HDLExpr pat = convOperandToHDLExpr(item0.pat[i]);
						s.addStateTransit(hm.newExpr(HDLOp.EQ, cond, pat), states[item.getBranchId()[i]]);
					}
					s.addStateTransit(states[item0.getBranchId()[item0.pat.length]]);
					break;
				}
				case JT:{
					HDLExpr flag = convOperandToHDLExpr(item.getSrcOperand()[0]);
					s.addStateTransit(hm.newExpr(HDLOp.EQ, flag, HDLPreDefinedConstant.HIGH), states[item.getBranchId()[0]]);
					s.addStateTransit(hm.newExpr(HDLOp.EQ, flag, HDLPreDefinedConstant.LOW), states[item.getBranchId()[1]]);
					break;
				}
				case JP:
					s.addStateTransit(states[item.getBranchId()[0]]);
					break;
				case CALL:
				case EXT_CALL:
				{
					MethodInvokeItem item0 = (MethodInvokeItem)item;
					HDLVariable call_req, call_busy;
					HDLSignal flag;
					if(item0.getOp() == Op.EXT_CALL){
						HDLInstance obj = (HDLInstance)(varTable.get(item0.obj.getName()));
						call_req = obj.getSignalForPort(item0.name + "_req");
						call_busy = obj.getSignalForPort(item0.name + "_busy");
						flag = hm.newSignal(String.format("%s_ext_call_flag_%04d", obj.getName(), item.getStepId()), HDLPrimitiveType.genBitType(), HDLSignal.ResourceKind.WIRE);
					}else{
						call_req = varTable.get(item0.name + "_req_local");
						call_busy = varTable.get(item0.name + "_busy_sig");
						flag = hm.newSignal(String.format("%s_call_flag_%04d", item0.name, item.getStepId()), HDLPrimitiveType.genBitType(), HDLSignal.ResourceKind.WIRE);
					}
					//System.out.println("EXT_CALL: req=" + call_req);
					//System.out.println("EXT_CALL: busy=" + call_busy);
					call_req.setAssign(s, 0, HDLPreDefinedConstant.HIGH);
					call_req.setDefaultValue(HDLPreDefinedConstant.LOW); // otherwise '0'
					flag.setAssign(null, hm.newExpr(HDLOp.EQ,
							             hm.newExpr(HDLOp.AND,
							             hm.newExpr(HDLOp.EQ, call_busy, HDLPreDefinedConstant.LOW),
							             hm.newExpr(HDLOp.EQ, call_req, HDLPreDefinedConstant.LOW)),
							             HDLPreDefinedConstant.HIGH));
					s.setMaxConstantDelay(1);
					s.setStateExitFlag(flag);
					s.addStateTransit(states[item.getStepId()+1]);
					break;
				}
				default:
					if(item.isBranchOp()){
						s.addStateTransit(states[item.getBranchId()[0]]);
					}else{
						s.addStateTransit(states[item.getStepId()+1]);
					}
				}
			}
		}
		seq.getIdleState().addStateTransit(states[0]);
		return states;
	}

	private class IdGen{
		String prefix; 
		public IdGen(String prefix){
			this.prefix = prefix;
		}
		public String get(int id){
			String v = String.format("%s_%04d", prefix, id);
			return v;
		}
	}

}
