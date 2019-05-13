package synthesijer.hdl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumSet;

import synthesijer.SynthesijerUtils;
import synthesijer.hdl.HDLPort.OPTION;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.hdl.tools.HDLSequencerToDot;
import synthesijer.hdl.tools.ResourceUsageTable;

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

	public enum Format{
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

	public static void genHDLSequencerDump(HDLModule m){
		try(PrintWriter dest = new PrintWriter(new FileOutputStream(new File(m.getName() + "_statemachine_hdl.dot")), true)){
			HDLSequencerToDot obj = new HDLSequencerToDot(m);
			obj.generate(dest);
		}catch(IOException e){
			SynthesijerUtils.error(e.toString());
		}
	}

	public static void genResourceUsageTable(HDLModule m){
		try(PrintWriter dest = new PrintWriter(new FileOutputStream(new File(m.getName() + "_resourcetable.html")), true)){
			ResourceUsageTable obj = new ResourceUsageTable(m);
			obj.generate(dest);
		}catch(IOException e){
			SynthesijerUtils.error(e.toString());
		}
	}

	public static HDLPort genOutputPort(HDLModule m, String name){
		return m.newPort(name, HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
	}

	public static HDLPort genOutputPort(HDLModule m, String name, EnumSet<OPTION> opt){
		return m.newPort(name, HDLPort.DIR.OUT, HDLPrimitiveType.genBitType(), opt);
	}

	public static HDLPort genOutputPort(HDLModule m, String name, int width){
		return m.newPort(name, HDLPort.DIR.OUT, HDLPrimitiveType.genVectorType(width));
	}

	public static HDLPort genOutputPort(HDLModule m, String name, int width, EnumSet<OPTION> opt){
		return m.newPort(name, HDLPort.DIR.OUT, HDLPrimitiveType.genVectorType(width), opt);
	}

	public static HDLPort genInputPort(HDLModule m, String name){
		return m.newPort(name, HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
	}

	public static HDLPort genInputPort(HDLModule m, String name, EnumSet<OPTION> opt){
		return m.newPort(name, HDLPort.DIR.IN, HDLPrimitiveType.genBitType(), opt);
	}

	public static HDLPort genInputPort(HDLModule m, String name, int width){
		return m.newPort(name, HDLPort.DIR.IN, HDLPrimitiveType.genVectorType(width));
	}

	public static HDLPort genInputPort(HDLModule m, String name, int width, EnumSet<OPTION> opt){
		return m.newPort(name, HDLPort.DIR.IN, HDLPrimitiveType.genVectorType(width), opt);
	}

	public static HDLValue value(int value, int width){
		return new HDLValue(String.valueOf(value), HDLPrimitiveType.genSignedType(width));
	}

	public static HDLValue newValue(int v, int w){
		return new HDLValue(String.valueOf(v), HDLPrimitiveType.genSignedType(w));
	}

}
