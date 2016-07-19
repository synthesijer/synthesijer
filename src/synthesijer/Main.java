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

import net.wasamon.mjlib.util.GetOpt;
import openjdk.com.sun.tools.javac.main.Main.Result;
import synthesijer.Manager.OutputFormat;
import synthesijer.Manager.SynthesijerModuleInfo;
import synthesijer.hdl.HDLModule;
import synthesijer.tools.xilinx.HDLModuleToComponentXML;

/**
 * The user interface for Synthesijer.
 * 
 * @author miyo
 *
 */
public class Main {

    public static void main(String... args) throws Exception{
	GetOpt opt = new GetOpt("h", "no-optimize,vhdl,verilog,help,config:,chaining,no-chaining,ip-exact:,vendor:,libname:,lib-classes:,legacy-instance-variable-name,iroha,bb2", args);
	if(opt.flag("h") || opt.flag("help") || opt.getArgs().length == 0){
	    printHelp();
	    System.exit(0);
	}

	ArrayList<String> javaSrc = new ArrayList<>();
	ArrayList<String> irSrc = new ArrayList<>();
	
	ArrayList<String> classPath = new ArrayList<>();
	ArrayList<String> javacPath = new ArrayList<>();
	
	classPath.add(".");
	javacPath.add(".");
	addClassPath(".");

	if(opt.flag("lib-classes")){
	    String arg = opt.getValue("lib-classes");
	    if(arg != null){
		String[] paths = arg.split(":");
		for(String path: paths){
		    addClassPath(path);
		    classPath.add(path);
		    javacPath.add(path);
		}
	    }
	}
	
	for(String a: opt.getArgs()){
	    if(a.endsWith(".java")){
		javaSrc.add(a);
		File f = new File(a);
		String p = f.getParent();
		if(p != null && classPath.contains(p) == false){
		    addClassPath(p);
		    classPath.add(p);
		}
	    }else if(a.endsWith("ir")){
		irSrc.add(a);
	    }
	}
	
	String osName = System.getProperty("os.name");
	String pathDelim = ":"; 
	if(osName.startsWith("Windows")){
	    pathDelim = ";";
	}
	String classPathStr = System.getProperty("java.class.path");
	for(String s: javacPath){
	    classPathStr += pathDelim + s;
	}
		
	if(javaSrc.size() > 0){
	    javaSrc.add(0, classPathStr);
	    javaSrc.add(0, "-cp");
	    //System.out.println(javaSrc);
	    openjdk.com.sun.tools.javac.main.Main compiler = new openjdk.com.sun.tools.javac.main.Main("javac", new PrintWriter(System.err, true));
	    Result result = compiler.compile(javaSrc.toArray(new String[]{}));
	    if(result.isOK() == false){
		System.exit(result.exitCode);
	    }
	}
		
	boolean vhdlFlag = opt.flag("vhdl");
	boolean verilogFlag = opt.flag("verilog");
	Options.INSTANCE.optimizing = !opt.flag("no-optimize");
	//Options.INSTANCE.chaining = !opt.flag("no-chaining");
	Options.INSTANCE.chaining = opt.flag("chaining");
	Options.INSTANCE.bb2 = opt.flag("bb2");
	Options.INSTANCE.legacy_instance_variable_name = opt.flag("legacy-instance-variable-name");
	Options.INSTANCE.operation_strength_reduction = opt.flag("operation_strength_reduction");
	Options.INSTANCE.iroha = opt.flag("iroha");
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
				
	//dump("dump000.xml");
	Manager.INSTANCE.preprocess();
	Manager.INSTANCE.optimize(Options.INSTANCE);
		
	for(String f: irSrc){
	    System.out.println(f);
	    Manager.INSTANCE.loadIR(f);
	}
	Manager.INSTANCE.generate();
		
	if(vhdlFlag) Manager.INSTANCE.output(OutputFormat.VHDL);
	if(verilogFlag) Manager.INSTANCE.output(OutputFormat.Verilog);
	if(packaging){
	    SynthesijerModuleInfo info = Manager.INSTANCE.searchHDLModuleInfo(packageTop);
	    if(info == null){
		SynthesijerUtils.warn("unknown module for ip-exact: " + packageTop);
	    }else{
		HDLModule m = info.getHDLModule();
		HDLModuleToComponentXML.conv(m, null, vendor, libname);
	    }
	}

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
	System.out.println("  --iroha: output IROHA source code");
	System.out.println("  (If you specify neither --vhdl nor --verilog, --vhdl is selected.)");
	System.out.println("  --config=file: the specified file is used for compiler settings");
	System.out.println("  --lib-classes=path1:path2:...: the specified paths will be added as classpaths for compile time");
	System.out.println("  --no-optimize: do not apply any optimizations");
	//System.out.println("  --no-chaining: do not apply opeartion chain in greedy manner");
	System.out.println("  --chaining: apply opeartion chain in greedy manner");
	System.out.println("  --operation-strength-reduction: do opeartion strength reduction");
	System.out.println("  --legacy-instance-variable-name: to use legacy variable name for instance variables, such as class_*_0000");
	System.out.println("  --ip-exact=TOP: generates a IP package template for \"TOP\" module");
	System.out.println("  --vendor=name: to specify vendor id for generating a IP package");
	System.out.println("  --libname=name: to specify library name for generating a IP package");
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

    public static void addClassPath(String classPath) throws IOException {
        addClassPath(new File(classPath));
    }

    private static void addClassPath(File classPath) throws IOException {
	/*
	  if (classPath.isDirectory()) {
	  File[] children = classPath.listFiles();
	  for(File f: children){
	  addClassPath(f);
	  }
	  }else{
	  addClassPath(classPath.toURL());
	  }
	*/	
	addClassPath(classPath.toURL());
    }

    private static final Class<?>[] PARAMETERS = new Class<?>[] { URL.class };

    private static void addClassPath(URL classPathUrl) throws IOException {
	System.out.println("Add classpath: " + classPathUrl);

        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();

        Class<?> sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", PARAMETERS);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] { classPathUrl });
        } catch (NoSuchMethodException e) {
            throw new IOException("could not add " + classPathUrl + " to classpath");
        } catch (InvocationTargetException e) {
            throw new IOException("could not add " + classPathUrl + " to classpath");
        } catch (IllegalAccessException e) {
            throw new IOException("could not add " + classPathUrl + " to classpath");
        }
    }
}
