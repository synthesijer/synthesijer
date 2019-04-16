package synthesijer;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TreeScanner;
import com.sun.source.tree.CompilationUnitTree;

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
import java.util.Hashtable;

import net.wasamon.mjlib.util.GetOpt;

import synthesijer.jcfrontend.JCTopVisitor;
import synthesijer.jcfrontend.SourceInfo;
import synthesijer.jcfrontend.PreScanner;
import synthesijer.ast.Module;
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
		if (e.getKind() == TaskEvent.Kind.GENERATE){
			System.out.println(e);
			Hashtable<String, String> importTable = new Hashtable<>();
			ArrayList<String> implementing = new ArrayList<>();
			String extending = "";
			boolean synthesizeFlag = true;

			CompilationUnitTree t = e.getCompilationUnit();
			SourceInfo info = new SourceInfo();
			t.accept(new PreScanner(), info);
			Module module = new Module(info.className, importTable, null, implementing);
			JCTopVisitor visitor = new JCTopVisitor(module);
			t.accept(visitor, null);
			Manager.INSTANCE.addModule(module, synthesizeFlag);
        }
	}

	@Override
	public void finished(TaskEvent e){
		if (e.getKind() == TaskEvent.Kind.GENERATE){
			Manager.INSTANCE.preprocess();
			//Manager.INSTANCE.optimize(Options.INSTANCE);
			Manager.INSTANCE.generate();
			boolean vhdlFlag = true;
			boolean verilogFlag = true;
			boolean packaging = false;
			if (vhdlFlag)
				Manager.INSTANCE.output(OutputFormat.VHDL);
			if (verilogFlag)
				Manager.INSTANCE.output(OutputFormat.Verilog);
		}
	}
	
	private static void printHelp() {
		System.out.printf("Synthesijer: %d.%d.%d", Constant.majorVersion, Constant.minorVersion, Constant.revVersion);
		System.out.println();
		System.out.println("Please access to http://synthesijer.github.io/web/ for more information.");
	}

}

