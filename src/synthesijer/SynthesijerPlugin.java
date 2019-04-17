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

	boolean vhdlFlag = true;
	boolean verilogFlag = true;

	@Override
	public void init(JavacTask task, String... args){
		GetOpt opt = new GetOpt("h",
				"no-optimize,vhdl,verilog,help,config:,chaining,no-chaining,ip-exact:,vendor:,libname:,lib-classes:,legacy-instance-variable-name,iroha,bb2,opencl,bb,ssa",
				args);
		if (opt.flag("h") || opt.flag("help")) {
			printHelp();
			System.exit(0);
		}

		vhdlFlag = opt.flag("vhdl");
		verilogFlag = opt.flag("verilog");
		Options.INSTANCE.optimizing = !opt.flag("no-optimize");
		// Options.INSTANCE.chaining = !opt.flag("no-chaining");
		Options.INSTANCE.chaining = opt.flag("chaining");
		Options.INSTANCE.bb2 = opt.flag("bb2");
		Options.INSTANCE.bb = opt.flag("bb");
		Options.INSTANCE.legacy_instance_variable_name = opt.flag("legacy-instance-variable-name");
		Options.INSTANCE.operation_strength_reduction = opt.flag("operation_strength_reduction"
																 );
		Options.INSTANCE.iroha = opt.flag("iroha");
		Options.INSTANCE.opencl = opt.flag("opencl");
		Options.INSTANCE.with_ssa = opt.flag("ssa");
		
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
			Manager.INSTANCE.optimize(Options.INSTANCE);
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

