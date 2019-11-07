package synthesijer.hdl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Hashtable;

import synthesijer.SynthesijerComponent;
import synthesijer.SynthesijerUtils;
import synthesijer.hdl.expr.HDLCombinationExpr;
import synthesijer.hdl.expr.HDLPhiExpr;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.hdl.verilog.GenerateVerilogVisitor;
import synthesijer.hdl.vhdl.GenerateVHDLVisitor;
import synthesijer.hdl.sequencer.SequencerState;

/**
 * HDLModule: The top module for HDL-side AST.
 * This module corresponds to VHDL-entity and Verilog-HDL module
 */
public class HDLModule implements HDLTree, SynthesijerComponent{

	/**
	 * name of this module
	 */
	private final String name;

	/**
	 * name of clock signal of this module
	 */
	private final String sysClkName;

	/**
	 * name of reset signal of this module
	 */
	private final String sysResetName;

	/**
	 * the flag whether this module is synchronous module or not
	 */
	private final boolean synchronousFlag;

	/**
	 * A collection of input/output ports of this module
	 */
	private ArrayList<HDLPort> ports = new ArrayList<>();

	/**
	 * A collection of signals, corresponding signals for VHDL and wire/reg variables for Verilog-HDL)
	 */
	private ArrayList<HDLSignal> signals = new ArrayList<>();

	/**
	 * State-machine sequencers, corresponding process blocks for VHDL and always block for Verilog-HDL)
	 */
	private ArrayList<HDLSequencer> sequencer = new ArrayList<>();

	/**
	 * user type definitions
	 */
	private ArrayList<HDLUserDefinedType> usertype = new ArrayList<>();

	/**
	 * A collection of sub modules, which are instanced in this module.
	 */
	private ArrayList<HDLInstance> submodules = new ArrayList<>();
	/**
	 * A collection of asynchronous expression
	 */
	private ArrayList<HDLExpr> exprs = new ArrayList<>();
	/**
	 * Parameters
	 */
	private ArrayList<HDLParameter> parameters = new ArrayList<>();
	/**
	 * Signal bindings
	 */
	private ArrayList<HDLSignalBinding> bindings = new ArrayList<>();
	/**
	 * A table to manage libraries by its name
	 */
	private final Hashtable<String, LibrariesInfo> libraries = new Hashtable<>();

	/**
	 * clock signal
	 */
	private HDLPort sysClk;
	/**
	 * reset signal
	 */
	private HDLPort sysReset;
	/**
	 * negative reset flag. When this flag is true, reset signal will be treated as negative signal.
	 */
	private boolean negativeResetFlag = false;
	/**
	 * requirement of component declaration
	 */
	private boolean componentDeclRequired = true;

	/**
	 * Base constructor of HDLModule
	 * @param name module name
	 * @param sysClkName name of clock signal
	 * @param sysResetName name of reset signal
	 * @param syncFlag synchronous flag
	 */
	private HDLModule(String name, String sysClkName, String sysResetName, boolean syncFlag){
		this.name = name.replace('.', '_');
		this.synchronousFlag = syncFlag;
		this.sysClkName = sysClkName;
		this.sysResetName = sysResetName;
		if(syncFlag){
			sysClk = newPort(sysClkName, HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		}
		if(syncFlag){
			sysReset = newPort(sysResetName, HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		}
	}

	/**
	 * Constructor of synchronous module
	 * @param name module name
	 * @param sysClkName name of clock signal
	 * @param sysResetName name of reset signal
	 */
	public HDLModule(String name, String sysClkName, String sysResetName){
		this(name, sysClkName, sysResetName, true);
	}

	/**
	 * Constructor of asynchronous module
	 * @param name module name
	 */
	public HDLModule(String name){
		this(name, "", "", false);
	}

	/**
	 * @return clock signal
	 */
	public HDLPort getSysClk(){
		return sysClk;
	}

	/**
	 * @return clock signal port pair
	 */
	public HDLPortPairItem getSysClkPairItem(){
		return sysClk;
	}

	/**
	 *
	 * @return reset signal
	 */
	public HDLPort getSysReset(){
		return sysReset;
	}

	/**
	 *
	 * @return reset signal port pair
	 */
	public HDLPortPairItem getSysResetPairItem(){
		return sysReset;
	}

	/**
	 *
	 * @return synchronous or not
	 */
	public boolean isSynchronous(){
		return synchronousFlag;
	}

	/**
	 *
	 * @return name of this module
	 */
	public String getName(){
		return name;
	}

	/**
	 * add a new parameter to this module
	 * @param name name of the parameter
	 * @param type type of the parameter
	 * @param defaultValue default value of the parameter
	 * @return created parameter
	 */
	public HDLParameter newParameter(String name, HDLPrimitiveType type, HDLValue defaultValue){
		HDLParameter param = new HDLParameter(name, type, defaultValue, defaultValue);
		parameters.add(param);
		return param;
	}

	/**
	 * add a new integer parameter to this module
	 * @param name name of the parameter
	 * @param defaultValue default value of the parameter
	 * @return created parameter
	 */
	public HDLParameter newParameter(String name, int defaultValue){
		HDLParameter param = newParameter(name,
				(HDLPrimitiveType)HDLPrimitiveType.genIntegerType(),
				new HDLValue(String.valueOf(defaultValue), HDLPrimitiveType.genIntegerType()));
		return param;
	}

	/**
	 * search and return parameter by the name
	 * @param name name of the parameter
	 * @return found parameter. return null when the parameter is not found.
	 */
	public HDLParameter getParameterByName(String name){
		for(HDLParameter p: parameters){
			if(p.getName().equals(name)) return p;
		}
		return null;
	}

	/**
	 *
	 * @return an array of all parameters.
	 */
	public HDLParameter[] getParameters(){
		return parameters.toArray(new HDLParameter[]{});
	}

	/**
	 * add a new input/output port into this module
	 * @param name the name of the port
	 * @param wire the kind of the port
	 * @param dir the direction of the port
	 * @param type the type of the port
	 * @param opt option flags for the port
	 * @return created port
	 */
	public HDLPort newPort(String name, String wire, HDLPort.DIR dir, HDLType type, EnumSet<HDLPort.OPTION> opt){
		HDLPort port = new HDLPort(this, name, wire, dir, type, opt);
		ports.add(port);
		return port;
	}

	/**
	 * add a new input/output port into this module
	 * @param name the name of the port
	 * @param wire the kind of the port
	 * @param dir the direction of the port
	 * @param type the type of the port
	 * @return created port
	 */
	public HDLPort newPort(String name, String wire, HDLPort.DIR dir, HDLType type){
		return newPort(name, wire, dir, type, EnumSet.noneOf(HDLPort.OPTION.class));
	}

	/**
	 * add a new input/output port into this module
	 * @param name the name of the port
	 * @param dir the direction of the port
	 * @param type the type of the port
	 * @param opt option flags for the port
	 * @return created port
	 */
	public HDLPort newPort(String name, HDLPort.DIR dir, HDLType type, EnumSet<HDLPort.OPTION> opt){
		HDLPort port = new HDLPort(this, name, dir, type, opt);
		ports.add(port);
		return port;
	}

	/**
	 * add a new input/output port into this module
	 * @param name the name of the port
	 * @param dir the direction of the port
	 * @param type the type of the port
	 * @return created port
	 */
	public HDLPort newPort(String name, HDLPort.DIR dir, HDLType type){
		return newPort(name, dir, type, EnumSet.noneOf(HDLPort.OPTION.class));
	}

	/**
	 *
	 * @return an array of all input/output ports
	 */
	public HDLPort[] getPorts(){
		return ports.toArray(new HDLPort[]{});
	}

	/**
	 * search and return a port with name
	 * @param name name of the port
	 * @return found port, null when the named port is not found.
	 */
	public HDLPort getPort(String name){
		for(HDLPort port: ports){
			if(port.getName().equals(name)) return port;
		}
		return null;
	}

	/**
	 * add a new signal into this module
	 * @param name name of the signal
	 * @param type type of the signal
	 * @param kind kind of the signal
	 * @param equivExpr equivalent expression with this signal
	 * @param equivFlag equivalent flag with this signal
	 * @return created signal
	 */
	public HDLSignal newSignal(String name, HDLType type, HDLSignal.ResourceKind kind, HDLExpr equivExpr, boolean equivFlag){
		HDLSignal sig = new HDLSignal(this, name, type, kind, equivExpr, equivFlag);
		signals.add(sig);
		return sig;
	}

	/**
	 * add a new signal into this module
	 * @param name name of the signal
	 * @param type type of the signal
	 * @param kind kind of the signal
	 * @return created signal
	 */
	public HDLSignal newSignal(String name, HDLType type, HDLSignal.ResourceKind kind){
		HDLSignal sig = new HDLSignal(this, name, type, kind);
		signals.add(sig);
		return sig;
	}

	/**
	 * add a new signal into this module
	 * @param name name of the signal
	 * @param type type of the signal
	 * @return created signal
	 */
	public HDLSignal newSignal(String name, HDLType type){
		HDLSignal sig = new HDLSignal(this, name, type, HDLSignal.ResourceKind.REGISTER);
		signals.add(sig);
		return sig;
	}

	/**
	 * add a new temporal signal into this module. The name of the signal is not required.
	 * @param type type of the signal
	 * @param kind kind of the signal
	 * @return created signal
	 */
	public HDLSignal newTmpSignal(HDLType type, HDLSignal.ResourceKind kind){
		HDLSignal sig = new HDLSignal(this, String.format("tmp_%04d", getExprUniqueId()), type, kind);
		signals.add(sig);
		return sig;
	}

	/**
	 * search and return a signal with name
	 * @param name name of the sinngal
	 * @return found signal. null when the signal is not found.
	 */
	public HDLSignal getSignal(String name){
		for(HDLSignal sig: signals){
			if(sig.getName().equals(name)) return sig;
		}
		return null;
	}

	/**
	 * remove a signal in this module
	 * @param sig the signal to remove
	 */
	public void rmSignal(HDLSignal sig){
		signals.remove(sig);
	}

	/**
	 * add a new user defined type
	 * @param base prefix of the defined type
	 * @param items items in the type
	 * @param defaultIndex default index of the items
	 * @return created user defined type
	 */
	public HDLUserDefinedType newUserDefinedType(String base, String[] items, int defaultIndex){
		HDLUserDefinedType t = new HDLUserDefinedType(base, items, defaultIndex);
		usertype.add(t);
		return t;
	}

	/**
	 * add a new expression in this module
	 * @param op operator
	 * @param arg0 argument 0
	 * @param arg1 argument 1
	 * @param arg2 argument 2
	 * @return created expression
	 */
	public HDLExpr newExpr(HDLOp op, HDLExpr arg0, HDLExpr arg1, HDLExpr arg2){
		HDLExpr expr = new HDLCombinationExpr(this, getExprUniqueId(), op, arg0, arg1, arg2);
		exprs.add(expr);
		return expr;
	}

	/**
	 * add a new expression of this module
	 * @param op operator
	 * @param arg argument
	 * @return created expression
	 */
	public HDLExpr newExpr(HDLOp op, HDLExpr arg){
		HDLExpr expr = new HDLCombinationExpr(this, getExprUniqueId(), op, arg);
		exprs.add(expr);
		return expr;
	}

	/**
	 * add a new expression of this module
	 * @param op operator
	 * @param arg0 argument
	 * @param value constant integer value
	 * @return created expression
	 */
	public HDLExpr newExpr(HDLOp op, HDLExpr arg0, int value){
		return newExpr(op, arg0, new HDLValue(String.valueOf(value), HDLPrimitiveType.genIntegerType()));
	}

	/**
	 *
	 * @return an array of all expressions in this module
	 */
	public HDLExpr[] getExprs(){
		return exprs.toArray(new HDLExpr[]{});
	}

	/**
	 * add a new module instance
	 * @param m module
	 * @param n name of the instance
	 * @param origName original name of the instance
	 * @return created module instance
	 */
	public HDLInstance newModuleInstance(HDLModule m, String n, String origName){
		HDLInstance obj = new HDLInstance(this, n, m, origName);
		submodules.add(obj);
		return obj;
	}

	/**
	 * add a new module instance
	 * @param m module
	 * @param n name of the instance
	 * @return created module instance
	 */
	public HDLInstance newModuleInstance(HDLModule m, String n){
		HDLInstance obj = new HDLInstance(this, n, m);
		submodules.add(obj);
		return obj;
	}

	/**
	 * add a new module instance without specific instance name
	 * @param m module
	 * @return created module instance
	 */
	public HDLInstance newModuleInstance(HDLModule m){
		return newModuleInstance(m, String.format("sjr_generate_U_%04d", getExprUniqueId()));
	}

	/**
	 *
	 * @return an array of all instances of sub-module in this module
	 */
	public HDLInstance[] getModuleInstances(){
		return submodules.toArray(new HDLInstance[]{});
	}

	/**
	 * search and return an instance of sub-module with the name
	 * @param key name
	 * @return found instance, null when an instance with the name is not found.
	 */
	public HDLInstance getModuleInstance(String key){
		for(HDLInstance inst: submodules){
			if(inst.getName().equals(key)){
				return inst;
			}
		}
		return null;
	}

	/**
	 * unique identifier number in this module
	 */
	private int uniqId = 1;

	/**
	 * get and update unique identifier number
	 * @return unique identifier number
	 */
	public int getExprUniqueId(){
		int tmp = uniqId;
		uniqId++;
		return tmp;
	}

	/**
	 * add a new expression
	 * @param op operator
	 * @param args arguments
	 * @return created new expression.
	 */
	public HDLPhiExpr newPhiExpr(HDLOp op, HDLExpr[] args, HDLExpr dest, SequencerState[] ss){
		HDLPhiExpr expr = null;
		if(op == HDLOp.PHI){
			expr = new HDLPhiExpr(this, getExprUniqueId(), op, dest, args, ss);
			exprs.add(expr);
		}else{
			SynthesijerUtils.error("HDLModule::newPhiExpr with args[] are not supported for " + op);
		}
		return expr;
	}
	
	/**
	 * add a new expression
	 * @param op operator
	 * @param arg0 argument 0
	 * @param arg1 argument 1
	 * @return created new expression.
	 */
	public HDLExpr newExpr(HDLOp op, HDLExpr arg0, HDLExpr arg1){
		HDLExpr expr = null;
		expr = new HDLCombinationExpr(this, getExprUniqueId(), op, arg0, arg1);
		exprs.add(expr);
		return expr;
	}

	/**
	 *
	 * @return an array of all signals in this module
	 */
	public HDLSignal[] getSignals(){
		return signals.toArray(new HDLSignal[]{});
	}

	/**
	 *
	 * @return the name of clock signal
	 */
	public String getSysClkName(){
		return sysClkName;
	}

	/**
	 *
	 * @return the name of reset signal
	 */
	public String getSysResetName(){
		return sysResetName;
	}

	/**
	 * add a new sequencer in this module
	 * @param key prefix of state name of this sequencer
	 * @return created sequencer
	 */
	public HDLSequencer newSequencer(String key){
		HDLSequencer s = new HDLSequencer(this, key);
		sequencer.add(s);
		return s;
	}

	/**
	 *
	 * @return an array of all sequencers in this module
	 */
	public HDLSequencer[] getSequencers(){
		return sequencer.toArray(new HDLSequencer[]{});
	}

	/**
	 * search and return the sequencer with given name
	 * @param name name of sequencer
	 * @return found sequencer, null when no such sequencer
	 */
	public HDLSequencer getSequencer(String name){
		for(HDLSequencer s: sequencer){
			if(s.getStateKey().getName().equals(name)) return s;
		}
		return null;
	}

	/**
	 * add signal binding
	 * @param binding the signal to bind
	 */
	public void addSignalBinding(HDLSignalBinding binding){
		bindings.add(binding);
	}

	/**
	 *
	 * @return an array of all signal bindings
	 */
	public HDLSignalBinding[] getSignalBindings(){
		return bindings.toArray(new HDLSignalBinding[0]);
	}

	/**
	 * traverse and print this module as VHDL format.
	 * @param dest output destination
	 */
	public void genVHDL(PrintWriter dest){
		accept(new GenerateVHDLVisitor(dest, 0));
	}

	/**
	 * traverse and print this module as Verilog-HDL format
	 * @param dest output destination
	 */
	public void genVerilogHDL(PrintWriter dest){
		accept(new GenerateVerilogVisitor(dest, 0));
	}

	/**
	 * Visitor-pattern method to print this mdoule
	 * @param v
	 */
	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLModule(this);
	}

	/**
	 *
	 * @return flag whether negative reset or not
	 */
	public boolean isNegativeReset(){
		return negativeResetFlag;
	}

	/**
	 * set flag to use negative reset in this module
	 * @param flag
	 */
	public void setNegativeReset(boolean flag){
		negativeResetFlag = flag;
	}

	/**
	 * set flag whether this module requires component declaration or not.
	 * @param f
	 */
	public void setComponentDeclRequired(boolean f){
		this.componentDeclRequired = f;
	}

	/**
	 *
	 * @return flag of component declaration required.
	 */
	public boolean isComponentDeclRequired(){
		return this.componentDeclRequired;
	}

	/**
	 * add using library
	 * @param key library key
n	 * @param use library name
	 */
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

	/**
	 *
	 * @return array of all libraries used in this module
	 */
	public LibrariesInfo[] getLibraries(){
		return libraries.values().toArray(new LibrariesInfo[]{});
	}

	/**
	 * Library information
	 */
	public class LibrariesInfo{
		public final String libName;
		public final ArrayList<String> useName = new ArrayList<>();

		public LibrariesInfo(String libName){
			this.libName = libName;
		}
	}

}
