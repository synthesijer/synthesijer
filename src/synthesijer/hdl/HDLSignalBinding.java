package synthesijer.hdl;

import java.util.Enumeration;
import java.util.Hashtable;

public abstract class HDLSignalBinding {
	
	public final String name;
	
	public HDLSignalBinding(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public abstract HDLSignalBinding export(String prefix);

	private final Hashtable<HDLPort, String> list = new Hashtable<>();
	
	public void set(HDLPort key, String value){
		list.put(key, value);
		key.setSignalBinding(this);
	}
	
	public String get(HDLPort key){
		return list.get(key);
	}
	
	public void dump(){
		System.out.println(list);
	}
	
	public Enumeration<HDLPort> getKeys(){
		return list.keys();
	}

	public abstract String getVendor();

	public abstract String getLibrary();
	
	public abstract String getBusTypeName();
	
	public abstract String getBusAbstractionTypeName();
	
	public abstract String getVersion();
	
	public abstract boolean isMaster();
	
	public abstract boolean hasAddressSpace();
	
	public abstract boolean hasMemoryMap();
	
	public abstract String getAddressBlockName();
	
	public abstract long getRange();

}
