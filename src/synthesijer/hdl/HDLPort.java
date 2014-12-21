package synthesijer.hdl;

import java.util.EnumSet;


public class HDLPort implements HDLTree, HDLPortPairItem{
	
	private final String name;
	
	private final String wire;
		
	private final DIR dir;
	
	private final HDLType type;
	
	private final HDLSignal sig;
	
	private final EnumSet<OPTION> options;
	
	HDLPort(HDLModule m, String name, DIR dir, HDLType type, EnumSet<OPTION> opt){
		this(m, name, "", dir, type, opt);
	}
	
	HDLPort(HDLModule m, String name, String wire, DIR dir, HDLType type, EnumSet<OPTION> opt){
		this.name = name;
		if(wire.equals("") == false){
			this.wire = "user_wire_" + wire;
		}else{
			this.wire = "";
		}
		this.dir = dir;
		this.type = type;
		
//		if(dir != DIR.INOUT){ // forced "no_sig"
//			opt.add(OPTION.NO_SIG);
//		}

		options = opt;
		
		if(options.contains(OPTION.NO_SIG) == false && dir != DIR.INOUT){
			if(dir == DIR.OUT){
				this.sig = new HDLSignal(m, name + "_sig", type, HDLSignal.ResourceKind.REGISTER);
			}else{
				this.sig = new HDLSignal(m, name + "_sig", type, HDLSignal.ResourceKind.WIRE);
			}
		}else{
			sig = null;
		}
	}
	
	public boolean isSet(OPTION opt){
		return options.contains(opt);
	}
	
	public boolean hasWireName(){
		return !wire.equals("");
	}

	public String getWireName(){
		return wire;
	}

	public String getName(){
		return name;
	}
	
	public DIR getDir(){
		return dir;
	}
	
	public boolean isOutput(){
		return dir == DIR.OUT;
	}
	
	public HDLType getType(){
		return type;
	}
		
	public HDLSignal getSignal(){
		return sig;
	}
	
	public enum DIR {
		IN("in", "input"),
		OUT("out", "output"),
		INOUT("inout", "inout");
		
		private final String vhdl, verilog;
		
		private DIR(String vhdl, String verilog){
			this.vhdl = vhdl;
			this.verilog = verilog;
		}
		
		public String getVHDL(){
			return vhdl;
		}
		public String getVerilogHDL(){
			return verilog;
		}
	}

	public enum OPTION {
		NO_SIG,
		EXPORT
	}

	public String toString(){
		String str = String.format("HDLPort: %s dir=%s, type=%s", name, dir, type);
		if(isSet(OPTION.NO_SIG)) str += " ,NO_SIG";
		if(isSet(OPTION.NO_SIG)) str += " ,EXPORT";
		return str;
	}
	
	
	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLPort(this);
	};

}
