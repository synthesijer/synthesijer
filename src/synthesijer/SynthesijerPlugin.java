package synthesijer;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TreeScanner;
import com.sun.source.tree.ClassTree;

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
// import synthesijer.Manager.OutputFormat;
// import synthesijer.Manager.SynthesijerModuleInfo;
// import synthesijer.hdl.HDLModule;
// import synthesijer.tools.xilinx.HDLModuleToComponentXML;

/**
 * The user interface for Synthesijer.
 * 
 * @author miyo
 *
 */
public class SynthesijerPlugin implements Plugin, TaskListener{

	@Override
	public String getName(){
		return "Synthesijer";
	}

	@Override
	public void init(JavacTask task, String... args){
		GetOpt opt = new GetOpt("h",
				"no-optimize,vhdl,verilog,help,config:,chaining,no-chaining,ip-exact:,vendor:,libname:,lib-classes:,legacy-instance-variable-name,iroha,bb2,opencl,bb,ssa",
				args);
		if (opt.flag("h") || opt.flag("help")) {
			printHelp();
			System.exit(0);
		}
		task.addTaskListener(this);
	}

	@Override
	public void started(TaskEvent e){
		//synthesijer.jcfrontend.Main.newModule(env, cdef); // add hook for synthesijer
	}

	@Override
	public void finished(TaskEvent e){
	}
	
	private static void printHelp() {
		System.out.printf("Synthesijer: %d.%d.%d", Constant.majorVersion, Constant.minorVersion, Constant.revVersion);
		System.out.println();
		System.out.println("Please access to http://synthesijer.github.io/web/ for more information.");
	}

}

