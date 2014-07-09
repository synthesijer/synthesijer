package synthesijer.hdl;

import java.util.ArrayList;

import openjdk.com.sun.tools.javac.util.Pair;
import synthesijer.hdl.HDLSequencer.SequencerState;

public class HDLInstance implements HDLTree, HDLExpr, HDLVariable{
	
	private final HDLModule module;
	private final String name;
	private final HDLModule submodule;
	
	private ArrayList<PortPair> pairs = new ArrayList<>();
	private ArrayList<ParamPair> params = new ArrayList<>();
	
	HDLInstance(HDLModule module, String name, HDLModule submodule){
		this.module = module;
		this.name = name;
		this.submodule = submodule;
		genPortSignals();
		for(HDLParameter param: submodule.getParameters()){
			params.add(new ParamPair(param, null));
		}
	}
	
	private String getAbsoluteName(String n){
		return this.name + "_" + n;
	}
	
	private void genPortSignals(){
		for(PortPair pair: pairs){ module.rmSignal(pair.signal); }
		pairs.clear();
		for(HDLPort p: submodule.getPorts()){
			HDLSignal.ResourceKind k = p.isOutput() ? HDLSignal.ResourceKind.WIRE : HDLSignal.ResourceKind.REGISTER;
			HDLSignal s = module.newSignal(getAbsoluteName(p.getName()), p.getType(), k);
			pairs.add(new PortPair(s, p));
		}
	}
	
	public void update(){
		genPortSignals();
	}
	
	public String getName(){
		return name;
	}
	
	public HDLModule getSubModule(){
		return submodule;
	}
	
	public ArrayList<PortPair> getPairs(){
		return pairs;
	}
	
	public HDLSignal getSignalForPort(String name){
		for(PortPair pair: pairs){
			if(pair.port.getName().equals(name)) return pair.signal;
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
		for(HDLPort p: submodule.getPorts()){
			s += " " + p + "\n";
		}
		return s;
	}
	

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLInstance(this);
	}
	
	public class PortPair{
		public final HDLSignal signal;
		public final HDLPort port;
		PortPair(HDLSignal signal, HDLPort port){this.signal = signal; this.port = port;}
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
