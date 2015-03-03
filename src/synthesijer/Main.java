package synthesijer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import net.wasamon.mjlib.util.GetOpt;
import net.wasamon.mjlib.util.GetOptException;
import openjdk.com.sun.tools.javac.main.Main.Result;
import synthesijer.Manager.OutputFormat;
import synthesijer.hdl.HDLModule;
import synthesijer.tools.xilinx.HDLModuleToComponentXML;

/**
 * The user interface for Synthesijer.
 * 
 * @author miyo
 *
 */
public class Main {
	
	public static void main(String... args) throws GetOptException{
		GetOpt opt = new GetOpt("h", "no-optimize,vhdl,verilog,help,config:,chaining,ip-exact:,vendor:,libname:", args);
		if(opt.flag("h") || opt.flag("help")){
			printHelp();
			System.exit(0);
		}

		openjdk.com.sun.tools.javac.main.Main compiler = new openjdk.com.sun.tools.javac.main.Main("javac", new PrintWriter(System.err, true));

		Result result = compiler.compile(opt.getArgs());
		
		boolean vhdlFlag = opt.flag("vhdl");
		boolean verilogFlag = opt.flag("verilog");
		Options options = new Options();
		options.optimizing = !opt.flag("no-optimize");
		options.chaining = opt.flag("chaining");
		options.operation_strength_reduction = opt.flag("operation_strength_reduction");
		boolean packaging = false;
		String packageTop = "";
		if(opt.flag("ip-exact")){
			packageTop = opt.getValue("ip-exact");
			packaging = true;
		}
		String vendor = "synthesijer";
		if(opt.flag("vendor")){
			vendor = opt.getValue("vendor");
		}
		String libname = "user";
		if(opt.flag("libname")){
			libname = opt.getValue("user");
		}

		if(opt.flag("config")){
			System.out.println("config: " + opt.getValue("config"));
		}
		
		if(vhdlFlag == false && verilogFlag == false){
			vhdlFlag = true;
		}
		
		if(result.isOK()){
			dump("dump000.xml");
			Manager.INSTANCE.preprocess();
			Manager.INSTANCE.generate(options);
			if(vhdlFlag) Manager.INSTANCE.output(OutputFormat.VHDL);
			if(verilogFlag) Manager.INSTANCE.output(OutputFormat.Verilog);
			if(packaging){
				HDLModule m = Manager.INSTANCE.searchHDLModuleInfo(packageTop).getHDLModule();
				HDLModuleToComponentXML.conv(m, null, vendor, libname);
			}
		}
		System.exit(result.exitCode);
	}
	
	private static void printHelp(){
		System.out.println();
		System.out.printf("Synthesijer: %d.%d.%d", Constant.majorVersion, Constant.minorVersion, Constant.revVersion);
		System.out.println();
		System.out.println();
		System.out.println("Usage: java [-cp classpath] synthesijer.Main [options] sources");
		System.out.println();
		System.out.println("Options:");
		System.out.println("  -h, --help: print this help");
		System.out.println("  --vhdl: output VHDL");
		System.out.println("  --verilog: output Verilog HDL");
		System.out.println("  (If you specify neither --vhdl nor --verilog, --vhdl is selected.)");
		System.out.println("  --config=file: thespecified file is used for compiler settings");
		System.out.println("  --no-optimize: do not apply any optimizations");
		System.out.println("  --chaining: do opeartion chain in greedy manner");
		System.out.println("  --operation-strength-reduction: do opeartion strength reduction");
		System.out.println();
		System.out.println("Please access to http://synthesijer.sorceforge.net/ for more information.");
		System.out.println();
	}
	
	public static void dump(String destName){
		try(PrintWriter dest = new PrintWriter(new FileOutputStream(destName), true)){
			Manager.INSTANCE.dumpAsXML(dest);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
