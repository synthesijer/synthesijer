package synthesijer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;

import synthesijer.ast.Module;
import synthesijer.ast2hdl.GenerateHDLModuleVisitor;
import synthesijer.hdl.HDLModule;
import synthesijer.lib.BlockRAM;

public enum Manager {
	
	INSTANCE;
	
	private Hashtable<String, Module> entries = new Hashtable<String, Module>();
	private Hashtable<String, HDLModuleInfo> moduleTable = new Hashtable<String, HDLModuleInfo>();
		
	private Manager(){
		addHDLModule("BlockRAM", null, new BlockRAM(), false);
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
		return moduleTable.get(name);
	}

	public boolean isGeneratedHDLModule(String name){
		HDLModuleInfo info = moduleTable.get(name);
		return !info.sysnthesisFlag || !info.state.isBefore(CompileState.GENERATE_HDL);
	}

	public boolean hasModule(String key){
		return entries.contains(key);
	}
	
	public void generate(){
		makeCallGraph();
		try{
			makeStateMachine();
			dumpStateMachine();
			genHDLAll();
			output(OutputFormat.VHDL);
			output(OutputFormat.Verilog);
		}catch(Exception e){
			e.printStackTrace();
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
	
	public void dumpStateMachine() throws FileNotFoundException{
		for(Module m: entries.values()){
			PrintWriter dest = new PrintWriter(new FileOutputStream(String.format("%s_statemachine.dot", m.getName())), true);
			m.accept(new DumpStatemachineVisitor(dest));
			dest.close();
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
			info.m.accept(new GenerateHDLModuleVisitor(info.hm));
			info.state = CompileState.GENERATE_HDL;
		}
	}
	
	public void output(OutputFormat format) throws FileNotFoundException{
		for(Module m: entries.values()){
			HDLModule hm = moduleTable.get(m.getName()).hm;
			if(format == OutputFormat.VHDL){
				System.out.printf("Output VHDL: %s.vhd\n", m.getName());
				PrintWriter dest = new PrintWriter(new FileOutputStream(String.format("%s.vhd", m.getName())), true); 
				hm.genVHDL(dest);
				dest.close();
			}else{
				System.out.printf("Output Verilog HDL: %s.v\n", m.getName());
				PrintWriter dest = new PrintWriter(new FileOutputStream(String.format("%s.v", m.getName())), true); 
				hm.genVerilogHDL(dest);
				dest.close();
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
