package synthesijer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.ast.Module;
import synthesijer.ast2hdl.GenerateHDLModuleVisitor;
import synthesijer.hdl.HDLModule;
import synthesijer.lib.BlockRAM;

public enum Manager {
	
	INSTANCE;
	
	private Hashtable<String, Module> entries = new Hashtable<>();
	private Hashtable<String, HDLModuleInfo> moduleTable = new Hashtable<>();
	private ArrayList<String> userHDLModules = new ArrayList<>();
		
	private Manager(){
		// TODO
		addHDLModule("BlockRAM1",  null, new BlockRAM(1, 10, 1024), false);
		addHDLModule("BlockRAM8",  null, new BlockRAM(8, 10, 1024), false);
		addHDLModule("BlockRAM16", null, new BlockRAM(16, 10, 1024), false);
		addHDLModule("BlockRAM32", null, new BlockRAM(32, 10, 1024), false);
		addHDLModule("BlockRAM64", null, new BlockRAM(64, 10, 1024), false);
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
		moduleTable.put(name, new HDLModuleInfo(m, hm));
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

	public boolean isGeneratedHDLModule(String name){
		HDLModuleInfo info = moduleTable.get(name);
		return !info.sysnthesisFlag || !info.state.isBefore(CompileState.GENERATE_HDL);
	}

	public boolean hasModule(String key){
		return entries.contains(key);
	}
	
	public void preprocess(){
		doGenSimplifiedAst(new IdentifierGenerator());
		loadUserHDLModules();
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
	
	public void generate(){
		makeStateMachine();
		dumpStateMachine();
		genHDLAll();
		output(OutputFormat.VHDL);
		output(OutputFormat.Verilog);
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
	
	public void dumpStateMachine(){
		for(Module m: entries.values()){
			try(PrintWriter dest = new PrintWriter(new FileOutputStream(String.format("%s_statemachine.dot", m.getName())), true)){
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
			this.state = CompileState.INITIALIZE;
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
