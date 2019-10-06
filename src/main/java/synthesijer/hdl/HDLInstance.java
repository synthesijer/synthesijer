package synthesijer.hdl;

import java.util.ArrayList;

import synthesijer.SynthesijerUtils;
import synthesijer.Options;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.hdl.sequencer.SequencerState;

/**
 * Sub-module instantiated in HDL module
 */
public class HDLInstance implements HDLTree, HDLExpr, HDLVariable{

	/**
	 * parent module
	 */
	private final HDLModule module;

	/**
	 * instance name
	 */
	private final String name;

	/**
	 * original instance name
	 */
	private final String origName;

	/**
	 * target module to make instance
	 */
	private final HDLModule target;

	/**
	 * port map
	 */
	private ArrayList<PortPair> pairs = new ArrayList<>();

	/**
	 * parameter map
	 */
	private ArrayList<ParamPair> params = new ArrayList<>();

	/**
	 * create target module instance with user-defined name
	 * @param module parent module
	 * @param name name
	 * @param target target module to make instance
	 * @param origName original name
	 */
	HDLInstance(HDLModule module, String name, HDLModule target, String origName){
		this.module = module;
		this.name = name;
		this.target = target;
		this.origName = origName;
		genPortSignals();
		for(HDLParameter param: target.getParameters()){
			params.add(new ParamPair(param, null));
		}
	}

	/**
	 * create target module instance with user-defined name
	 * @param module parent module
	 * @param name name
	 * @param target target module to make instance
	 */
	HDLInstance(HDLModule module, String name, HDLModule target){
		this(module, name, target, name);
	}

	/**
	 * return this module, not the target module to make a instance
	 */
	public HDLModule getModule(){
		return module;
	}

	/**
	 * get absolute name, which is combined module name and instance name
	 * @param n target module name
	 * @return absolute name
	 */
	private String getAbsoluteName(String n){
		return this.name + "_" + n;
	}

	/**
	 * clear port pair
	 */
	private void clearPortPair(){
		for(PortPair pair: pairs){
			if(pair.item instanceof HDLSignal) module.rmSignal((HDLSignal)(pair.item));
		}
		pairs.clear();
	}

	/**
	 * generate pairs of ports and signals
	 */
	private void genPortSignals(){
		clearPortPair();
		for(HDLPort p: target.getPorts()){
			if(p.isSet(HDLPort.OPTION.NO_SIG)) continue;
			HDLSignal.ResourceKind k = p.isOutput() ? HDLSignal.ResourceKind.WIRE : HDLSignal.ResourceKind.REGISTER;
			HDLSignal s;
			if(p.hasWireName()){
				s = module.getSignal(p.getWireName());
				if(s == null){
					s = module.newSignal(p.getWireName(), p.getType(), HDLSignal.ResourceKind.WIRE);
				}else{
					if(!p.getType().isEqual(s.getType())) SynthesijerUtils.warn("instance wire type missmatch: " + p.getWireName());
				}
			}else{
				if(p.isSet(HDLPort.OPTION.EXPORT) && Options.INSTANCE.legacy_instance_variable_name == false){
					s = module.newSignal(origName + "_" + p.getName(), p.getType(), k);
				}else{
					s = module.newSignal(getAbsoluteName(p.getName()), p.getType(), k);
				}
			}
			pairs.add(new PortPair(s, p));
		}
	}

	/**
	 * add port pair
	 * @param item port pair
	 * @param port target port
	 */
	public void addPortPair(HDLPortPairItem item, HDLPort port){
		pairs.add(new PortPair(item, port));
	}

	/**
	 * remove port pair
	 * @param pair port pair
	 */
	public void rmPortPair(PortPair pair){
		if(pair == null){
			SynthesijerUtils.warn("not specified remove pair, nothing to do.");
			return;
		}
		pairs.remove(pair);
		if(pair.item instanceof HDLSignal) module.rmSignal((HDLSignal)pair.item);
	}

	/**
	 * replace port pair
	 * @param oldItem old pair
	 * @param newItem new pair
	 */
	public void replacePortPair(HDLPortPairItem oldItem, HDLPortPairItem newItem){
		PortPair pair = getPortPairByItem(oldItem);
		if(pair == null){
			SynthesijerUtils.warn("not specified remove pair, nothing to do.");
			return;
		}
		pairs.remove(pair);
		pairs.add(new PortPair(newItem, pair.port));
	}

	/**
	 * update pair of signals and ports
	 */
	public void update(){
		genPortSignals();
	}

	/**
	 *
	 * @return name of instance module
	 */
	@Override
	public String getName(){
		return name;
	}

	/**
	 *
	 * @return target module to make instance
	 */
	public HDLModule getSubModule(){
		return target;
	}

	/**
	 *
	 * @return reference of all pairs
	 */
	public ArrayList<PortPair> getPairs(){
		return pairs;
	}

	/**
	 * search and return port pair from port
	 * @param port port to find port pair
	 * @return port pair, null when no such pair
	 */
	public PortPair getPortPair(HDLPort port){
		for(PortPair pair: pairs){
			if(pair.port.getName().equals(port.getName())) return pair;
		}
		return null;
	}

	/**
	 * search and return port pair form port air
	 * @param item
	 * @return port pair, null when no such pair
	 */
	private PortPair getPortPairByItem(HDLPortPairItem item){
		for(PortPair pair: pairs){
			if(pair.item.getName().equals(item.getName())) return pair;
		}
		return null;
	}

	/**
	 * return port with the name
	 * @param name name of the port
	 * @return port
	 */
	public HDLPort getPort(String name){
		for(PortPair pair: pairs){
			if(pair.port.getName().equals(name)) return pair.port;
		}
		return null;
	}

	/**
	 * return signal corresponding the the port with name
	 * @param name name of the port
	 * @return signal
	 */
	public HDLSignal getSignalForPort(String name){
		for(PortPair pair: pairs){
			if(pair.port.getName().equals(name) && pair.item instanceof HDLSignal) return (HDLSignal)pair.item;
		}
		return null;
	}

	/**
	 * return port pair
	 * @param port
	 * @return
	 */
	public HDLPortPairItem getPairItemForPort(HDLPort port){
		//System.out.println("getSignalForPort:" + port);
		for(PortPair pair: pairs){
			//System.out.println("  pair:" + pair);
			if(pair.port.getName().equals(port.getName())) return (HDLSignal)pair.item;
		}
		return null;
	}

	/**
	 * return parameter pair
	 * @param name
	 * @return
	 */
	public ParamPair getParameterPair(String name){
		for(ParamPair pair: params){
			if(pair.param.getName().equals(name)) return pair;
		}
		return null;
	}

	/**
	 * overwrite parameter with given value
	 * @param name name of parameter to set
	 * @param value new value
	 */
	public void setParameterOverwrite(String name, HDLValue value){
		for(ParamPair pair: params){
			if(pair.param.getName().equals(name)){
				pair.value = value;
				return;
			}
		}
	}

	/**
	 *
	 * @return reference to all parameters
	 */
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

	/**
	 * Visitor-pattern method
	 * @param v
	 */
	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLInstance(this);
	}

	/**
	 * port pair
	 */
	public class PortPair{
		public final HDLPortPairItem item;
		public final HDLPort port;
		PortPair(HDLPortPairItem item, HDLPort port){this.item = item; this.port = port;}
	}

	/**
	 * parameter pair
	 */
	public class ParamPair{
		public final HDLParameter param;
		private HDLValue value;
		ParamPair(HDLParameter param, HDLValue value){this.param = param; this.value = value;}
		public void setValue(HDLValue value){
			this.value = value;
		}
		public HDLValue getValue(){
			return value;
		}
	}

	@Override
	public void setAssignForSequencer(HDLSequencer s, HDLExpr expr){
		// TODO Auto-generated method stub

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
	public void setAssign(SequencerState s, HDLExpr cond, HDLExpr expr) {
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

	@Override
	public void setAssignForSequencer(HDLSequencer s, HDLExpr cond, HDLExpr expr) {
		// TODO Auto-generated method stub

	}

}
