package synthesijer.hdl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import synthesijer.SynthesijerUtils;

public class HDLUtils {
	
	public static void println(PrintWriter dest, int offset, String str){
		dest.println(pad(offset) + str);
	}
	
	public static void print(PrintWriter dest, int offset, String str){
		dest.print(pad(offset) + str);
	}

	public static void nl(PrintWriter dest){
		dest.println();
	}
	
	private static String pad(int offset){
		String s = "";
		for(int i = 0; i < offset; i++){
			s += " ";
		}
		return s;
	}
	
	enum Format{
		VHDL, Verilog
	};
	
	public static final Format VHDL = Format.VHDL; 
	public static final Format Verilog = Format.Verilog; 
	
	public static void generate(HDLModule m, Format f){
		String ext = f == Format.VHDL ? ".vhd" : ".v";
		try(PrintWriter dest = new PrintWriter(new FileOutputStream(new File(m.getName() + ext)), true)){
			if(f == Format.VHDL){
				m.genVHDL(dest);
			}else{
				m.genVerilogHDL(dest);
			}
		}catch(IOException e){
			SynthesijerUtils.error(e.toString());
		}
	}

}
