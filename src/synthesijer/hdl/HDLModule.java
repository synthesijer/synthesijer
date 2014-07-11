package synthesijer.hdl;

import java.io.PrintWriter;
import java.util.ArrayList;

import synthesijer.hdl.expr.HDLCombinationExpr;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.hdl.verilog.GenerateVerilogVisitor;
import synthesijer.hdl.vhdl.GenerateVHDLVisitor;

public class HDLModule implements HDLTree{
	
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
	
	private HDLPort sysClk; 
	private HDLPort sysReset; 
	
	public HDLModule(String name, String sysClkName, String sysResetName, boolean syncFlag){
		this.name = name;
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
	
	HDLModule(String name){
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
		
	public HDLParameter newParameter(String name, HDLPrimitiveType type, String defaultValue, String value){
		HDLParameter param = new HDLParameter(name, type, defaultValue, value);
		parameters.add(param);
		return param;
	}
	
	public HDLParameter[] getParameters(){
		return parameters.toArray(new HDLParameter[]{});
	}

	public HDLPort newPort(String name, HDLPort.DIR dir, HDLType type){
		HDLPort port = new HDLPort(this, name, dir, type);
		ports.add(port);
		return port;
	}
	
	public HDLPort[] getPorts(){
		return ports.toArray(new HDLPort[]{});
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

	public HDLExpr newExpr(HDLOp op, HDLSignal arg0, int value){
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
	
	public HDLInstance[] getModuleInstances(){
		return submodules.toArray(new HDLInstance[]{});
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

}
