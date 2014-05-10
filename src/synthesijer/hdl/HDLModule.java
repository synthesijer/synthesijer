package synthesijer.hdl;

import java.io.PrintWriter;
import java.util.ArrayList;

import synthesijer.hdl.expr.HDLBinaryExpr;
import synthesijer.hdl.literal.HDLValue;
import synthesijer.hdl.verilog.GenerateVerilogVisitor;
import synthesijer.hdl.vhdl.GenerateVHDLVisitor;

public class HDLModule implements HDLTree{
	
	private final String name;
	private final String sysClkName;
	private final String sysResetName;
	
	private ArrayList<HDLPort> ports = new ArrayList<HDLPort>();
	private ArrayList<HDLSignal> signals = new ArrayList<HDLSignal>();
	private ArrayList<HDLSequencer> sequencer = new ArrayList<HDLSequencer>();
	private ArrayList<HDLUserDefinedType> usertype = new ArrayList<HDLUserDefinedType>();
	private ArrayList<HDLInstance> submodules = new ArrayList<HDLInstance>();
	private ArrayList<HDLExpr> exprs = new ArrayList<HDLExpr>();
	
	public HDLModule(String name, String sysClkName, String sysResetName){
		this.name = name;
		this.sysClkName = sysClkName;
		this.sysResetName = sysResetName;
		newPort(sysClkName, HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		newPort(sysResetName, HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
	}
	
	public HDLModule(String name){
		this.name = name;
		sysClkName = "";
		sysResetName = "";
	}
	
	public String getName(){
		return name;
	}
	
	public HDLPort newPort(String name, HDLPort.DIR dir, HDLType type){
		HDLPort port = new HDLPort(this, name, dir, type);
		ports.add(port);
		return port;
	}
	
	public ArrayList<HDLPort> getPorts(){
		return ports;
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
	
	public HDLExpr newExpr(HDLOp op, HDLSignal arg0, int value){
		return newExpr(op, arg0, new HDLValue(String.valueOf(value), HDLPrimitiveType.genIntegerType()));
	}
	
	public ArrayList<HDLExpr> getExprs(){
		return exprs;
	}

	public HDLInstance newModuleInstance(HDLModule m, String n){
		HDLInstance obj = new HDLInstance(this, n, m);
		submodules.add(obj);
		return obj;
	}
	
	public ArrayList<HDLInstance> getModuleInstances(){
		return submodules;
	}
		
	private int getExprUniqueId(){
		return exprs.size() + 1;
	}
		
	public HDLExpr newExpr(HDLOp op, HDLExpr arg0, HDLExpr arg1){
		HDLExpr expr = null;
		expr = new HDLBinaryExpr(this, getExprUniqueId(), op, arg0, arg1);
		exprs.add(expr);
		return expr;
	}

	public ArrayList<HDLSignal> getSignals(){
		return signals;
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
	
	public ArrayList<HDLSequencer> getSequencers(){
		return sequencer;
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
