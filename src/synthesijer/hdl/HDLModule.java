package synthesijer.hdl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Hashtable;

import synthesijer.SynthesijerComponent;
import synthesijer.hdl.expr.HDLCombinationExpr;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.hdl.verilog.GenerateVerilogVisitor;
import synthesijer.hdl.vhdl.GenerateVHDLVisitor;

public class HDLModule implements HDLTree, SynthesijerComponent{
	
	private final String name;
	private final String sysClkName;
	private final String sysResetName;
	private final boolean syncronousFlag;
	
	private ArrayList<HDLPort> ports = new ArrayList<>();
	private ArrayList<HDLSignal> signals = new ArrayList<>();
	private ArrayList<HDLSequencer> sequencer = new ArrayList<>();
	private ArrayList<HDLUserDefinedType> usertype = new ArrayList<>();
	private ArrayList<HDLInstance> submodules = new ArrayList<>();
	private ArrayList<HDLExpr> exprs = new ArrayList<>();
	private ArrayList<HDLParameter> parameters = new ArrayList<>();
	private ArrayList<HDLSignalBinding> bindings = new ArrayList<>();
	private final Hashtable<String, LibrariesInfo> libraries = new Hashtable<>();
	
	private HDLPort sysClk; 
	private HDLPort sysReset;
	
	private boolean negativeResetFlag = false;
	
	private boolean componentDeclRequired = true;
	
	public HDLModule(String name, String sysClkName, String sysResetName, boolean syncFlag){
		this.name = name.replace('.', '_');
		this.sysClkName = sysClkName;
		this.sysResetName = sysResetName;
		if(syncFlag){
			sysClk = newPort(sysClkName, HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		}
		if(syncFlag){
			sysReset = newPort(sysResetName, HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		}
		syncronousFlag = syncFlag;
	}
	
	public HDLModule(String name, String sysClkName, String sysResetName){
		this(name, sysClkName, sysResetName, true);
	}
	
	public HDLModule(String name){
		this(name, "", "", false);
	}
	
	public HDLPort getSysClk(){
		return sysClk;
	}

	public HDLPort getSysReset(){
		return sysReset;
	}

	public boolean isSynchronous(){
		return syncronousFlag;
	}
	
	public String getName(){
		return name;
	}
		
	public HDLParameter newParameter(String name, HDLPrimitiveType type, HDLValue defaultValue){
		HDLParameter param = new HDLParameter(name, type, defaultValue, defaultValue);
		parameters.add(param);
		return param;
	}
	
        public HDLParameter newParameter(String name, int defaultValue){
	    HDLParameter param = newParameter(name,
					      (HDLPrimitiveType)HDLPrimitiveType.genIntegerType(),
					      new HDLValue(String.valueOf(defaultValue), HDLPrimitiveType.genIntegerType()));
	    return param;
	}

        public HDLParameter getParamterByName(String name){
		for(HDLParameter p: parameters){
			if(p.getName().equals(name)) return p;
		}
		return null;
	}
	
	public HDLParameter[] getParameters(){
		return parameters.toArray(new HDLParameter[]{});
	}

	public HDLPort newPort(String name, String wire, HDLPort.DIR dir, HDLType type, EnumSet<HDLPort.OPTION> opt){
		HDLPort port = new HDLPort(this, name, wire, dir, type, opt);
		ports.add(port);
		return port;
	}

	public HDLPort newPort(String name, String wire, HDLPort.DIR dir, HDLType type){
		HDLPort port = new HDLPort(this, name, wire, dir, type, EnumSet.noneOf(HDLPort.OPTION.class));
		ports.add(port);
		return port;
	}

	public HDLPort newPort(String name, HDLPort.DIR dir, HDLType type, EnumSet<HDLPort.OPTION> opt){
		HDLPort port = new HDLPort(this, name, dir, type, opt);
		ports.add(port);
		return port;
	}
	
	public HDLPort newPort(String name, HDLPort.DIR dir, HDLType type){
		return newPort(name, dir, type, EnumSet.noneOf(HDLPort.OPTION.class));
	}
	
	public HDLPort[] getPorts(){
		return ports.toArray(new HDLPort[]{});
	}
	
	public HDLPort getPort(String name){
		for(HDLPort port: ports){
			if(port.getName().equals(name)) return port;
		}
		return null;
	}

	public HDLSignal newSignal(String name, HDLType type, HDLSignal.ResourceKind kind, HDLExpr equivExpr, boolean equivFlag){
		HDLSignal sig = new HDLSignal(this, name, type, kind, equivExpr, equivFlag);
		signals.add(sig);
		return sig;
	}

	public HDLSignal newSignal(String name, HDLType type, HDLSignal.ResourceKind kind){
		HDLSignal sig = new HDLSignal(this, name, type, kind);
		signals.add(sig);
		return sig;
	}

	public HDLSignal newSignal(String name, HDLType type){
		HDLSignal sig = new HDLSignal(this, name, type, HDLSignal.ResourceKind.REGISTER);
		signals.add(sig);
		return sig;
	}
	
	public HDLSignal newTmpSignal(HDLType type, HDLSignal.ResourceKind kind){
		HDLSignal sig = new HDLSignal(this, String.format("tmp_%04d", getExprUniqueId()), type, kind);
		signals.add(sig);
		return sig;
	}

	public HDLSignal getSignal(String name){
		for(HDLSignal sig: signals){
			if(sig.getName().equals(name)) return sig;
		}
		return null;
	}
	
	public void rmSignal(HDLSignal sig){
		signals.remove(sig);
	}
	
	public HDLUserDefinedType newUserDefinedType(String base, String[] items, int defaultIndex){
		HDLUserDefinedType t = new HDLUserDefinedType(base, items, defaultIndex);
		usertype.add(t);
		return t;
	}
	
	public HDLExpr newExpr(HDLOp op, HDLExpr arg0, HDLExpr arg1, HDLExpr arg2){
		HDLExpr expr = new HDLCombinationExpr(this, getExprUniqueId(), op, arg0, arg1, arg2);
		exprs.add(expr);
		return expr;
	}

	public HDLExpr newExpr(HDLOp op, HDLExpr arg){
		HDLExpr expr = new HDLCombinationExpr(this, getExprUniqueId(), op, arg);
		exprs.add(expr);
		return expr;
	}

	public HDLExpr newExpr(HDLOp op, HDLExpr arg0, int value){
		return newExpr(op, arg0, new HDLValue(String.valueOf(value), HDLPrimitiveType.genIntegerType()));
	}
	
	public HDLExpr[] getExprs(){
		return exprs.toArray(new HDLExpr[]{});
	}

	public HDLInstance newModuleInstance(HDLModule m, String n){
		HDLInstance obj = new HDLInstance(this, n, m);
		submodules.add(obj);
		return obj;
	}

	public HDLInstance newModuleInstance(HDLModule m){
		HDLInstance obj = new HDLInstance(this, String.format("sjr_generate_U_%04d", getExprUniqueId()), m);
		submodules.add(obj);
		return obj;
	}

	public HDLInstance[] getModuleInstances(){
		return submodules.toArray(new HDLInstance[]{});
	}

	public HDLInstance getModuleInstance(String key){
		for(HDLInstance inst: submodules){
			if(inst.getName().equals(key)){
				return inst;
			}
		}
		return null;
	}

	private int uniqId = 1;
	
	public int getExprUniqueId(){
		int tmp = uniqId;
		uniqId++;
		return tmp;
	}
		
	public HDLExpr newExpr(HDLOp op, HDLExpr arg0, HDLExpr arg1){
		HDLExpr expr = null;
		expr = new HDLCombinationExpr(this, getExprUniqueId(), op, arg0, arg1);
		exprs.add(expr);
		return expr;
	}
	
	public HDLSignal[] getSignals(){
		return signals.toArray(new HDLSignal[]{});
	}

	public String getSysClkName(){
		return sysClkName;
	}

	public String getSysResetName(){
		return sysResetName;
	}
	
	public HDLSequencer newSequencer(String key){
		HDLSequencer s = new HDLSequencer(this, key);
		sequencer.add(s);
		return s;
	}
	
	public HDLSequencer[] getSequencers(){
		return sequencer.toArray(new HDLSequencer[]{});
	}
	
	public HDLSequencer getSequencer(String name){
		for(HDLSequencer s: sequencer){
			if(s.getStateKey().getName().equals(name)) return s;
		}
		return null;
	}
	
	public void addSignalBinding(HDLSignalBinding binding){
		bindings.add(binding);
	}
	
	public HDLSignalBinding[] getSignalBindings(){
		return bindings.toArray(new HDLSignalBinding[0]);
	}
	
	public void genVHDL(PrintWriter dest){
		accept(new GenerateVHDLVisitor(dest, 0));
	}
	
	public void genVerilogHDL(PrintWriter dest){
		accept(new GenerateVerilogVisitor(dest, 0));
	}

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLModule(this);
	}
	
	public boolean isNegativeReset(){
		return negativeResetFlag;
	}

	public void setNegativeReset(boolean flag){
		negativeResetFlag = flag;
	}
		
	public void setComponentDeclRequired(boolean f){
		this.componentDeclRequired = f;
	}

	public boolean isComponentDeclRequired(){
		return this.componentDeclRequired;
	}

	public void addLibraryUse(String key, String use){
		LibrariesInfo info;
		if(libraries.containsKey(key)){
			info = libraries.get(key);
		}else{
			info = new LibrariesInfo(key);
			libraries.put(key, info);
		}
		if(info.useName.contains(use) == false){
			info.useName.add(use);
		}
	}
	
	public LibrariesInfo[] getLibraries(){
		return libraries.values().toArray(new LibrariesInfo[]{});
	}
	
	public class LibrariesInfo{
		public final String libName;
		public final ArrayList<String> useName = new ArrayList<>();
		
		public LibrariesInfo(String libName){
			this.libName = libName;
		}
	}
	
}
