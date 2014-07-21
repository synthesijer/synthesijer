package synthesijer.hdl;

import java.util.ArrayList;

import synthesijer.SynthesijerUtils;
import synthesijer.hdl.HDLSequencer.SequencerState;

public class HDLInstance implements HDLTree, HDLExpr, HDLVariable{
	
	private final HDLModule module;
	private final String name;
	private final HDLModule target;
	
	private ArrayList<PortPair> pairs = new ArrayList<>();
	private ArrayList<ParamPair> params = new ArrayList<>();
	
	HDLInstance(HDLModule module, String name, HDLModule target){
		this.module = module;
		this.name = name;
		this.target = target;
		genPortSignals();
		for(HDLParameter param: target.getParameters()){
			params.add(new ParamPair(param, null));
		}
	}
	
	private String getAbsoluteName(String n){
		return this.name + "_" + n;
	}
	
	private void clearPortPair(){
		for(PortPair pair: pairs){
			if(pair.item instanceof HDLSignal) module.rmSignal((HDLSignal)(pair.item));
		}
		pairs.clear();
	}
	
	private void genPortSignals(){
		clearPortPair();
		for(HDLPort p: target.getPorts()){
			if(p.isSet(HDLPort.OPTION.NO_SIG)) continue;
			HDLSignal.ResourceKind k = p.isOutput() ? HDLSignal.ResourceKind.WIRE : HDLSignal.ResourceKind.REGISTER;
			HDLSignal s = module.newSignal(getAbsoluteName(p.getName()), p.getType(), k);
			pairs.add(new PortPair(s, p));
		}
	}
	
	public void addPortPair(HDLPortPairItem item, HDLPort port){
		//System.out.println("add:" + item);
		pairs.add(new PortPair(item, port));
	}
	
	public void rmPortPair(PortPair pair){
		if(pair == null){
			SynthesijerUtils.warn("not specified remove pair, nothing to do.");
			return;
		}
		pairs.remove(pair);
		if(pair.item instanceof HDLSignal) module.rmSignal((HDLSignal)pair.item);
	}
	
	public void update(){
		genPortSignals();
	}
	
	public String getName(){
		return name;
	}
	
	public HDLModule getSubModule(){
		return target;
	}
	
	public ArrayList<PortPair> getPairs(){
		return pairs;
	}

	public PortPair getPortPair(HDLPort port){
		for(PortPair pair: pairs){
			if(pair.port.getName().equals(port.getName())) return pair;
		}
		return null;
	}

	public HDLSignal getSignalForPort(String name){
		for(PortPair pair: pairs){
			if(pair.port.getName().equals(name) && pair.item instanceof HDLSignal) return (HDLSignal)pair.item;
		}
		return null;
	}
	
	public HDLPortPairItem getPairItemForPort(HDLPort port){
		//System.out.println("getSignalForPort:" + port);
		for(PortPair pair: pairs){
			//System.out.println("  pair:" + pair);
			if(pair.port.getName().equals(port.getName())) return (HDLSignal)pair.item;
		}
		return null;
	}
	
	public ParamPair getParameterPair(String name){
		for(ParamPair pair: params){
			if(pair.param.getName().equals(name)) return pair;
		}
		return null;
	}

	public ArrayList<ParamPair> getParameterPairs(){
		return params;
	}

	public String toString(){
		String s = "";
		s += "HDLInstance : " + name + "\n";
		for(HDLPort p: target.getPorts()){
			s += " " + p + "\n";
		}
		return s;
	}
	

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLInstance(this);
	}
	
	public class PortPair{
		public final HDLPortPairItem item;
		public final HDLPort port;
		PortPair(HDLPortPairItem item, HDLPort port){this.item = item; this.port = port;}
	}
	
	public class ParamPair{
		public final HDLParameter param;
		private String value;
		ParamPair(HDLParameter param, String value){this.param = param; this.value = value;}
		public void setValue(String value){
			this.value = value;
		}
		public String getValue(){
			return value;
		}
	}

	@Override
	public void setAssign(SequencerState s, HDLExpr expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAssign(SequencerState s, int count, HDLExpr expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setResetValue(HDLExpr s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDefaultValue(HDLExpr s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getVHDL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVerilogHDL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HDLExpr getResultExpr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HDLType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HDLSignal[] getSrcSignals() {
		// TODO Auto-generated method stub
		return null;
	}

}
