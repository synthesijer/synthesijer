package synthesijer.hdl;


public class HDLPort implements HDLTree{
	
	private final String name;
		
	private final DIR dir;
	
	private final HDLType type;
	
	private final HDLSignal srcSig;
	
	HDLPort(HDLModule m, String name, DIR dir, HDLType type){
		this.name = name;
		this.dir = dir;
		this.type = type;
		this.srcSig = new HDLSignal(m, name + "_sig", type, HDLSignal.ResourceKind.REGISTER);
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
		
	public HDLSignal getSrcSignal(){
		return srcSig;
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


	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLPort(this);
	};

}
