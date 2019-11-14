package synthesijer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Arrays;
import java.util.Locale;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import javax.tools.StandardJavaFileManager;

import net.wasamon.mjlib.util.GetOpt;

import synthesijer.Manager;
import synthesijer.hdl.HDLModule;
import synthesijer.tools.xilinx.HDLModuleToComponentXML;

/**
 * The user interface for Synthesijer.
 *
 * @author miyo
 *
 */
public class Main {

	public static void main(String... args) throws Exception {
		GetOpt opt = new GetOpt("h",
				"no-optimize,vhdl,verilog,help,config:,chaining,no-chaining,ip-exact:,vendor:,libname:,lib-classes:,legacy-instance-variable-name,iroha,bb2,opencl,bb,ssa,verbose,debug,devel:",
				args);
		if (opt.flag("h") || opt.flag("help") || opt.getArgs().length == 0) {
			printHelp();
			System.exit(0);
		}
		Options.INSTANCE.verbose = opt.flag("verbose");
		Options.INSTANCE.debug = opt.flag("debug");

		ArrayList<String> javaSrc = new ArrayList<>();
		ArrayList<String> irSrc = new ArrayList<>();
		ArrayList<String> vhdlSrc = new ArrayList<>();

		ArrayList<String> classPath = new ArrayList<>();
		ArrayList<String> javacPath = new ArrayList<>();

		String pathDelim = System.getProperty("os.name").startsWith("Windows") ? ";" : ":";

		classPath.add(".");
		javacPath.add(".");
		addClassPath(".");

		if (opt.flag("lib-classes")) {
			String arg = opt.getValue("lib-classes");
			if (arg != null) {
				String[] paths = arg.split(":");
				for (String path : paths) {
					addClassPath(path);
					classPath.add(path);
					javacPath.add(path);
				}
			}
		}

		for (String a : opt.getArgs()) {
			if (a.endsWith(".java")) {
				javaSrc.add(a);
				File f = new File(a);
				String p = f.getParent();
				if (p != null && classPath.contains(p) == false) {
					addClassPath(p);
					classPath.add(p);
				}
			} else if (a.endsWith("ir")) {
				irSrc.add(a);
			} else if (a.endsWith("vhd") || a.endsWith("vhdl")) {
				vhdlSrc.add(a);
			}
		}

		String classPathStr = System.getProperty("java.class.path");
		for (String s : javacPath) {
			classPathStr += pathDelim + s;
		}

		if(Options.INSTANCE.verbose){
			System.out.print("javac path =");
			for(var s: javacPath){
				System.out.print(" " + s);
			}
			System.out.println();
			System.out.print("class path =");
			for(var s: classPath){
				System.out.print(" " + s);
			}
			System.out.println();
		}

		if (javaSrc.size() > 0) {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, Locale.ENGLISH, StandardCharsets.UTF_8);

			ArrayList<File> files = new ArrayList<File>();
			for(String s: javaSrc){
				files.add(new File(s));
			}

			Iterable<? extends JavaFileObject> compilationUnits1 =
					fileManager.getJavaFileObjectsFromFiles(files);
                        JavaCompiler.CompilationTask t = 
			compiler.getTask(new PrintWriter(System.err),
					fileManager,
					null, // a diagnostic listener
					Arrays.asList(new String[]{"-encoding", "UTF-8", "-Xplugin:Synthesijer", "-cp", classPathStr}),
					null, // names of classes to be processed by annotation processing
					compilationUnits1
			);
			t.call();

			fileManager.close();
		}

		boolean vhdlFlag = opt.flag("vhdl");
		boolean verilogFlag = opt.flag("verilog");
		Options.INSTANCE.optimizing = !opt.flag("no-optimize");
		// Options.INSTANCE.chaining = !opt.flag("no-chaining");
		Options.INSTANCE.chaining = opt.flag("chaining");
		Options.INSTANCE.bb2 = opt.flag("bb2");
		Options.INSTANCE.bb = opt.flag("bb");
		Options.INSTANCE.ibb = opt.flag("inner-bb");
		Options.INSTANCE.legacy_instance_variable_name = opt.flag("legacy-instance-variable-name");
		Options.INSTANCE.operation_strength_reduction = opt.flag("operation_strength_reduction");
		Options.INSTANCE.iroha = opt.flag("iroha");
		Options.INSTANCE.opencl = opt.flag("opencl");
		Options.INSTANCE.with_ssa = opt.flag("ssa");
		boolean packaging = false;
		String packageTop = "";
		if (opt.flag("ip-exact")) {
			packageTop = opt.getValue("ip-exact");
			packaging = true;
		}
		String vendor = "synthesijer";
		if (opt.flag("vendor")) {
			vendor = opt.getValue("vendor");
		}
		String libname = "user";
		if (opt.flag("libname")) {
			libname = opt.getValue("user");
		}

		if (opt.flag("config")) {
			System.out.println("config: " + opt.getValue("config"));
		}

		if (vhdlFlag == false && verilogFlag == false) {
			vhdlFlag = true;
		}
		
		if (opt.flag("devel")) {
			try{
				Options.INSTANCE.develLevel = Integer.valueOf(opt.getValue("devel"));
			}catch(NumberFormatException e){
				Options.INSTANCE.develLevel = 0;
			}
		}

		Manager.INSTANCE.preprocess();
		Manager.INSTANCE.optimize(Options.INSTANCE);

		for (String f : irSrc) {
			System.out.println(f);
			Manager.INSTANCE.loadIR(f, Options.INSTANCE);
		}
		for (String f : vhdlSrc) {
			System.out.println(f);
			Manager.INSTANCE.loadVHDL(f, Options.INSTANCE);
		}
		Manager.INSTANCE.generate();

		if (vhdlFlag)
			Manager.INSTANCE.output(Manager.OutputFormat.VHDL);
		if (verilogFlag)
			Manager.INSTANCE.output(Manager.OutputFormat.Verilog);
		if (packaging) {
			Manager.SynthesijerModuleInfo info = Manager.INSTANCE.searchHDLModuleInfo(packageTop);
			if (info == null) {
				SynthesijerUtils.warn("unknown module for ip-exact: " + packageTop);
			} else {
				HDLModule m = info.getHDLModule();
				HDLModuleToComponentXML.conv(m, null, vendor, libname);
			}
		}

	}

	private static void printHelp() {
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
		System.out.println("  --iroha: output IROHA source code");
		System.out.println("  (If you specify neither --vhdl nor --verilog, --vhdl is selected.)");
		System.out.println("  --config=file: the specified file is used for compiler settings");
		System.out.println("  --lib-classes=path1:path2:...: the specified paths will be added as classpaths for compile time");
		System.out.println("  --no-optimize: do not apply any optimizations");
		// System.out.println(" --no-chaining: do not apply opeartion chain in greedy manner");
		System.out.println("  --chaining: apply opeartion chain in greedy manner");
		System.out.println("  --bb2: to use BasicParallelizer2 (in default, BasicParallelizer will be used)");
		System.out.println("  --operation-strength-reduction: do opeartion strength reduction");
		System.out.println("  --legacy-instance-variable-name: to use legacy variable name for instance variables, such as class_*_0000");
		System.out.println("  --ip-exact=TOP: generates a IP package template for \"TOP\" module");
		System.out.println("  --vendor=name: to specify vendor id for generating a IP package");
		System.out.println("  --libname=name: to specify library name for generating a IP package");
		System.out.println();
		System.out.println("Please access to http://synthesijer.sorceforge.net/ for more information.");
		System.out.println();
	}

	public static void dump(String destName) {
		try (PrintWriter dest = new PrintWriter(new FileOutputStream(destName), true)) {
			Manager.INSTANCE.dumpAsXML(dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void addClassPath(String classPath) throws IOException {
		Manager.INSTANCE.addLoadPath(classPath);
	}
}
