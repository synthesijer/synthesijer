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
	private Hashtable<String, HDLModule> moduleTable = new Hashtable<String, HDLModule>();
	
	private Manager(){
		addHDLModule("BlockRAM", new BlockRAM());
	}

	public void addModule(Module m){
		if(hasModule(m.getName())) return;
		entries.put(m.getName(), m);
		addHDLModule(m.getName(), new HDLModule(m.getName(), "clk", "reset"));
	}
	
	public void addHDLModule(String name, HDLModule hm){
		moduleTable.put(name, hm);
	}
	
	public Module searchModule(String name){
		return entries.get(name);
	}

	public HDLModule searchHDLModule(String name){
		return moduleTable.get(name);
	}

	public boolean hasModule(String key){
		return entries.contains(key);
	}
	
	public void generate(){
		makeCallGraph();
		try{
			makeStateMachine();
			dumpStateMachine();
			genHDL();
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
		
	public void genHDL(){
		
		for(Module m: entries.values()){
			HDLModule top = moduleTable.get(m.getName());
			m.accept(new GenerateHDLModuleVisitor(top));
		}
	}
	
	public void output(OutputFormat format) throws FileNotFoundException{
		for(HDLModule m: moduleTable.values()){
			if(format == OutputFormat.VHDL){
				System.out.printf("Output VHDL: %s.vhd\n", m.getName());
				PrintWriter dest = new PrintWriter(new FileOutputStream(String.format("%s.vhd", m.getName())), true); 
				m.genVHDL(dest);
				dest.close();
			}else{
				System.out.printf("Output Verilog HDL: %s.v\n", m.getName());
				PrintWriter dest = new PrintWriter(new FileOutputStream(String.format("%s.v", m.getName())), true); 
				m.genVerilogHDL(dest);
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
	

}
