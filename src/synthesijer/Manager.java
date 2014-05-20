package synthesijer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.ast.Module;
import synthesijer.ast2hdl.GenerateHDLModuleVisitor;
import synthesijer.hdl.HDLModule;

public enum Manager {
	
	INSTANCE;
	
	private Hashtable<String, Module> entryTable = new Hashtable<String, Module>();
	private ArrayList<Module> entries = new ArrayList<Module>();
	
	public void addModule(Module m){
		if(hasModule(m.getName())) return;
		entries.add(m);
		entryTable.put(m.getName(), m);
	}
	
	public boolean hasModule(String key){
		return entryTable.contains(key);
	}
	
	public void generate(){
		makeCallGraph();
		try{
			makeStateMachine();
			dumpStateMachine();
			genHDL(OutputFormat.VHDL);
			genHDL(OutputFormat.Verilog);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void makeCallGraph(){
		MakeCallFlowVisitor visitor = new MakeCallFlowVisitor();
		for(Module m: entries){
			m.accept(visitor);
		}
	}

	public void makeStateMachine(){
		for(Module m: entries){
			m.genStateMachine();
		}
	}
	
	public void dumpStateMachine() throws FileNotFoundException{
		for(Module m: entries){
			PrintWriter dest = new PrintWriter(new FileOutputStream(String.format("%s_statemachine.dot", m.getName())), true);
			m.accept(new DumpStatemachineVisitor(dest));
			dest.close();
		}
	}

	
	enum OutputFormat { Verilog, VHDL; };
	
	public void genHDL(OutputFormat format) throws FileNotFoundException{
		for(Module m: entries){
			HDLModule top = new HDLModule(m.getName(), "clk", "reset");
			m.accept(new GenerateHDLModuleVisitor(top));
			if(format == OutputFormat.VHDL){
				PrintWriter dest = new PrintWriter(new FileOutputStream(String.format("%s.vhd", m.getName())), true); 
				top.genVHDL(dest);
				dest.close();
			}else{
				PrintWriter dest = new PrintWriter(new FileOutputStream(String.format("%s.v", m.getName())), true); 
				top.genVerilogHDL(dest);
				dest.close();
			}
		}
	}

	public void dumpAsXML(PrintWriter dest){
		DumpAsXMLVisitor visitor = new DumpAsXMLVisitor(dest);
		dest.printf("<?xml version=\"1.0\" ?>\n");
		dest.printf("<modules>\n");
		for(Module m: entries){
			m.accept(visitor);
		}
		dest.printf("</modules>\n");
	}
	

}
