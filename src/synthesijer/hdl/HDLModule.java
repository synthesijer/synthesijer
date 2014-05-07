package synthesijer.hdl;

import java.util.ArrayList;

public class HDLModule implements HDLTree{
	
	private final String name;
	private final String sysClkName;
	private final String sysResetName;
	
	private ArrayList<HDLPort> ports = new ArrayList<HDLPort>();
	private ArrayList<HDLSignal> signals = new ArrayList<HDLSignal>();
	private ArrayList<HDLSequencer> statemachines = new ArrayList<HDLSequencer>();
	
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
		statemachines.add(m);
	}
	
	public ArrayList<HDLSequencer> getSequencers(){
		return statemachines;
	}

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLModule(this);
	}

}
