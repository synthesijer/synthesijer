package synthesijer.hdl;

import java.io.PrintWriter;
import java.util.ArrayList;

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
		ports.add(new HDLPort(sysClkName, HDLPort.DIR.IN, HDLType.genBitType()));
		ports.add(new HDLPort(sysResetName, HDLPort.DIR.IN, HDLType.genBitType()));
	}
	
	public String getName(){
		return name;
	}
	
	public void addPort(HDLPort p){
		ports.add(p);
	}
	
	public ArrayList<HDLPort> getPorts(){
		return ports;
	}

	public void addSignal(HDLSignal s){
		signals.add(s);
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
	
	public void addStateMachine(HDLSequencer m){
		sequencer.add(m);
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
