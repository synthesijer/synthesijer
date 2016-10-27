package synthesijer.scheduler;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Optional;

import synthesijer.CompileState;
import synthesijer.Options;
import synthesijer.IdentifierGenerator;
import synthesijer.Manager;
import synthesijer.Manager.SynthesijerModuleInfo;
import synthesijer.SynthesijerUtils;
import synthesijer.ast.Type;
import synthesijer.ast.type.ArrayRef;
import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.BitVector;
import synthesijer.ast.type.ComponentRef;
import synthesijer.ast.type.ComponentType;
import synthesijer.ast.type.MultipleType;
import synthesijer.ast.type.MySelfType;
import synthesijer.ast.type.PrimitiveTypeKind;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLInstanceRef;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLSignalBinding;
import synthesijer.hdl.HDLType;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.HDLVariable;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.hdl.sequencer.SequencerState;
import synthesijer.lib.FCOMP32;
import synthesijer.lib.FCOMP64;

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
		//for(ArrayList<VariableOperand> t: info.getVarTableList()){
		for(ArrayList<Operand> t: info.getVarTableList()){
			//for(VariableOperand v: t){
			for(Operand v: t){
				if(!(v instanceof VariableOperand)) continue;
				Optional<HDLVariable> var = Optional.ofNullable(genHDLVariable((VariableOperand)v));
				var.ifPresent(x -> varTable.put(v.getName(), x));
			}
		}
    }

    private SynthesijerModuleInfo searchArrayModuleInfo(PrimitiveTypeKind t, boolean publicFlag){
		SynthesijerModuleInfo info = null;
		if(publicFlag){
			switch(t){
			case BOOLEAN: info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM1");  break;
			case BYTE:    info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM8");  break;
			case SHORT:   info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM16"); break;
			case INT:     info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM32"); break;
			case LONG:    info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM64"); break;
			case FLOAT:   info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM32"); break;
			case DOUBLE:  info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM64"); break;
			default: throw new RuntimeException("unsupported type: " + t);
			}
		}else{
			switch(t){
			case BOOLEAN: info = Manager.INSTANCE.searchHDLModuleInfo("SimpleBlockRAM1");  break;
			case BYTE:    info = Manager.INSTANCE.searchHDLModuleInfo("SimpleBlockRAM8");  break;
			case SHORT:   info = Manager.INSTANCE.searchHDLModuleInfo("SimpleBlockRAM16"); break;
			case INT:     info = Manager.INSTANCE.searchHDLModuleInfo("SimpleBlockRAM32"); break;
			case LONG:    info = Manager.INSTANCE.searchHDLModuleInfo("SimpleBlockRAM64"); break;
			case FLOAT:   info = Manager.INSTANCE.searchHDLModuleInfo("SimpleBlockRAM32"); break;
			case DOUBLE:  info = Manager.INSTANCE.searchHDLModuleInfo("SimpleBlockRAM64"); break;
			default: throw new RuntimeException("unsupported type: " + t);
			}
		}
		return info;
    }
	
    private HDLInstance genArrayInstance(String name, ArrayType t, boolean publicFlag){
		SynthesijerModuleInfo info = null;
		Type t0 = t.getElemType();
		if(t0 instanceof PrimitiveTypeKind == false){
			throw new RuntimeException("unsupported type: " + t);
		}
		info = searchArrayModuleInfo((PrimitiveTypeKind)t0, publicFlag);
		HDLInstance inst = hm.newModuleInstance(info.getHDLModule(), name);
		inst.getSignalForPort("clk").setAssign(null, hm.getSysClk().getSignal());
		inst.getSignalForPort("reset").setAssign(null, hm.getSysReset().getSignal());
		return inst;
    }

    private HDLInstanceRef genArrayInstanceRef(String name, ArrayType t, boolean publicFlag){
		SynthesijerModuleInfo info = null;
		Type t0 = t.getElemType();
		if(t0 instanceof PrimitiveTypeKind == false){
			throw new RuntimeException("unsupported type: " + t);
		}
		info = searchArrayModuleInfo((PrimitiveTypeKind)t0, publicFlag);
		HDLInstanceRef ref = new HDLInstanceRef(hm, info.getHDLModule(), name);
		return ref;
    }

    private class Pair{
		public final VariableOperand orig; // the VariableOperand in given source
		public final HDLSignal reg; // the actual signal for the method.
		public final HDLPort port;  // port from outside
		public final HDLSignal local; // signal for local invocation
		public Pair(VariableOperand v, HDLSignal reg, HDLPort port, HDLSignal local){
			this.orig = v;
			this.reg = reg;
			this.port = port;
			this.local = local;
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
	
    private HDLExpr convConstantToHDLExpr(ConstantOperand c){
		HDLPrimitiveType type = (HDLPrimitiveType)getHDLType(c.getType());
		//HDLSignal tmp = hm.newSignal(String.format("constant_%04d", constIdGen.id()), type);
		//tmp.setAssign(null, new HDLValue(c.getValue(), type));
		return new HDLValue(c.getValue(), type);
    }
	
    private HDLVariable genHDLVariable(VariableOperand v, PrimitiveTypeKind type){
		String name = v.getName();
		if(type == PrimitiveTypeKind.DECLARED){
			//SynthesijerUtils.warn("Declaration is skipped: " + name + "::" + type);
			return null;
		}
		if(type == PrimitiveTypeKind.VOID) return null; // Void variable is not synthesized.
		HDLSignal sig = hm.newSignal(name, getHDLType(type));
		if(v.isDebug()) sig.setDebugFlag(true);
		//		if(v.getInitExpr() != null && v.getInitExpr().isConstant()){
		if(v.getInitSrc() != null){
			if(v.getInitSrc() instanceof ConstantOperand){
				HDLExpr e = convConstantToHDLExpr((ConstantOperand)(v.getInitSrc()));
				if(e == null){
					SynthesijerUtils.warn("initial value is not allowed:" + v.getInitSrc());
				}else{
					sig.setResetValue(e);
				}
				/*
				  }else if(v.getInitSrc() instanceof VariableOperand){
				  VariableOperand o = (VariableOperand)(v.getInitSrc());
				  if(varTable.containsKey(o.getName())){
				  HDLExpr e = varTable.get(o.getName());
				  sig.setResetValue(e);
				  }else{
				  if(v.isMember()){
				  SynthesijerUtils.warn(v.getInitSrc().info() + " is not defiend at previous using by "+ v.getName());
				  }
				  }
				  //				if(v.isMember()){
				  //					SynthesijerUtils.warn("only litral for initial value of member is allowed: " + v.getName() + ":" + v.getInitSrc().info());
				  //				}
				  */
			}
		}
		if(v.isMethodParam()){
			String prefix = v.getMethodName();
			String n = prefix + "_" + v.getOrigName();
			HDLSignal local = hm.newSignal(n + "_local", getHDLType(type));
			if(v.isPrivateMethod()){
				getMethodParamPairList(prefix).add(new Pair(v, sig, null, local));
			}else{
				HDLPort port = hm.newPort(n, HDLPort.DIR.IN, getHDLType(type));
				getMethodParamPairList(prefix).add(new Pair(v, sig, port, local));
			}
		}else if(v.isPublic() && (!v.isGlobalConstant())){
			String n = v.getOrigName();
			HDLPort din = hm.newPort(n + "_in", HDLPort.DIR.IN, getHDLType(type));
			HDLPort we = hm.newPort(n + "_we", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
			HDLPort dout = hm.newPort(n + "_out", HDLPort.DIR.OUT, getHDLType(type));
			dout.getSignal().setAssign(null, sig);
			// always(clk'posedge) sig <= din when we = '1' else sig
			HDLSignal mux = hm.newSignal(name + "_mux", getHDLType(type), HDLSignal.ResourceKind.WIRE); 
			mux.setAssign(null, hm.newExpr(HDLOp.IF, we.getSignal(), din.getSignal(), sig));
			sig.setDefaultValue(mux);
		}
		return sig;
    }
	
    private HDLVariable genHDLVariable(VariableOperand v, ArrayType t){
		String name = v.getName();
		Operand initSrc = v.getInitSrc();
		HDLVariable ret = null;
		if(initSrc != null && initSrc instanceof ArrayRefOperand){
			HDLInstance array = genArrayInstance(name, t, v.isPublic());
			ArrayRefOperand o = (ArrayRefOperand)initSrc;
			array.getParameterPair("WORDS").setValue(new HDLValue(o.words));
			int depth = o.depth;
			array.getParameterPair("DEPTH").setValue(new HDLValue(depth));
	    
			if(v.isPublic() && (!v.isGlobalConstant())){
				String n = v.getOrigName();
				HDLPort addr = hm.newPort(n + "_address", HDLPort.DIR.IN, array.getSignalForPort("address").getType());
				HDLPort we = hm.newPort(n + "_we", HDLPort.DIR.IN, array.getSignalForPort("we").getType());
				HDLPort oe = hm.newPort(n + "_oe", HDLPort.DIR.IN, array.getSignalForPort("oe").getType());
				HDLPort din = hm.newPort(n + "_din", HDLPort.DIR.IN, array.getSignalForPort("din").getType());
				HDLPort dout = hm.newPort(n + "_dout", HDLPort.DIR.OUT, array.getSignalForPort("dout").getType());
				HDLPort length = hm.newPort(n + "_length", HDLPort.DIR.OUT, array.getSignalForPort("length").getType());
				array.getSignalForPort("address").setAssign(null, addr.getSignal());
				array.getSignalForPort("we").setAssign(null, we.getSignal());
				array.getSignalForPort("oe").setAssign(null, oe.getSignal());
				array.getSignalForPort("din").setAssign(null, din.getSignal());
				dout.getSignal().setAssign(null, array.getSignalForPort("dout"));				
				length.getSignal().setAssign(null, array.getSignalForPort("length"));				
				//System.out.println("len:" + length.getName());
			}
			ret = array;
		}else if(v.isMethodParam()){
			String prefix = v.getMethodName();
			String n = prefix + "_" + v.getOrigName();
			HDLType ht = getHDLType(t.getElemType());
			HDLInstanceRef ref = genArrayInstanceRef(n, t, v.isPublic());
			ret = ref;
		}else{
			if(initSrc != null){
				//SynthesijerUtils.warn("unsupported to init array with un-immediate number:" + initSrc.info());
			}else{
				SynthesijerUtils.warn("unsupported to array initializing expression.");
			}
			SynthesijerUtils.warn("the size of memory is set as default parameter(DEPTH=1024)");
		}
	
		return ret;
    }
	
    private HDLVariable genHDLVariable(VariableOperand v, ComponentType t){
		String name = v.getName();
		//NewClassExpr expr = (NewClassExpr)v.getInitExpr();
		Operand initSrc = v.getInitSrc();
		//String instName = c.getName();
		if(initSrc == null || !(initSrc instanceof InstanceRefOperand)){
			SynthesijerUtils.warn("unsupported to init component without InstanceRefOperand:" + initSrc.info());
			return null;
		}
		//		String instName = expr.getClassName();
		String instName = ((InstanceRefOperand)initSrc).className;
		Manager.SynthesijerModuleInfo info = Manager.INSTANCE.searchHDLModuleInfo(instName);
		if(info == null){
			SynthesijerUtils.error(instName + " is not found.");
			Manager.INSTANCE.HDLModuleInfoList();
			System.exit(0);
		}
		if(info.getCompileState().isBefore(CompileState.GENERATE_HDL)){
			SynthesijerUtils.info("enters into >>>");
			Manager.INSTANCE.compileSchedulerInfo(instName);
			SynthesijerUtils.info("<<< return to compiling " + this.info.getName());
		}
		//System.out.println(v.getName());
		//System.out.println(v.getOrigName());
		HDLInstance inst = hm.newModuleInstance(info.getHDLModule(), name, v.getOrigName());
		
		if(v.getInitSrc() != null){
			Operand src = v.getInitSrc();
			if(src instanceof InstanceRefOperand){
				InstanceRefOperand o = (InstanceRefOperand)src;
				for(InstanceRefOperand.ParamPair p: o.getParameters()){
					String key = p.key;
					String value = p.value;				
					if(inst.getParameterPair(key) != null){
						inst.getParameterPair(key).setValue(new HDLValue(Integer.parseInt(value))); // ad-hoc, only to treat integer value paramters
					}else{
						SynthesijerUtils.warn(key + " is not defined in " + inst.getSubModule().getName());
						SynthesijerUtils.warn("parameter " + key + " -> " + value + " is not used.");
					}
				}
			}else{
				SynthesijerUtils.warn("can use only InstanceRefOperand for source operand of instance variable");
			}
		}
		
		Hashtable<HDLSignalBinding, HDLSignalBinding> exportBindingMap = new Hashtable<>();
		for(HDLPort p: inst.getSubModule().getPorts()){
			if(p.isSet(HDLPort.OPTION.EXPORT)){
				String n = inst.getSignalForPort(p.getName()).getName();
				//String n = p.getName();
				if(p.isSet(HDLPort.OPTION.EXPORT_PATH) == false || Options.INSTANCE.legacy_instance_variable_name){
					n += "_exp";
				}
				HDLPort export = hm.newPort(n, p.getDir(), p.getType(), EnumSet.of(HDLPort.OPTION.EXPORT, HDLPort.OPTION.EXPORT_PATH));

				hm.rmSignal(inst.getSignalForPort(p.getName()));
				inst.rmPortPair(inst.getPortPair(p));
				inst.addPortPair(export, p);
				/*
				  if(p.getDir() == HDLPort.DIR.INOUT || p.isSet(HDLPort.OPTION.NO_SIG)){
				  hm.rmSignal(inst.getSignalForPort(p.getName()));
				  inst.rmPortPair(inst.getPortPair(p));
				  inst.addPortPair(export, p);
				  }else if(p.getDir() == HDLPort.DIR.OUT){
				  export.getSignal().setAssign(null, inst.getSignalForPort(p.getName()));
				  }else{
				  inst.getSignalForPort(p.getName()).setAssign(null, export.getSignal());
				  }
				*/
				if(p.isBinded()){
					HDLSignalBinding b = p.getSignalBinding();
					HDLSignalBinding e = null;
					if(exportBindingMap.containsKey(b) == false){
						// only first time for this binding
						e = b.export(inst.getName());
						exportBindingMap.put(b, e);
					}else{
						e = exportBindingMap.get(b);
					}
					e.set(export, b.get(p));
				}
			}
		}
		
		inst.getSignalForPort(inst.getSubModule().getSysClkName()).setAssign(null, hm.getSysClk().getSignal());
		inst.getSignalForPort(inst.getSubModule().getSysResetName()).setAssign(null, hm.getSysReset().getSignal());
		return inst;
    }
	
    private HDLVariable genHDLVariable(VariableOperand v){
		Type type = v.getType();
		if(type instanceof PrimitiveTypeKind){
			return genHDLVariable(v, (PrimitiveTypeKind)type);
		}else if(type instanceof ArrayType){
			return genHDLVariable(v, (ArrayType)type);
		}else if(type instanceof ComponentType){
			return genHDLVariable(v, (ComponentType)type);
		}else if(type instanceof ArrayRef){
			String name = v.getName();
			Type t = ((ArrayRef) type).getRefType().getElemType();
			HDLSignal sig = hm.newSignal(name, getHDLType(t));
			return sig;
		}else if(type instanceof ComponentRef){
			String name = v.getName();
			ComponentRef cr = (ComponentRef)type;
			HDLSignal sig = null;
			if(cr.getRefType() instanceof PrimitiveTypeKind){
				sig = hm.newSignal(name, getHDLType(cr.getRefType()));
			}else if(cr.getRefType() instanceof ArrayType){
				Type t = ((ArrayType) cr.getRefType()).getElemType();
				sig = hm.newSignal(name, getHDLType(t));
			}else{
				System.out.println("unknown ref type: " + name + ":" + cr.getRefType());
			}
			return sig;
		}else if(type instanceof BitVector){
			String name = v.getName();
			HDLSignal sig = hm.newSignal(name, HDLPrimitiveType.genVectorType(((BitVector) type).getWidth()));
			return sig;
		}else{
			String name = v.getName();
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
		}else if(type instanceof MultipleType){
			return getHDLType((MultipleType)type);
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
		//System.err.println("unsupported type: " + t);
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

    private HDLType getHDLType(MultipleType t){
    	return getHDLType(t.get(0));
    }

    private Hashtable<SchedulerItem, HDLExpr> predExprMap;
    private void genStatemachines(){
		Hashtable<SchedulerBoard, Hashtable<HDLVariable, HDLInstance>> callStackMaps = new Hashtable<>();
		for(SchedulerBoard board: info.getBoardsList()){
			Hashtable<HDLVariable, HDLInstance> callStackMap = new Hashtable<>();
			callStackMaps.put(board, callStackMap);
			genMethodCtrlSignals(board, callStackMap);
		}
		for(SchedulerBoard board: info.getBoardsList()){
			predExprMap = new Hashtable<>();
			HardwareResource resource = new HardwareResource();
			Hashtable<Integer, SequencerState> returnTable = new Hashtable<>();
			Hashtable<HDLVariable, HDLInstance> callStackMap = callStackMaps.get(board);
			Hashtable<Integer, SequencerState> states = genStatemachine(board, resource, returnTable, callStackMap);
			genExprs(board, states, resource, returnTable);
		}
    }

    class HardwareResource{
		private HDLInstance mul32 = null;
		private HDLInstance mul64 = null;
		private HDLInstance div32 = null;
		private HDLInstance div64 = null;
		
		private HDLInstance fadd32 = null;
		private HDLInstance fsub32 = null;
		private HDLInstance fmul32 = null;
		private HDLInstance fdiv32 = null;
		
		private HDLInstance fadd64 = null;
		private HDLInstance fsub64 = null;
		private HDLInstance fmul64 = null;
		private HDLInstance fdiv64 = null;
		
		private HDLInstance f2i = null;
		private HDLInstance i2f = null;
		private HDLInstance d2l = null;
		private HDLInstance l2d = null;
		private HDLInstance f2d = null;
		private HDLInstance d2f = null;
		
		private HDLInstance lshift32 = null;
		private HDLInstance logic_rshift32 = null;
		private HDLInstance arith_rshift32 = null;
		private HDLInstance lshift64 = null;
		private HDLInstance logic_rshift64 = null;
		private HDLInstance arith_rshift64 = null;
		
		private HDLInstance fcomp32 = null;
		private HDLInstance fcomp64 = null;
    }
	
    private IdentifierGenerator constIdGen = new IdentifierGenerator();
    private HDLExpr convOperandToHDLExpr(SchedulerItem item, Operand o){
		HDLExpr ret;
		if(o instanceof VariableOperand){
			if(o.isChaining(item)){
				SchedulerItem pred = ((VariableOperand)o).getPredItem(item);
				if(predExprMap.containsKey(pred)){
					//ret = predExprMap.get(pred);
					ret = predExprMap.get(pred).getResultExpr();
				}else{
					SynthesijerUtils.warn("detected chaining, but chained expression is not found.");
					ret = varTable.get(((VariableOperand)o).getName());	
				}
			}else{
				ret = varTable.get(((VariableOperand)o).getName());
			}
		}else{ // instanceof ConstantOperand
			ConstantOperand c = (ConstantOperand)o;
			HDLPrimitiveType type = (HDLPrimitiveType)getHDLType(c.getType());
			//HDLSignal tmp = hm.newSignal(String.format("constant_%04d", constIdGen.id()), type);
			//tmp.setAssign(null, new HDLValue(c.getValue(), type));
			ret = new HDLValue(c.getValue(), type);
		}
		return ret;
    }
	
    private void genExprs(SchedulerBoard board, Hashtable<Integer, SequencerState> states, HardwareResource resource, Hashtable<Integer, SequencerState> returnTable){
		HDLSignal return_sig = null;
		return_sig = returnSigTable.get(board);
		Hashtable<String, FieldAccessItem> fieldAccessChainMap = new Hashtable<>();
		for(SchedulerSlot slot: board.getSlots()){
			int id = slot.getStepId();
			for(SchedulerItem item: slot.getItems()){
				genExpr(board, resource, item, states.get(id), return_sig, paramListMap.get(board.getName()), fieldAccessChainMap, predExprMap, returnTable);
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
		case MUL32 : break; //ret = HDLOp.MUL;break;
		case MUL64 : break; // ret = HDLOp.MUL;break;
		case DIV32 : break;
		case DIV64 : break;
		case MOD32 : break;
		case MOD64 : break;
		case LT : ret = HDLOp.LT;break;
		case LEQ : ret = HDLOp.LEQ;break;
		case GT : ret = HDLOp.GT;break;
		case GEQ : ret = HDLOp.GEQ;break;
		case COMPEQ : ret = HDLOp.EQ;break;
		case NEQ : ret = HDLOp.NEQ;break;
		case SIMPLE_LSHIFT32 : ret = HDLOp.LSHIFT32;break;
		case SIMPLE_LOGIC_RSHIFT32 : ret = HDLOp.LOGIC_RSHIFT32;break;
		case SIMPLE_ARITH_RSHIFT32 : ret = HDLOp.ARITH_RSHIFT32;break;
		case SIMPLE_LSHIFT64 : ret = HDLOp.LSHIFT64;break;
		case SIMPLE_LOGIC_RSHIFT64 : ret = HDLOp.LOGIC_RSHIFT64;break;
		case SIMPLE_ARITH_RSHIFT64 : ret = HDLOp.ARITH_RSHIFT64;break;
		case LSHIFT32 : break;
		case LOGIC_RSHIFT32 : break;
		case ARITH_RSHIFT32 : break;
		case LSHIFT64 : break;
		case LOGIC_RSHIFT64 : break;
		case ARITH_RSHIFT64 : break;
		case JP : break;
		case JT : break;
		case RETURN : break;
		case SELECT : break;
		case AND : ret = HDLOp.AND;break;
		case NOT : ret = HDLOp.NOT;break;
		case MSB_FLAP : ret = HDLOp.MSB_FLAP;break;
		case LAND : ret = HDLOp.AND;break;
		case LOR : ret = HDLOp.OR;break;
		case OR : ret = HDLOp.OR;break;
		case XOR : ret = HDLOp.XOR;break;
		case LNOT : ret = HDLOp.NOT;break;
		case ARRAY_ACCESS : break;
		case ARRAY_ACCESS_WAIT : break;
		case ARRAY_ACCESS0 : break;
		case ARRAY_INDEX : break;
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
	
    private int getBitWidth(Type t){
		if(t instanceof PrimitiveTypeKind){
			return ((PrimitiveTypeKind) t).getWidth();
		}else if(t instanceof BitVector){
			return ((BitVector) t).getWidth();
		}else if(t instanceof ArrayRef){
			return getBitWidth(((ArrayRef) t).getRefType());
		}else if(t instanceof ArrayType){
			return getBitWidth(((ArrayType) t).getElemType());
		}else{
			System.out.println(t);
			return -1;
		}
    }
	
    private void genExpr(SchedulerBoard board, HardwareResource resource, SchedulerItem item, SequencerState state, HDLSignal return_sig, ArrayList<Pair> paramList, Hashtable<String, FieldAccessItem> fieldAccessChainMap, Hashtable<SchedulerItem, HDLExpr> predExprMap, Hashtable<Integer, SequencerState> returnTable){
		switch(item.getOp()){
		case METHOD_ENTRY:{
			if(paramList != null){
				for(Pair pair: paramList){
					// MUX to select valid siganl from inside/outside arguments
					HDLExpr arg;
					if(board.isPrivate() == false){
						arg = hm.newExpr(HDLOp.IF, varTable.get(board.getName()+"_req"), pair.port.getSignal(), pair.local);
					}else{
						arg = pair.local;
					}
					pair.reg.setAssign(state, arg);
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
			if(dest.getType() instanceof PrimitiveTypeKind || dest.getType() instanceof BitVector){
				
				HDLVariable d;
				FieldAccessItem fa = fieldAccessChainMap.get(dest.getName());
				if(fa != null){
					// should write into field variable pointed by FIELD_ACCESS
					HDLInstance obj = (HDLInstance)(varTable.get(fa.obj.getName()));
					d = obj.getSignalForPort(fa.name);
					if(d != null){
						// for unsynthesized HDLModule
					}else{
						d = obj.getSignalForPort(fa.name + "_in");
						HDLSignal we = obj.getSignalForPort(fa.name + "_we");
						if(we != null){
							we.setAssign(state, HDLPreDefinedConstant.HIGH); // in this state
							we.setDefaultValue(HDLPreDefinedConstant.LOW); // otherwise
						}
					}
				}else{
					d = (HDLVariable)(convOperandToHDLExpr(item, dest));
				}
				if(d != null){
					HDLExpr expr = convOperandToHDLExpr(item, src[0]);
					d.setAssign(state, expr);
					predExprMap.put(item, expr);
				}
				
			}else if(dest.getType() instanceof ArrayRef){
				VariableRefOperand d = (VariableRefOperand)dest;
				VariableOperand ref = d.getRef();
				
				HDLSignal we = null, din = null, addr = null;
				HDLVariable var = varTable.get(ref.getName());
				if(var instanceof HDLInstance){
					// local memory
					HDLInstance array = (HDLInstance)var;
					//we = array.getSignalForPort("we_b");
					//din = array.getSignalForPort("din_b");
					we = hm.getSignal(array.getName() + "_we_b");
					din = hm.getSignal(array.getName() + "_din_b");
					addr = hm.getSignal(array.getName() + "_address_b");
				}else if(var instanceof HDLInstanceRef){
					HDLInstanceRef array = (HDLInstanceRef)var;
					we = array.getPortByPostfix("_we_b").get().getSignal();
					din = array.getPortByPostfix("_din_b").get().getSignal();
					addr = array.getPortByPostfix("_address_b").get().getSignal();
				}else{
					// external memory (through Field Access)
					FieldAccessItem fa = fieldAccessChainMap.get(ref.getName());
					HDLInstance obj = (HDLInstance)(varTable.get(fa.obj.getName()));
					we = obj.getSignalForPort(fa.name + "_we");
					din = obj.getSignalForPort(fa.name + "_din");
					addr = obj.getSignalForPort(fa.name + "_address_b");
				}
		
				we.setAssign(state, HDLPreDefinedConstant.HIGH);
				we.setDefaultValue(HDLPreDefinedConstant.LOW);
		
				HDLExpr expr = convOperandToHDLExpr(item, src[0]);
				din.setAssign(state, expr);
				if(addr == null){
					// indirect memory access. in this case, destination address is already asserted...
				}else{
					addr.setAssign(state, convOperandToHDLExpr(item, d.getPtr()));
				}
				predExprMap.put(item, expr);
			}else{
				SynthesijerUtils.warn("Unsupported ASSIGN: " + item.info());
			}
		}
		case NOP :{
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
			return_sig.setAssign(state, convOperandToHDLExpr(item, src[0]));
		}
		case MULTI_RETURN : {
			// TODO
			Operand[] src = item.getSrcOperand();
			HDLSignal s = returnMultiSigTable.get(board)[Integer.parseInt(src[0].getName())];
			//return_sig.setAssign(state, convOperandToHDLExpr(item, src[1]));
			s.setAssign(state, convOperandToHDLExpr(item, src[1]));
		}
		case SELECT :{
			break;
		}
		case ARRAY_ACCESS_WAIT :{
			break;
		}
		case ARRAY_ACCESS0:
		case ARRAY_ACCESS :{
			if(item.getOp() == Op.ARRAY_ACCESS){
				state.setMaxConstantDelay(2);
			}
			HDLSignal dest = (HDLSignal)convOperandToHDLExpr(item, item.getDestOperand());
			Operand src[] = item.getSrcOperand();
			
			HDLSignal addr = null, oe = null, dout = null;
			HDLVariable var = varTable.get(((VariableOperand)src[0]).getName());
			if(var instanceof HDLInstance){
				// local memory
				HDLInstance array = (HDLInstance)var;
				//addr = array.getSignalForPort("address_b");
				//oe = array.getSignalForPort("oe_b");
				//dout = array.getSignalForPort("dout_b");
				addr = hm.getSignal(array.getName() + "_address_b");
				oe = hm.getSignal(array.getName() + "_oe_b");
				dout = hm.getSignal(array.getName() + "_dout_b");
			}else if(var instanceof HDLInstanceRef){
				HDLInstanceRef array = (HDLInstanceRef)var;
				addr = array.getPortByPostfix("_address_b").get().getSignal();
				oe = array.getPortByPostfix("_oe_b").get().getSignal();
				dout = array.getPortByPostfix("_dout_b").get().getSignal();
			}else{
				// external memory (through Field Access)
				FieldAccessItem fa = fieldAccessChainMap.get(((VariableOperand)src[0]).getName());
				HDLInstance obj = (HDLInstance)(varTable.get(fa.obj.getName()));
				addr = obj.getSignalForPort(fa.name + "_address");
				oe = obj.getSignalForPort(fa.name + "_oe");
				dout = obj.getSignalForPort(fa.name + "_dout");
			}

			HDLExpr index = convOperandToHDLExpr(item, src[1]);
			if((addr != null) && (item.getOp() == Op.ARRAY_ACCESS)){
				addr.setAssign(state, 0, index);
			}
			if(oe != null){
				oe.setAssign(state, 0, HDLPreDefinedConstant.HIGH);
				oe.setDefaultValue(HDLPreDefinedConstant.LOW);
			}
			if(dout != null){
				if(item.getOp() == Op.ARRAY_ACCESS){
					dest.setAssign(state, 2, dout);
				}else{
					dest.setAssign(state, dout);
				}
				predExprMap.put(item, dout);
			}
			
			break;
		}
		case ARRAY_INDEX :{

			Operand src[] = item.getSrcOperand();
			
			HDLSignal addr = null;
			HDLVariable var = varTable.get(((VariableOperand)src[0]).getName());
			if(var instanceof HDLInstance){
				// local memory
				HDLInstance array = (HDLInstance)var;
				//addr = array.getSignalForPort("address_b");
				addr = hm.getSignal(array.getName() + "_address_b");
			}else if(var instanceof HDLInstanceRef){
				HDLInstanceRef array = (HDLInstanceRef)var;
				addr = array.getPortByPostfix("_address_b").get().getSignal();
			}else{
				// external memory (through Field Access)
				FieldAccessItem fa = fieldAccessChainMap.get(((VariableOperand)src[0]).getName());
		
				HDLInstance obj = (HDLInstance)(varTable.get(fa.obj.getName()));
				addr = obj.getSignalForPort(fa.name + "_address");
			}
			
			HDLExpr index = convOperandToHDLExpr(item, src[1]);

			addr.setAssign(state, index);
	    
			break;
		}
		case CALL :{
			MethodInvokeItem item0 = (MethodInvokeItem)item;
			Operand[] params = item0.getSrcOperand();
			ArrayList<Pair> list = getMethodParamPairList(item0.name);
			for(int i = 0; i < params.length; i++){
				HDLSignal t = list.get(i).local;
				HDLExpr s = convOperandToHDLExpr(item, params[i]);
				t.setAssign(state.getTransitions().get(0).getDestState(), 0, s);  // should set in ***_body
				//t.setAssign(state, 0, s);
			}
			if(item0.getDestOperand().getType() != PrimitiveTypeKind.VOID){
				HDLSignal dest = (HDLSignal)convOperandToHDLExpr(item, item0.getDestOperand());
				HDLSignal ret = hm.getSignal(item0.name + "_return");
				if(ret == null) ret = hm.getPort(item0.name + "_return").getSignal();
				dest.setAssign(state.getTransitions().get(0).getDestState().getTransitions().get(0).getDestState(), ret); // should read in ***_wait
				predExprMap.put(item, ret);
				SequencerState retState = returnTable.get(item0.getStepId());
				if(retState != null){
					dest.setAssign(retState, ret); // should read in ***_wait
				}
			}
			break;
		}
		case EXT_CALL :{
			MethodInvokeItem item0 = (MethodInvokeItem)item;
			HDLInstance obj = (HDLInstance)(varTable.get(item0.obj.getName()));
			Operand[] params = item0.getSrcOperand();
			for(int i = 0; i < item0.args.length; i++){
				Type t = params[i].getType();
				if(t instanceof ArrayType){
					// connect array type parameter
					HDLInstance target = (HDLInstance)(varTable.get((params[i]).getName()));
					HDLSignal flag = hm.newTmpSignal(HDLPrimitiveType.genBitType(), HDLSignal.ResourceKind.REGISTER);
					flag.setDefaultValue(HDLPreDefinedConstant.LOW);
					flag.setAssign(state, HDLPreDefinedConstant.HIGH);
					flag.setAssign(state.getTransitions().get(0).getDestState(), HDLPreDefinedConstant.HIGH); // ext_call body
					flag.setAssign(state.getTransitions().get(0).getDestState().getTransitions().get(0).getDestState(), HDLPreDefinedConstant.HIGH); // ext_call wait
					connectArrayTypeParammeter(flag, target, obj, item0.name + "_" + item0.args[i]); // connect obj into target
				}else{
					// for primitive parameters
					HDLSignal d = obj.getSignalForPort(item0.name + "_" + item0.args[i]);
					// should set in ***_body
					d.setAssign(state.getTransitions().get(0).getDestState(), 0, convOperandToHDLExpr(item, params[i]));
					//t.setAssign(state, 0, convOperandToHDLExpr(params[i]));
				}
			}
			if(item0.getDestOperand().getType() != PrimitiveTypeKind.VOID){ // non-void function
				HDLSignal dest = (HDLSignal)convOperandToHDLExpr(item, item0.getDestOperand());
				HDLSignal ret = obj.getSignalForPort(item0.name + "_return");
				dest.setAssign(state.getTransitions().get(0).getDestState().getTransitions().get(0).getDestState(), ret); // should read in ***_wait
				//dest.setAssign(state, ret);
				predExprMap.put(item, ret);
				SequencerState retState = returnTable.get(item0.getStepId());
				if(retState != null){
					dest.setAssign(retState, ret); // should read in ***_wait
				}
			}
			break;
		}
		case FIELD_ACCESS :{
			FieldAccessItem item0 = (FieldAccessItem)item;
			HDLVariable var = varTable.get(item0.obj.getName());
			HDLSignal src = null;
			if(var != null && var instanceof HDLInstance){
				HDLInstance obj = (HDLInstance)var;
				src = obj.getSignalForPort(item0.name);
				if(src == null){
					src = obj.getSignalForPort(item0.name + "_out"); // normal
				}
			}else if(var != null && var instanceof HDLInstanceRef){
				src = hm.getPort(var.getName() + "_" + item0.name).getSignal();
			}
			HDLExpr dest = convOperandToHDLExpr(item, item0.getDestOperand());
			if(dest instanceof HDLSignal && src != null){
				HDLSignal d = (HDLSignal)dest;
				d.setAssign(state, src);
				predExprMap.put(item, src);
			}else{
				// just ref
			}
			// stored this item into map to use in following items
			fieldAccessChainMap.put(item0.getDestOperand().getName(), item0);
			break;
		}
		case BREAK :{
			break;
		}
		case CONTINUE :{
			break;
		}
		case CAST:{
			TypeCastItem item0 = (TypeCastItem)item;
			HDLSignal dest = (HDLSignal)(convOperandToHDLExpr(item, item.getDestOperand()));
			HDLExpr src = convOperandToHDLExpr(item, item.getSrcOperand()[0]);
			if(src instanceof HDLValue){
				HDLSignal tmp = hm.newSignal(String.format("const_%04d", constIdGen.id()), (HDLPrimitiveType)src.getType());
				tmp.setAssign(null, src);
				src = tmp;
			}
			int w0 = getBitWidth(item0.orig);
			int w1 = getBitWidth(item0.target);
			if(w0 < 0 || w1 < 0){
				SynthesijerUtils.error("Unsupported CAST: " + item.info());
			}
			HDLExpr expr; 
			if(w0 > w1){
				expr = hm.newExpr(HDLOp.DROPHEAD, src, HDLUtils.newValue(w0-w1, 32));
			}else if(w0 < w1){
				expr = hm.newExpr(HDLOp.PADDINGHEAD, src, HDLUtils.newValue(w1-w0, 32));
			}else{
				expr = src; 
			}
			dest.setAssign(state, expr);
			predExprMap.put(item, expr);
			break;
		}
		case COND:{
			HDLVariable dest = (HDLVariable)(convOperandToHDLExpr(item, item.getDestOperand()));
			Operand[] src = item.getSrcOperand();
			HDLExpr expr = hm.newExpr(HDLOp.IF,
									  convOperandToHDLExpr(item, src[0]),
									  convOperandToHDLExpr(item, src[1]),
									  convOperandToHDLExpr(item, src[2]));
			dest.setAssign(state, expr);
			predExprMap.put(item, expr);
			break;
		}
		case UNDEFINED :{
			System.out.println("UNDEFINED : " + item.info());
			break;
		}
		case LSHIFT32:
		case LOGIC_RSHIFT32:
		case ARITH_RSHIFT32:
		case LSHIFT64:
		case LOGIC_RSHIFT64:
		case ARITH_RSHIFT64:
		case MUL32:
		case MUL64:
			{
				Operand[] arg = item.getSrcOperand();
				HDLInstance inst = getOperationUnit(item.getOp(), resource, board.getName());
				inst.getSignalForPort("a").setAssign(state, 0, convOperandToHDLExpr(item, arg[0]));
				inst.getSignalForPort("b").setAssign(state, 0, convOperandToHDLExpr(item, arg[1]));
				HDLSignal dest = (HDLSignal)convOperandToHDLExpr(item, item.getDestOperand());
				dest.setAssign(state, inst.getSignalForPort("result"));
				predExprMap.put(item, inst.getSignalForPort("result"));
				break;
			}
		case DIV32 :
		case DIV64 :
			{
				Operand[] arg = item.getSrcOperand();
				HDLInstance inst = getOperationUnit(item.getOp(), resource, board.getName());
				inst.getSignalForPort("a").setAssign(state, 0, convOperandToHDLExpr(item, arg[0]));
				inst.getSignalForPort("b").setAssign(state, 0, convOperandToHDLExpr(item, arg[1]));
				inst.getSignalForPort("nd").setAssign(state, 0, HDLPreDefinedConstant.HIGH);
				inst.getSignalForPort("nd").setDefaultValue(HDLPreDefinedConstant.LOW);
				inst.getSignalForPort("nd").setResetValue(HDLPreDefinedConstant.LOW);
				HDLSignal dest = (HDLSignal)convOperandToHDLExpr(item, item.getDestOperand());
				dest.setAssign(state, inst.getSignalForPort("quantient"));
				predExprMap.put(item, inst.getSignalForPort("quantient"));
				break;
			}
		case MOD32 :
		case MOD64 :
			{
				Operand[] arg = item.getSrcOperand();
				HDLInstance inst = getOperationUnit(item.getOp(), resource, board.getName());
				inst.getSignalForPort("a").setAssign(state, 0, convOperandToHDLExpr(item, arg[0]));
				inst.getSignalForPort("b").setAssign(state, 0, convOperandToHDLExpr(item, arg[1]));
				inst.getSignalForPort("nd").setAssign(state, 0, HDLPreDefinedConstant.HIGH);
				inst.getSignalForPort("nd").setDefaultValue(HDLPreDefinedConstant.LOW);
				HDLSignal dest = (HDLSignal)convOperandToHDLExpr(item, item.getDestOperand());
				dest.setAssign(state, inst.getSignalForPort("remainder"));
				predExprMap.put(item, inst.getSignalForPort("remainder"));
				break;
			}
		case FADD32 :
		case FSUB32 :
		case FMUL32 :
		case FDIV32 :
		case FADD64 :
		case FSUB64 :
		case FMUL64 :
		case FDIV64 :
			{
				Operand[] arg = item.getSrcOperand();
				HDLInstance inst = getOperationUnit(item.getOp(), resource, board.getName());
				inst.getSignalForPort("a").setAssign(state, 0, convOperandToHDLExpr(item, arg[0]));
				inst.getSignalForPort("b").setAssign(state, 0, convOperandToHDLExpr(item, arg[1]));
				inst.getSignalForPort("nd").setAssign(state, 0, HDLPreDefinedConstant.HIGH);
				inst.getSignalForPort("nd").setDefaultValue(HDLPreDefinedConstant.LOW);
				HDLSignal dest = (HDLSignal)convOperandToHDLExpr(item, item.getDestOperand());
				dest.setAssign(state, inst.getSignalForPort("result"));
				predExprMap.put(item, inst.getSignalForPort("result"));
				break;
			}
		case CONV_F2I:
		case CONV_I2F:
		case CONV_D2L:
		case CONV_L2D:
		case CONV_F2D:
		case CONV_D2F:
			{
				Operand[] arg = item.getSrcOperand();
				HDLInstance inst = getOperationUnit(item.getOp(), resource, board.getName());
				inst.getSignalForPort("a").setAssign(state, 0, convOperandToHDLExpr(item, arg[0]));
				inst.getSignalForPort("nd").setAssign(state, 0, HDLPreDefinedConstant.HIGH);
				inst.getSignalForPort("nd").setDefaultValue(HDLPreDefinedConstant.LOW);
				HDLSignal dest = (HDLSignal)convOperandToHDLExpr(item, item.getDestOperand());
				dest.setAssign(state, inst.getSignalForPort("result"));
				predExprMap.put(item, inst.getSignalForPort("result"));
				break;
			}
		case FLT32:     genCompUnitExpr(item, FCOMP32.LT, state, resource, board);  break;
		case FLEQ32:    genCompUnitExpr(item, FCOMP32.LEQ, state, resource, board); break;
		case FGT32:     genCompUnitExpr(item, FCOMP32.GT, state, resource, board);  break;
		case FGEQ32:    genCompUnitExpr(item, FCOMP32.GEQ, state, resource, board); break;
		case FCOMPEQ32: genCompUnitExpr(item, FCOMP32.EQ, state, resource, board);  break;
		case FNEQ32:    genCompUnitExpr(item, FCOMP32.NEQ, state, resource, board); break;
		case FLT64:     genCompUnitExpr(item, FCOMP64.LT, state, resource, board);  break;
		case FLEQ64:    genCompUnitExpr(item, FCOMP64.LEQ, state, resource, board); break;
		case FGT64:     genCompUnitExpr(item, FCOMP64.GT, state, resource, board);  break;
		case FGEQ64:    genCompUnitExpr(item, FCOMP64.GEQ, state, resource, board); break;
		case FCOMPEQ64: genCompUnitExpr(item, FCOMP64.EQ, state, resource, board);  break;
		case FNEQ64:    genCompUnitExpr(item, FCOMP64.NEQ, state, resource, board); break;
		case MSB_FLAP:{
			HDLOp op = convOp2HDLOp(item.getOp());
			HDLVariable dest = (HDLVariable)(convOperandToHDLExpr(item, item.getDestOperand()));
			HDLExpr src = convOperandToHDLExpr(item, item.getSrcOperand()[0]);
			if(src instanceof HDLValue){
				HDLSignal tmp = hm.newSignal(String.format("const_%04d", constIdGen.id()), (HDLPrimitiveType)src.getType());
				tmp.setAssign(null, src);
				src = tmp;
			}
			HDLExpr expr = hm.newExpr(op, src);
			dest.setAssign(state, expr);
			predExprMap.put(item, expr);
			break;
		}
		default: {
			//			System.out.println(item.info());
			HDLOp op = convOp2HDLOp(item.getOp());
			//			if(op == HDLOp.UNDEFINED) return;
			HDLVariable dest = (HDLVariable)(convOperandToHDLExpr(item, item.getDestOperand()));
			Operand[] src = item.getSrcOperand();
			int nums = op.getArgNums();
			
			HDLExpr expr = (nums == 1) ?
				hm.newExpr(op, convOperandToHDLExpr(item, src[0])) :
				hm.newExpr(op, convOperandToHDLExpr(item, src[0]), convOperandToHDLExpr(item, src[1]));
				
			dest.setAssign(state, expr);
			predExprMap.put(item, expr);
		}
		}
    }

    private void connectArrayTypeParammeter(HDLExpr flag, HDLInstance src, HDLInstance dest, String key){

		HDLExpr mux_key = hm.newExpr(HDLOp.OR, dest.getSignalForPort(key + "_we_b"), dest.getSignalForPort(key + "_oe_b"));
	
		// length (dest.length <- src.length)
		HDLSignal dest_length = dest.getSignalForPort(key + "_length");
		if(dest_length.isAssignAlways() == false){
			dest_length.setAssign(null, src.getSignalForPort("length"));
		}else{
			HDLExpr expr = dest_length.getAssignAlwaysExpr();
			dest_length.setAssign(null, hm.newExpr(HDLOp.IF, flag, src.getSignalForPort("length"), expr));
		}

		// dout_b (dest.dout_b <- src.dout_b)
		HDLSignal dest_dout = dest.getSignalForPort(key + "_dout_b");
		if(dest_dout.isAssignAlways() == false){
			dest_dout.setAssign(null, src.getSignalForPort("dout_b"));
		}else{
			HDLExpr expr = dest_dout.getAssignAlwaysExpr();
			dest_dout.setAssign(null, hm.newExpr(HDLOp.IF, flag, src.getSignalForPort("dout_b"), expr));
		}

		// address (src.address_b <- dest.address_b when mux_key else src.address_b)
		HDLSignal dest_address_b = dest.getSignalForPort(key + "_address_b");
		HDLSignal src_address_b = src.getSignalForPort("address_b");
		HDLSignal address_b_mux = hm.newTmpSignal(src_address_b.getType(), HDLSignal.ResourceKind.WIRE);
		//address_b_mux.setAssign(null, hm.newExpr(HDLOp.IF, hm.newExpr(HDLOp.AND, mux_key, flag), dest_address_b, src_address_b));
		address_b_mux.setAssign(null, hm.newExpr(HDLOp.IF, flag, dest_address_b, src_address_b));
		src.replacePortPair(src_address_b, address_b_mux);

		// din (src.din_b <- dest.din_b when mux_key else src.din_b)
		HDLSignal dest_din_b = dest.getSignalForPort(key + "_din_b");
		HDLSignal src_din_b = src.getSignalForPort("din_b");
		HDLSignal din_b_mux = hm.newTmpSignal(src_din_b.getType(), HDLSignal.ResourceKind.WIRE);
		din_b_mux.setAssign(null, hm.newExpr(HDLOp.IF, mux_key, dest_din_b, src_din_b));
		src.replacePortPair(src_din_b, din_b_mux);

		// we (src.we_b <- dest.we_b or src.we_b)
		HDLSignal dest_we_b = dest.getSignalForPort(key + "_we_b");
		HDLSignal src_we_b = src.getSignalForPort("we_b");
		HDLSignal we_b_or = hm.newTmpSignal(src_we_b.getType(), HDLSignal.ResourceKind.WIRE);
		we_b_or.setAssign(null, hm.newExpr(HDLOp.OR, hm.newExpr(HDLOp.AND, flag, dest_we_b), src_we_b));
		src.replacePortPair(src_we_b, we_b_or);

		// oe (src.oe_b <- dest.oe_b or else src.oe_b)
		HDLSignal dest_oe_b = dest.getSignalForPort(key + "_oe_b");
		HDLSignal src_oe_b = src.getSignalForPort("oe_b");
		HDLSignal oe_b_or = hm.newTmpSignal(src_oe_b.getType(), HDLSignal.ResourceKind.WIRE);
		oe_b_or.setAssign(null, hm.newExpr(HDLOp.OR, dest_oe_b, src_oe_b));
		src.replacePortPair(src_oe_b, oe_b_or);
    }
	
    private void genCompUnitExpr(SchedulerItem item, int opcode, SequencerState state, HardwareResource resource, SchedulerBoard board){
		Operand[] arg = item.getSrcOperand();
		HDLInstance inst = getOperationUnit(item.getOp(), resource, board.getName());
		inst.getSignalForPort("a").setAssign(state, 0, convOperandToHDLExpr(item, arg[0]));
		inst.getSignalForPort("b").setAssign(state, 0, convOperandToHDLExpr(item, arg[1]));
		inst.getSignalForPort("opcode").setAssign(state, 0, HDLUtils.newValue(opcode, 8));
		inst.getSignalForPort("nd").setAssign(state, 0, HDLPreDefinedConstant.HIGH);
		inst.getSignalForPort("nd").setDefaultValue(HDLPreDefinedConstant.LOW);
		HDLSignal dest = (HDLSignal)convOperandToHDLExpr(item, item.getDestOperand());
		dest.setAssign(state, inst.getSignalForPort("result"));
		predExprMap.put(item, inst.getSignalForPort("result"));
    }
	
    private Hashtable<SchedulerBoard, HDLSignal> returnSigTable = new Hashtable<>();
    private Hashtable<SchedulerBoard, HDLSignal[]> returnMultiSigTable = new Hashtable<>();

    private void genMethodCtrlSignals(SchedulerBoard board, Hashtable<HDLVariable, HDLInstance> callStackMap){
		if(board.getReturnType() != PrimitiveTypeKind.VOID &&
		   !(board.getReturnType() instanceof synthesijer.ast.type.MySelfType) &&
		   !(board.getReturnType() instanceof synthesijer.ast.type.MultipleType)
		  ){
			if(board.isPrivate() == false){
				HDLPort return_port = hm.newPort(board.getName() + "_return", HDLPort.DIR.OUT, getHDLType(board.getReturnType()));
				returnSigTable.put(board, return_port.getSignal());
			}else{
				HDLSignal return_sig = hm.newSignal(board.getName() + "_return", getHDLType(board.getReturnType()));
				returnSigTable.put(board, return_sig);
			}
		}
		
		if(board.getReturnType() instanceof synthesijer.ast.type.MultipleType){
			MultipleType t = (MultipleType)(board.getReturnType());
			ArrayList<HDLSignal> list = new ArrayList<HDLSignal>();
			for(int i = 0; i < t.size(); i++){
				if(board.isPrivate() == false){
					HDLPort return_port = hm.newPort(board.getName() + "_return_" + i, HDLPort.DIR.OUT, getHDLType(board.getReturnType()));
					list.add(return_port.getSignal());
				}else{
					HDLSignal return_sig = hm.newSignal(board.getName() + "_return_" + i, getHDLType(board.getReturnType()));
					list.add(return_sig);
				}
			}
			returnMultiSigTable.put(board, list.toArray(new HDLSignal[]{}));
		}

		if(board.isAuto()){
			return; 
		}
		
		// generating a busy port signal
		if(board.isPrivate() == false){ // public
			HDLPort busy_port = hm.newPort(board.getName() + "_busy", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
			busy_port.getSignal().setResetValue(HDLPreDefinedConstant.HIGH);
			varTable.put(busy_port.getName(), busy_port.getSignal());
		}else{ // private
			HDLSignal busy_sig = hm.newSignal(board.getName() + "_busy", HDLPrimitiveType.genBitType());
			varTable.put(busy_sig.getName(), busy_sig);
		}
				
		HDLSignal req_flag = hm.newSignal(board.getName() + "_req_flag", HDLPrimitiveType.genBitType());
		HDLSignal req_local = hm.newSignal(board.getName() + "_req_local", HDLPrimitiveType.genBitType());
		if(board.isPrivate() == false){ // public
			HDLPort req_port = hm.newPort(board.getName() + "_req", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
			varTable.put(req_port.getName(), req_port.getSignal());
			req_flag.setAssign(null, hm.newExpr(HDLOp.OR, req_local, req_port.getSignal()));
		}else{ // private
			req_flag.setAssign(null, req_local);
		}
		
		varTable.put(req_flag.getName(), req_flag);
		varTable.put(req_local.getName(), req_local);
		
		if(board.hasCallStack()){
			HDLInstance call_stack = genStackForRecursiveCall(board.getName() + "_call_stack_memory", board.getCallStackSize(), 16);
			//for(VariableOperand v: board.getVarList()){
			for(Operand v: board.getVarList()){
				if(!(v instanceof VariableOperand)) continue;
				if(((VariableOperand)v).getMethodName() == null){
					continue; // skip non-user defined variable
				}
				HDLVariable hv = varTable.get(v.getName());
				if(hv.getType() instanceof HDLPrimitiveType){
					HDLInstance inst = genStackForRecursiveCall(hv.getName() + "_call_stack_memory", board.getCallStackSize(), ((HDLPrimitiveType)hv.getType()).getWidth());
					callStackMap.put(hv, inst);
				}else{
					SynthesijerUtils.warn("cannot preserve non-primitive type variable for recursive call:" + v);
				}
			}
		}
    }
		
    private HDLInstance genStackForRecursiveCall(String name, int size, int width){
		HDLInstance stack;
		switch(width){
		case 1:
			stack = newInstModule("SimpleBlockRAM1", name);
			break;
		case 8:
			stack = newInstModule("SimpleBlockRAM8", name);
			break;
		case 16:
			stack = newInstModule("SimpleBlockRAM16", name);
			break;
		case 32:
			stack = newInstModule("SimpleBlockRAM32", name);
			break;
		case 64:
			stack = newInstModule("SimpleBlockRAM64", name);
			break;
		default:
			SynthesijerUtils.warn("SchedulerInfoCompiler: might be generated unexpected stack:" + name);
			stack = newInstModule("SimpleBlockRAM32", name);
		}
		stack.getSignalForPort("clk").setAssign(null, hm.getSysClk().getSignal());
		stack.getSignalForPort("reset").setAssign(null, hm.getSysReset().getSignal());
		stack.getParameterPair("WORDS").setValue(new HDLValue(size));
		int depth = (int)Math.ceil(Math.log(size) / Math.log(2.0));
		stack.getParameterPair("DEPTH").setValue(new HDLValue(depth));
		stack.getSignalForPort("address_b").setResetValue(HDLPreDefinedConstant.VECTOR_ZERO);
		stack.getSignalForPort("we_b").setResetValue(HDLPreDefinedConstant.LOW);
		return stack;
    }
	
    private Hashtable<Integer, SequencerState> genStatemachine(SchedulerBoard board, HardwareResource resource, Hashtable<Integer, SequencerState> returnTable, Hashtable<HDLVariable, HDLInstance> callStackMap){
		HDLSequencer seq = hm.newSequencer(board.getName() + "_method");
		IdGen id = new IdGen("S");
		Hashtable<Integer, SequencerState> states = new Hashtable<>();
		
		HDLVariable busy_port_sig = null, req_flag = null, req_flag_d = null, req_flag_edge = null;
		HDLInstance call_stack = hm.getModuleInstance(board.getName() + "_call_stack_memory");

		if(board.isAuto() == false){
			req_flag = varTable.get(board.getName() + "_req_flag");
			busy_port_sig = varTable.get(board.getName() + "_busy");
			
			req_flag_d = hm.newSignal(req_flag.getName() + "_d", req_flag.getType());
			req_flag_d.setAssignForSequencer(seq, req_flag);
			
			req_flag_edge = hm.newSignal(req_flag.getName() + "_edge", req_flag.getType());
			req_flag_edge.setAssign(null, hm.newExpr(HDLOp.AND, req_flag, hm.newExpr(HDLOp.NOT, req_flag_d)));
		}
		
		SequencerState methodEntryState = null;
		SequencerState methodIdleState = null;
		
		for(SchedulerSlot slot: board.getSlots()){
			states.put(slot.getStepId(), seq.addSequencerState(id.get(slot.getStepId())));
		}
		for(SchedulerSlot slot: board.getSlots()){
			if(slot.hasBranchOp() || slot.getNextStep().length > 1 || slot.getLatency() > 0) continue;
			if(slot.getStepId() == 0) continue;
			states.get(slot.getStepId()).addStateTransit(states.get(slot.getNextStep()[0]));
		}

		for(SchedulerSlot slot: board.getSlots()){
			for(SchedulerItem item: slot.getItems()){
				SequencerState s = states.get(item.getStepId());
				switch(item.getOp()){
				case METHOD_EXIT: {
					HDLExpr unlock = null;
					HDLExpr wait_with_unlock = null;

					if(board.hasWaitDependsBoard()){ // must wait for other method, such as join
						HDLVariable flag = varTable.get(board.getWaitDependsBoardName() + "_busy");
						wait_with_unlock = hm.newExpr(HDLOp.EQ, flag, HDLPreDefinedConstant.LOW); // the waiting method has been done.
						unlock = wait_with_unlock;
					}
					
					if(board.hasCallStack()){
						HDLExpr stack_bottom = hm.newExpr(HDLOp.EQ, call_stack.getSignalForPort("address_b"), HDLPreDefinedConstant.INTEGER_ZERO);
						// unlock is updated with this stack bottom condition 
						unlock = (unlock == null) ? stack_bottom : hm.newExpr(HDLOp.AND, stack_bottom, unlock);
					}
					
					if(unlock != null){
						s.addStateTransit(unlock, states.get(item.getSlot().getNextStep()[0]));
					}else{
						s.addStateTransit(states.get(item.getSlot().getNextStep()[0]));
					}
					
					if(board.isAuto() == false){
						if(unlock != null){
							busy_port_sig.setAssign(s, hm.newExpr(HDLOp.IF, unlock, HDLPreDefinedConstant.LOW, HDLPreDefinedConstant.HIGH));
						}else{
							busy_port_sig.setAssign(s, HDLPreDefinedConstant.LOW);
						}
					}

					methodIdleState = s;
					break;
				}
				case METHOD_ENTRY:{
					if(board.isAuto()){
						s.addStateTransit(states.get(item.getSlot().getNextStep()[0]));
					}else{
						s.addStateTransit(hm.newExpr(HDLOp.OR, req_flag, req_flag_d), states.get(item.getSlot().getNextStep()[0]));
						//s.addStateTransit(req_flag, states.get(item.getSlot().getNextStep()[0]));
						busy_port_sig.setAssign(s, hm.newExpr(HDLOp.OR, req_flag, req_flag_d));
					}
					methodEntryState = s;
					break;
				}
				case SELECT:{
					SelectItem item0 = (SelectItem)item;
					for(int i = 0; i < item0.pat.length; i++){
						HDLExpr cond = convOperandToHDLExpr(item, item0.target);
						HDLExpr pat = convOperandToHDLExpr(item, item0.pat[i]);
						s.addStateTransit(hm.newExpr(HDLOp.EQ, cond, pat), states.get(item.getSlot().getNextStep()[i]));
					}
					s.addStateTransit(states.get(item0.getSlot().getNextStep()[item0.pat.length]));
					break;
				}
				case JT:{
					HDLExpr flag = convOperandToHDLExpr(item, item.getSrcOperand()[0]);
					s.addStateTransit(hm.newExpr(HDLOp.EQ, flag, HDLPreDefinedConstant.HIGH), states.get(item.getSlot().getNextStep()[0]));
					s.addStateTransit(hm.newExpr(HDLOp.EQ, flag, HDLPreDefinedConstant.LOW), states.get(item.getSlot().getNextStep()[1]));
					break;
				}
				case JP:
					s.addStateTransit(states.get(item.getSlot().getNextStep()[0]));
					break;
				case CALL:
				case EXT_CALL:
					{
						SequencerState call_body = seq.addSequencerState(s.getStateId().getValue()+"_body", false);
						SequencerState call_wait = seq.addSequencerState(s.getStateId().getValue()+"_wait", false);
						MethodInvokeItem item0 = (MethodInvokeItem)item;
						HDLVariable call_req, call_busy;
						String flag_name;
						if(item0.getOp() == Op.EXT_CALL){
							HDLInstance obj = (HDLInstance)(varTable.get(item0.obj.getName()));
							call_req = obj.getSignalForPort(item0.name + "_req");
							call_busy = obj.getSignalForPort(item0.name + "_busy");
							//flag_name = String.format("%s_ext_call_flag_%04d", obj.getName(), item.getStepId());
							flag_name = String.format("%s_%s_ext_call_flag_%04d", obj.getName(), item0.name, item.getStepId());
						}else{
							call_req = varTable.get(item0.name + "_req_local");
							call_busy = varTable.get(item0.name + "_busy");
							flag_name = String.format("%s_call_flag_%04d", item0.name, item.getStepId());
						}
						HDLSignal flag = (HDLSignal)varTable.get(flag_name);
						if(flag == null){
							flag = hm.newSignal(flag_name, HDLPrimitiveType.genBitType(), HDLSignal.ResourceKind.WIRE);
							flag.setAssign(null, hm.newExpr(HDLOp.EQ,
															hm.newExpr(HDLOp.AND,
																	   hm.newExpr(HDLOp.EQ, call_busy, HDLPreDefinedConstant.LOW),
																	   hm.newExpr(HDLOp.EQ, call_req, HDLPreDefinedConstant.LOW)),
															HDLPreDefinedConstant.HIGH));
							varTable.put(flag_name, flag);
						}
					
						// when busy = '0', s -> call_body
						// recur					
						//					s.addStateTransit(hm.newExpr(HDLOp.EQ, call_busy, HDLPreDefinedConstant.LOW), call_body);
						s.addStateTransit(call_body);
					
						// call_body
						//					call_req.setAssign(call_body, 0, HDLPreDefinedConstant.HIGH);
						call_req.setAssign(call_body, HDLPreDefinedConstant.HIGH);
						call_req.setDefaultValue(HDLPreDefinedConstant.LOW); // otherwise '0'
						//					call_body.setMaxConstantDelay(1);
						if(item0.isNoWait() == true){
							//System.out.println("no wait:" + call_req);
							call_body.addStateTransit(states.get(item.getSlot().getNextStep()[0]));
						}else{
							//						call_body.setStateExitFlag(flag);
							call_body.addStateTransit(call_wait);
							call_wait.addStateTransit(flag, states.get(item.getSlot().getNextStep()[0]));
							//						call_wait.setStateExitFlag(flag);
						}
						//					call_body.addStateTransit(states.get(item.getSlot().getNextStep()[0]));
						if(call_stack != null && board.getName().equals(item0.name)){
							//System.out.println("treating recursive call.");
							HDLSignal addr = call_stack.getSignalForPort("address_b");
							HDLSignal we = call_stack.getSignalForPort("we_b");
							HDLSignal wdata = call_stack.getSignalForPort("din_b");
							we.setAssign(s, HDLPreDefinedConstant.HIGH);
							we.setDefaultValue(HDLPreDefinedConstant.LOW); // others
							int retPoint = item.getStepId();
							HDLValue retPointValue = new HDLValue(String.valueOf(retPoint), HDLPrimitiveType.genSignedType(16));
							wdata.setAssign(s, retPointValue);
							HDLExpr addr_expr = hm.newExpr(HDLOp.ADD, addr, HDLPreDefinedConstant.INTEGER_ONE);
							addr.setAssign(s, addr_expr);
							Enumeration<HDLVariable> preserve = callStackMap.keys();
							while(preserve.hasMoreElements()){
								HDLVariable src = preserve.nextElement();
								HDLInstance inst = callStackMap.get(src);
								HDLSignal a = inst.getSignalForPort("address_b");
								HDLSignal w = inst.getSignalForPort("we_b");
								HDLSignal d = inst.getSignalForPort("din_b");
								w.setAssign(s, HDLPreDefinedConstant.HIGH);
								w.setDefaultValue(HDLPreDefinedConstant.LOW); // others
								d.setAssign(s, src);
								a.setAssign(s, addr_expr);
							}
						
							SequencerState call_ret = seq.addSequencerState(s.getStateId().getValue()+"_ret", false);
							call_ret.addStateTransit(states.get(item.getSlot().getNextStep()[0]));
							returnTable.put(retPoint, call_ret);
						}
					
						break;
					}
				case LSHIFT32 :
				case LOGIC_RSHIFT32 :
				case ARITH_RSHIFT32 :
				case LSHIFT64 :
				case LOGIC_RSHIFT64 :
				case ARITH_RSHIFT64 :
				case MUL32 :
				case MUL64 :
				case DIV32 :
				case DIV64 :
				case MOD32 :
				case MOD64 :
				case FADD32 :
				case FSUB32 :
				case FMUL32 :
				case FDIV32 :
				case FADD64 :
				case FSUB64 :
				case FMUL64 :
				case FDIV64 :
				case CONV_F2I :
				case CONV_I2F :
				case CONV_D2L :
				case CONV_L2D :
				case CONV_F2D :
				case CONV_D2F :
				case FLT32:
				case FLEQ32:
				case FGT32:
				case FGEQ32:
				case FCOMPEQ32:
				case FNEQ32:
				case FLT64:
				case FLEQ64:
				case FGT64:
				case FGEQ64:
				case FCOMPEQ64:
				case FNEQ64:
					{
						s.setMaxConstantDelay(item.getOp().latency);
						s.addStateTransit(states.get(item.getSlot().getNextStep()[0]));
						HDLInstance inst = getOperationUnit(item.getOp(), resource, board.getName());
						s.setStateExitFlag(inst.getSignalForPort("valid"));
						break;
					}
				case ARRAY_ACCESS:{
					states.get(slot.getStepId()).addStateTransit(states.get(slot.getNextStep()[0]));
					break;
				}
				case RETURN:{
					states.get(slot.getStepId()).addStateTransit(states.get(slot.getNextStep()[0]));
					break;
				}
				case MULTI_RETURN:{
					// TODO
					states.get(slot.getStepId()).addStateTransit(states.get(slot.getNextStep()[0]));
					break;
				}
				default:
					/*
					if(slot.hasBranchOp() || slot.getNextStep().length > 1 || slot.getLatency() > 0){
						SynthesijerUtils.warn("Undefined state transition: " + item.getOp());
					}
					*/
					states.get(slot.getStepId()).addStateTransit(states.get(slot.getNextStep()[0]));
					/*
					  if(item.isBranchOp()){
					  }else{
					  s.addStateTransit(states[item.getStepId()+1]);
					  }
					*/
					//s.addStateTransit(states[item.getBranchId()[0]]);
					//s.addStateTransit(states.get(item.getSlot().getNextStep()[0]));
				}
			}
		}
		seq.getIdleState().addStateTransit(states.get(0));
		
		if(board.isAuto() == false){
			if(methodEntryState != null && methodIdleState != null){
				HDLSignal sig = methodEntryState.getKey();  
				sig.setAssignForSequencer(seq,
										  hm.newExpr(HDLOp.AND, hm.newExpr(HDLOp.NEQ, sig, methodIdleState.getStateId()),
													 hm.newExpr(HDLOp.AND, hm.newExpr(HDLOp.NEQ, sig, methodEntryState.getStateId()),
																req_flag_edge)),
										  methodEntryState.getStateId());
			}
		}

		if(board.hasCallStack()){
			HDLExpr unlock = null;
			
			if(board.hasWaitDependsBoard()){ // must wait for other method, such as join
				HDLVariable flag = varTable.get(board.getWaitDependsBoardName() + "_busy");
				unlock = hm.newExpr(HDLOp.EQ, flag, HDLPreDefinedConstant.LOW); // the waiting method has been done.
			}
			Enumeration<Integer> i = returnTable.keys();
			HDLSignal top = call_stack.getSignalForPort("dout_b");
			HDLExpr stack_bottom = hm.newExpr(HDLOp.EQ, call_stack.getSignalForPort("address_b"), HDLPreDefinedConstant.INTEGER_ZERO);
			HDLExpr not_stack_bottom = hm.newExpr(HDLOp.NOT, stack_bottom);
			while(i.hasMoreElements()){
				int nk = i.nextElement();
				SequencerState ns = returnTable.get(nk);
				HDLExpr cond = hm.newExpr(HDLOp.AND, not_stack_bottom, hm.newExpr(HDLOp.EQ, top, new HDLValue(String.valueOf(nk), HDLPrimitiveType.genSignedType(16))));
				cond = (unlock == null) ? cond : hm.newExpr(HDLOp.AND, unlock, cond);
				methodIdleState.addStateTransit(cond, ns);
			}

			HDLSignal addr = call_stack.getSignalForPort("address_b");
			HDLExpr dec = hm.newExpr(HDLOp.SUB, addr, HDLPreDefinedConstant.INTEGER_ONE);
			if(unlock != null){
				addr.setAssign(methodIdleState, hm.newExpr(HDLOp.AND, not_stack_bottom, unlock), dec);
			}else{
				addr.setAssign(methodIdleState, not_stack_bottom, dec);
			}
			
			Enumeration<HDLVariable> preserve = callStackMap.keys();
			while(preserve.hasMoreElements()){
				HDLVariable src = preserve.nextElement();
				HDLInstance inst = callStackMap.get(src);
				HDLSignal a = inst.getSignalForPort("address_b");
				HDLSignal d = inst.getSignalForPort("dout_b");
				if(unlock != null){
					src.setAssign(methodIdleState, hm.newExpr(HDLOp.AND, not_stack_bottom, unlock), d);
					a.setAssign(methodIdleState, hm.newExpr(HDLOp.AND, not_stack_bottom, unlock), dec);
				}else{
					src.setAssign(methodIdleState, not_stack_bottom, d);
					a.setAssign(methodIdleState, not_stack_bottom, dec);
				}
			}
			
		}

		return states;
    }

    private HDLInstance newInstModule(String mName, String uName){
		SynthesijerModuleInfo info = Manager.INSTANCE.searchHDLModuleInfo(mName);
		HDLInstance inst = hm.newModuleInstance(info.getHDLModule(), uName);
		inst.getSignalForPort("clk").setAssign(null, hm.getSysClk().getSignal());
		inst.getSignalForPort("reset").setAssign(null, hm.getSysReset().getSignal());
		return inst;
    }
	
    private HDLInstance getOperationUnit(Op op, HardwareResource resource, String name){
		switch(op){
		case MUL32:{
			if(resource.mul32 == null) resource.mul32 = newInstModule("MUL32", "u_synthesijer_mul32" + "_" + name);
			return resource.mul32;
		}
		case MUL64:{
			if(resource.mul64 == null) resource.mul64 = newInstModule("MUL64", "u_synthesijer_mul64" + "_" + name);
			return resource.mul64;
		}
		case DIV32:
		case MOD32:{
			if(resource.div32 == null){
				resource.div32 = newInstModule("DIV32", "u_synthesijer_div32" + "_" + name);
				resource.div32.getSignalForPort("b").setResetValue(HDLUtils.newValue(1, 32));
			}
			return resource.div32;
		}
		case DIV64:
		case MOD64:{
			if(resource.div64 == null){
				resource.div64 = newInstModule("DIV64", "u_synthesijer_div64" + "_" + name);
				resource.div64.getSignalForPort("b").setResetValue(HDLUtils.newValue(1, 64));
			}
			return resource.div64;
		}
		case FADD32:{
			if(resource.fadd32 == null) resource.fadd32 = newInstModule("FADD32", "u_synthesijer_fadd32" + "_" + name);
			return resource.fadd32;
		}
		case FSUB32:{
			if(resource.fsub32 == null) resource.fsub32 = newInstModule("FSUB32", "u_synthesijer_fsub32" + "_" + name);
			return resource.fsub32;
		}
		case FMUL32:{
			if(resource.fmul32 == null) resource.fmul32 = newInstModule("FMUL32", "u_synthesijer_fmul32" + "_" + name);
			return resource.fmul32;
		}
		case FDIV32:{
			if(resource.fdiv32 == null) resource.fdiv32 = newInstModule("FDIV32", "u_synthesijer_fdiv32" + "_" + name);
			return resource.fdiv32;
		}
		case FADD64:{
			if(resource.fadd64 == null) resource.fadd64 = newInstModule("FADD64", "u_synthesijer_fadd64" + "_" + name);
			return resource.fadd64;
		}
		case FSUB64:{
			if(resource.fsub64 == null) resource.fsub64 = newInstModule("FSUB64", "u_synthesijer_fsub64" + "_" + name);
			return resource.fsub64;
		}
		case FMUL64:{
			if(resource.fmul64 == null) resource.fmul64 = newInstModule("FMUL64", "u_synthesijer_fmul64" + "_" + name);
			return resource.fmul64;
		}
		case FDIV64:{
			if(resource.fdiv64 == null) resource.fdiv64 = newInstModule("FDIV64", "u_synthesijer_fdiv64" + "_" + name);
			return resource.fdiv64;
		}
		case CONV_F2I:{
			if(resource.f2i == null) resource.f2i = newInstModule("FCONV_F2I", "u_synthesijer_fconv_f2i" + "_" + name);
			return resource.f2i;
		}
		case CONV_I2F:{
			if(resource.i2f == null) resource.i2f = newInstModule("FCONV_I2F", "u_synthesijer_fconv_i2f" + "_" + name);
			return resource.i2f;
		}
		case CONV_L2D:{
			if(resource.l2d == null) resource.l2d = newInstModule("FCONV_L2D", "u_synthesijer_fconv_l2d" + "_" + name);
			return resource.l2d;
		}
		case CONV_D2L:{
			if(resource.d2l == null) resource.d2l = newInstModule("FCONV_D2L", "u_synthesijer_fconv_d2l" + "_" + name);
			return resource.d2l;
		}
		case CONV_F2D:{
			if(resource.f2d == null) resource.f2d = newInstModule("FCONV_F2D", "u_synthesijer_fconv_f2d" + "_" + name);
			return resource.f2d;
		}
		case CONV_D2F:{
			if(resource.d2f == null) resource.d2f = newInstModule("FCONV_D2F", "u_synthesijer_fconv_d2f" + "_" + name);
			return resource.d2f;
		}
		case LSHIFT32:{
			if(resource.lshift32 == null) resource.lshift32 = newInstModule("LSHIFT32", "u_synthesijer_lshift32" + "_" + name);
			return resource.lshift32;
		}
		case LOGIC_RSHIFT32:{
			if(resource.logic_rshift32 == null) resource.logic_rshift32 = newInstModule("LOGIC_RSHIFT32", "u_synthesijer_logic_rshift32" + "_" + name);
			return resource.logic_rshift32;
		}
		case ARITH_RSHIFT32:{
			if(resource.arith_rshift32 == null) resource.arith_rshift32 = newInstModule("ARITH_RSHIFT32", "u_synthesijer_arith_rshift32" + "_" + name);
			return resource.arith_rshift32;
		}
		case LSHIFT64:{
			if(resource.lshift64 == null) resource.lshift64 = newInstModule("LSHIFT64", "u_synthesijer_lshift64" + "_" + name);
			return resource.lshift64;
		}
		case LOGIC_RSHIFT64:{
			if(resource.logic_rshift64 == null) resource.logic_rshift64 = newInstModule("LOGIC_RSHIFT64", "u_synthesijer_logic_rshift64" + "_" + name);
			return resource.logic_rshift64;
		}
		case ARITH_RSHIFT64:{
			if(resource.arith_rshift64 == null) resource.arith_rshift64 = newInstModule("ARITH_RSHIFT64", "u_synthesijer_arith_rshift64" + "_" + name);
			return resource.arith_rshift64;
		}
		case FLT32:
		case FLEQ32:
		case FGT32:
		case FGEQ32:
		case FCOMPEQ32:
		case FNEQ32:{
			if(resource.fcomp32 == null) resource.fcomp32 = newInstModule("FCOMP32", "u_synthesijer_fcomp32" + "_" + name);
			return resource.fcomp32;
		}
		case FLT64:
		case FLEQ64:
		case FGT64:
		case FGEQ64:
		case FCOMPEQ64:
		case FNEQ64:{
			if(resource.fcomp64 == null) resource.fcomp64 = newInstModule("FCOMP64", "u_synthesijer_fcomp64" + "_" + name);
			return resource.fcomp64;
		}
		default: return null;
		}
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
