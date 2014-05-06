package synthesijer.hdl;

import java.io.PrintWriter;

public class HDLPort implements SynthesizableObject{
	
	private final String name;
		
	private final DIR dir;
	
	private final HDLType type;
	
	private HDLSignal srcSig;
	
	public HDLPort(String name, DIR dir, HDLType type){
		this.name = name;
		this.dir = dir;
		this.type = type;
	}
	
	public void dumpAsVHDL(PrintWriter dest, int offset){
		HDLUtils.print(dest, offset, String.format("%s : %s %s", name, dir.getVHDL(), type.getVHDL()));
	}

	public void dumpAsVerilogHDL(PrintWriter dest, int offset){
		HDLUtils.print(dest, offset, String.format("%s %s %s", dir.getVerilogHDL(), type.getVerilogHDL(), name));
	}
	
	public void setSrcSignal(HDLSignal src){
		srcSig = src;
	}
	
	public void assignAsVHDL(PrintWriter dest, int offset){
		if(dir == DIR.OUT){
			HDLUtils.println(dest, offset, String.format("%s <= %s;", name, srcSig.getName()));
		}
	}

	public void assignAsVerilogHDL(PrintWriter dest, int offset){
		if(dir == DIR.OUT){
			HDLUtils.println(dest, offset, String.format("assign %s = %s;", name, srcSig.getName()));
		}
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
	};

}
