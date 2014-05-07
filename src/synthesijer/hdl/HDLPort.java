package synthesijer.hdl;


public class HDLPort implements HDLTree{
	
	private final String name;
		
	private final DIR dir;
	
	private final HDLType type;
	
	private HDLSignal srcSig;
	
	public HDLPort(String name, DIR dir, HDLType type){
		this.name = name;
		this.dir = dir;
		this.type = type;
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
		
	public void setSrcSignal(HDLSignal src){
		srcSig = src;
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
