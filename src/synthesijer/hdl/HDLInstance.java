package synthesijer.hdl;

import java.util.ArrayList;

import synthesijer.SynthesijerUtils;
import synthesijer.Options;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.hdl.sequencer.SequencerState;

public class HDLInstance implements HDLTree, HDLExpr, HDLVariable{
	
    private final HDLModule module;
    private final String name;
    private final String origName;
    private final HDLModule target;
	
    private ArrayList<PortPair> pairs = new ArrayList<>();
    private ArrayList<ParamPair> params = new ArrayList<>();
	
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
    
    HDLInstance(HDLModule module, String name, HDLModule target){
	this(module, name, target, name);
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
	    HDLSignal s;
	    if(p.hasWireName()){
		s = module.getSignal(p.getWireName());
		if(s == null){
		    s = module.newSignal(p.getWireName(), p.getType(), HDLSignal.ResourceKind.WIRE);
		}else{
		    //if(p.getType() != s.getType()) SynthesijerUtils.warn("instance wire type missmatch: " + p.getWireName());
		    if(!p.getType().isEqual(s.getType())) SynthesijerUtils.warn("instance wire type missmatch: " + p.getWireName());
		}
	    }else{
		if(p.isSet(HDLPort.OPTION.EXPORT) && Options.INSTANCE.legacy_instance_variable_name == false){
		    s = module.newSignal(origName + "_" + p.getName(), p.getType(), k);
		}else{
		    s = module.newSignal(getAbsoluteName(p.getName()), p.getType(), k);
		}
		//System.out.println("n=" + p.getName());
		//System.out.println("s=" + s);
	    }
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

    public void replacePortPair(HDLPortPairItem oldItem, HDLPortPairItem newItem){
	PortPair pair = getPortPairByItem(oldItem);
	if(pair == null){
	    SynthesijerUtils.warn("not specified remove pair, nothing to do.");
	    return;
	}
	pairs.remove(pair);
	pairs.add(new PortPair(newItem, pair.port));
    }

    public void update(){
	genPortSignals();
    }
	
    @Override
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

    private PortPair getPortPairByItem(HDLPortPairItem item){
	for(PortPair pair: pairs){
	    //System.out.println(pair.item.getName() + "<->" + item.getName());
	    if(pair.item.getName().equals(item.getName())) return pair;
	}
	return null;
    }

    public HDLPort getPort(String name){
	for(PortPair pair: pairs){
	    if(pair.port.getName().equals(name)) return pair.port;
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

    public void setParameterOverwrite(String name, HDLValue value){
	for(ParamPair pair: params){
	    if(pair.param.getName().equals(name)){
		pair.value = value;
		return; 
	    }
	}
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
