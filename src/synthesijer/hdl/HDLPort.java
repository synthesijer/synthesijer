package synthesijer.hdl;


public class HDLPort implements HDLTree{
	
	private final String name;
		
	private final DIR dir;
	
	private final HDLType type;
	
	private final HDLSignal sig;
	
	HDLPort(HDLModule m, String name, DIR dir, HDLType type){
		this.name = name;
		this.dir = dir;
		this.type = type;
		if(dir == DIR.OUT){
			this.sig = new HDLSignal(m, name + "_sig", type, HDLSignal.ResourceKind.REGISTER);
		}else{
			this.sig = new HDLSignal(m, name + "_sig", type, HDLSignal.ResourceKind.WIRE);
		}
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

	public String toString(){
		return String.format("HDLPort: %s dir=%s, type=%s", name, dir, type);
	}
	
	
	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLPort(this);
	};

}
