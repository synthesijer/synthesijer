package synthesijer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import synthesijer.ast.Module;
import synthesijer.ast2hdl.GenerateHDLModuleVisitor;
import synthesijer.hdl.HDLModule;
import synthesijer.lib.BlockRAM;
import synthesijer.lib.DIV32;
import synthesijer.lib.DIV64;
import synthesijer.lib.FADD32;
import synthesijer.lib.FADD64;
import synthesijer.lib.FCONV_D2F;
import synthesijer.lib.FCONV_D2L;
import synthesijer.lib.FCONV_F2D;
import synthesijer.lib.FCONV_F2I;
import synthesijer.lib.FCONV_I2F;
import synthesijer.lib.FCONV_L2D;
import synthesijer.lib.FDIV32;
import synthesijer.lib.FDIV64;
import synthesijer.lib.FMUL32;
import synthesijer.lib.FMUL64;
import synthesijer.lib.FSUB32;
import synthesijer.lib.FSUB64;
import synthesijer.lib.INPUT1;
import synthesijer.lib.INPUT16;
import synthesijer.lib.INPUT32;
import synthesijer.lib.MUL32;
import synthesijer.lib.MUL64;
import synthesijer.lib.OUTPUT1;
import synthesijer.lib.OUTPUT16;
import synthesijer.lib.OUTPUT32;
import synthesijer.model.BasicBlockStatemachineOptimizer;
import synthesijer.model.GenerateSynthesisTableVisitor;
import synthesijer.model.SynthesisTable;
import synthesijer.scheduler.GenSchedulerBoardVisitor;
import synthesijer.scheduler.GlobalSymbolTable;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerInfoCompiler;

public enum Manager {
	
	INSTANCE;
	
	private Hashtable<String, Module> entries = new Hashtable<>();
	private Hashtable<String, HDLModuleInfo> moduleTable = new Hashtable<>();
	private Hashtable<String, SchedulerInfo> schedulerInfoTable = new Hashtable<>();
	private ArrayList<String> userHDLModules = new ArrayList<>();
		
	private Manager(){
		// TODO
		addHDLModule("BlockRAM1",  null, new BlockRAM(1, 10, 1024), false);
		addHDLModule("BlockRAM8",  null, new BlockRAM(8, 10, 1024), false);
		addHDLModule("BlockRAM16", null, new BlockRAM(16, 10, 1024), false);
		addHDLModule("BlockRAM32", null, new BlockRAM(32, 10, 1024), false);
		addHDLModule("BlockRAM64", null, new BlockRAM(64, 10, 1024), false);
		addHDLModule("synthesijer.lib.INPUT1", null, new INPUT1(), false);
		addHDLModule("synthesijer.lib.INPUT16", null, new INPUT16(), false);
		addHDLModule("synthesijer.lib.INPUT32", null, new INPUT32(), false);
		addHDLModule("synthesijer.lib.OUTPUT1", null, new OUTPUT1(), false);
		addHDLModule("synthesijer.lib.OUTPUT16", null, new OUTPUT16(), false);
		addHDLModule("synthesijer.lib.OUTPUT32", null, new OUTPUT32(), false);
		addHDLModule("INPUT1", null, new INPUT1(), false);
		addHDLModule("INPUT16", null, new INPUT16(), false);
		addHDLModule("INPUT32", null, new INPUT32(), false);
		addHDLModule("OUTPUT1", null, new OUTPUT1(), false);
		addHDLModule("OUTPUT16", null, new OUTPUT16(), false);
		addHDLModule("OUTPUT32", null, new OUTPUT32(), false);
		// MUL/DIV
		addHDLModule("MUL32", null, new MUL32(), false);
		addHDLModule("MUL64", null, new MUL64(), false);
		addHDLModule("DIV32", null, new DIV32(), false);
		addHDLModule("DIV64", null, new DIV64(), false);
		// floating 32-bit
		addHDLModule("FADD32", null, new FADD32(), false);
		addHDLModule("FSUB32", null, new FSUB32(), false);
		addHDLModule("FMUL32", null, new FMUL32(), false);
		addHDLModule("FDIV32", null, new FDIV32(), false);
		// floating 64-bit
		addHDLModule("FADD64", null, new FADD64(), false);
		addHDLModule("FSUB64", null, new FSUB64(), false);
		addHDLModule("FMUL64", null, new FMUL64(), false);
		addHDLModule("FDIV64", null, new FDIV64(), false);
		// conversion
		addHDLModule("FCONV_F2I", null, new FCONV_F2I(), false);
		addHDLModule("FCONV_D2L", null, new FCONV_D2L(), false);
		addHDLModule("FCONV_I2F", null, new FCONV_I2F(), false);
		addHDLModule("FCONV_L2D", null, new FCONV_L2D(), false);
		addHDLModule("FCONV_F2D", null, new FCONV_F2D(), false);
		addHDLModule("FCONV_D2F", null, new FCONV_D2F(), false);
	}
	
	public void registUserHDLModule(String name){
		userHDLModules.add(name);
	}
	
	public void addModule(Module m){
		if(hasModule(m.getName())) return;
		entries.put(m.getName(), m);
		addHDLModule(m.getName(), m, new HDLModule(m.getName(), "clk", "reset"));
	}
	
	private void addHDLModule(String name, Module m, HDLModule hm){
		addHDLModule(name, m, hm, true);
	}

	private void addHDLModule(String name, Module m, HDLModule hm, boolean synthesisFlag){
		moduleTable.put(name, new HDLModuleInfo(m, hm, synthesisFlag));
	}

	public Module searchModule(String name){
		return entries.get(name);
	}

	public HDLModuleInfo searchHDLModuleInfo(String name){
		HDLModuleInfo info = moduleTable.get(name);
		return info;
	}
	
	public void HDLModuleInfoList(){
		Enumeration<String> keys = moduleTable.keys();
		while(keys.hasMoreElements()){
			System.out.println(keys.nextElement());
		}
	}

	public boolean isGeneratedHDLModule(String name){
		HDLModuleInfo info = moduleTable.get(name);
		return !info.sysnthesisFlag || !info.state.isBefore(CompileState.GENERATE_HDL);
	}

	public boolean hasModule(String key){
		return entries.contains(key);
	}
	
	private void genGlobalSymbolTable(){
		Enumeration<String> keys = moduleTable.keys();
		while(keys.hasMoreElements()){
			String k = keys.nextElement();
			HDLModuleInfo info = moduleTable.get(k);
			if(info.m != null){
				GlobalSymbolTable.INSTANCE.add(info.m);
			}else{
				GlobalSymbolTable.INSTANCE.add(k, info.hm);
			}
		}
	}
	
	private void doGenSchedulerBoard(){
		for(Module m: entries.values()){
			IdentifierGenerator i = new IdentifierGenerator();
			SchedulerInfo info = new SchedulerInfo(m.getName());
			GenSchedulerBoardVisitor v = new GenSchedulerBoardVisitor(info, i);
			m.accept(v);
			schedulerInfoTable.put(m.getName(), info);
			try(PrintStream txt = new PrintStream(new FileOutputStream(new File(m.getName() + "_scheduler_board.txt")));
				PrintStream dot = new PrintStream(new FileOutputStream(new File(m.getName() + "_scheduler_board.dot")))){
				dot.println("digraph {");
				for(SchedulerBoard b: info.getBoardsList()){
					b.dump(txt);
					b.dumpDot(dot);
				}
				dot.println("}");
				txt.close();
				dot.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public void preprocess(){
		loadUserHDLModules();
		genGlobalSymbolTable();
		doGenSchedulerBoard();
		doGenSimplifiedAst(new IdentifierGenerator());
		//makeCallGraph();
	}
	
	private void loadUserHDLModules(){
		for(String s: userHDLModules){
			try {
				Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(s);
				
				Constructor<?> ct = clazz.getConstructor(new Class[]{String[].class});
				Object obj = ct.newInstance(new Object[]{new String[]{}});
				if(!(obj instanceof HDLModule)){
					System.err.printf("unsupported type: %s (%s)", obj, obj.getClass());
					System.exit(0);
				}
				HDLModuleInfo info = new HDLModuleInfo(null, (HDLModule)obj, false);
				info.state = CompileState.GENERATE_HDL;
				moduleTable.put(s, info);
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
	}
		
	private void doGenSimplifiedAst(IdentifierGenerator idGenerator){
		for(Module m: entries.values()){
			GenSimplifiedAstVisitor v = new GenSimplifiedAstVisitor(idGenerator);
			m.accept(v);
		}
	}
	
	private void doStateCombine(){
		for(Module m: entries.values()){
			(new BasicBlockStatemachineOptimizer(m)).optimize();
		}
	}
	
	private void optimizations(){
		doStateCombine();
	}
	
	private void doGenSynthesisTable(){
		for(Module m: entries.values()){
			try(FileOutputStream fo = new FileOutputStream(new File(m.getName() + "_synthesis_table.txt"))){
				PrintStream out = new PrintStream(fo);
				ArrayList<SynthesisTable> tables = new ArrayList<>();
				GenerateSynthesisTableVisitor v = new GenerateSynthesisTableVisitor(out, tables);
				m.accept(v);
				v.dump(out);
				out.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	private boolean SCHEDULER_BOARD_MODE = true;
	
	private void compileSchedulerInfoAll(){
		for(Module m: entries.values()){
			compileSchedulerInfo(m.getName(), searchHDLModuleInfo(m.getName()));
		}
	}
	
	public void compileSchedulerInfo(String name, HDLModuleInfo hmi){
		if(hmi.state.isBefore(CompileState.GENERATE_HDL)){
			hmi.state = CompileState.GENERATE_HDL;
			SchedulerInfo info = schedulerInfoTable.get(name);
			SchedulerInfoCompiler compiler = new SchedulerInfoCompiler(info, hmi.hm);			
			compiler.compile();
		}
	}
	
	public void generate(boolean optimizeFlag){
		if(SCHEDULER_BOARD_MODE == false){
			makeStateMachine();
			dumpStateMachine("init");
			if(optimizeFlag){
				optimizations();
				dumpStateMachine("opt");
			}else{
				SynthesijerUtils.warn("skip optimization");
			}
			doGenSynthesisTable();
			genHDLAll();
		}else{
			compileSchedulerInfoAll();
		}
	}
	
	public void makeCallGraph(){
		MakeCallFlowVisitor visitor = new MakeCallFlowVisitor();
		for(Module m: entries.values()){
			m.accept(visitor);
		}
	}

	public void makeStateMachine(){
		for(Module m: entries.values()){
			m.genStateMachine();
		}
	}
	
	public void dumpStateMachine(String s){
		for(Module m: entries.values()){
			try(PrintWriter dest = new PrintWriter(new FileOutputStream(String.format("%s_statemachine_%s.dot", m.getName(), s)), true)){
				m.accept(new DumpStatemachineVisitor(dest));
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}

	
	enum OutputFormat { Verilog, VHDL; };
		
	public void genHDLAll(){
		for(Module m: entries.values()){
			genHDL(searchHDLModuleInfo(m.getName()));
		}
	}
	
	public void genHDL(HDLModuleInfo info){
		if(info.state.isBefore(CompileState.GENERATE_HDL)){
			info.state = CompileState.GENERATE_HDL;
			info.m.accept(new GenerateHDLModuleVisitor(info.hm));
		}
	}
	
	public void output(OutputFormat format){
		for(Module m: entries.values()){
			HDLModule hm = moduleTable.get(m.getName()).hm;
			if(format == OutputFormat.VHDL){
				String destFileName = String.format("%s.vhd", hm.getName());
				System.out.printf("Output VHDL: %s\n", destFileName);
				try(PrintWriter dest = new PrintWriter(new FileOutputStream(destFileName), true)){ 
					hm.genVHDL(dest);
				}catch(IOException e){
					e.printStackTrace();
				}
			}else{
				String destFileName = String.format("%s.v", hm.getName());
				System.out.printf("Output Verilog HDL: %s\n", destFileName);
				try(PrintWriter dest = new PrintWriter(new FileOutputStream(destFileName), true)){ 
					hm.genVerilogHDL(dest);
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		
	}

	public void dumpAsXML(PrintWriter dest){
		DumpAsXMLVisitor visitor = new DumpAsXMLVisitor(dest);
		dest.printf("<?xml version=\"1.0\" ?>\n");
		dest.printf("<modules>\n");
		for(Module m: entries.values()){
			m.accept(visitor);
		}
		dest.printf("</modules>\n");
	}
	
	public class HDLModuleInfo{
		public final Module m;
		public final HDLModule hm; 
		public final boolean sysnthesisFlag;
		private CompileState state;
		public HDLModuleInfo(Module m, HDLModule hm, boolean synthesisFlag) {
			this.m = m;
			this.hm = hm;
			this.sysnthesisFlag = synthesisFlag;
			if(synthesisFlag == false){
				this.state = CompileState.GENERATE_HDL;
			}else{
				this.state = CompileState.INITIALIZE;
			}
		}
		
		public HDLModuleInfo(Module m, HDLModule hm){
			this(m, hm, true);
		}
		
		public void setCompileState(CompileState s){
			state = s;
		}
		
		public CompileState getCompileState(){
			return state;
		}
	}	

}
