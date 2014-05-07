package synthesijer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import synthesijer.ast.Module;
import synthesijer.hdl.HDLModule;
import synthesijer.model.StateMachine;

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

	public void makeStateMachine() throws FileNotFoundException{
		for(Module m: entries){
			m.genModuleStateMachine();
			Iterator<StateMachine> it = m.getStatemachinesIterator();
			PrintWriter dest = new PrintWriter(new FileOutputStream(String.format("%s_statemachine.dot", m.getName())), true);
			dest.printf("digraph " + m.getName() + "{\n");
			while(it.hasNext()){
				StateMachine machine = it.next();
				machine.dumpAsDot(dest);
			}
			dest.printf("}\n");
			dest.close();
		}
	}
	
	enum OutputFormat { Verilog, VHDL; };
	
	public void genHDL(OutputFormat format) throws FileNotFoundException{
		for(Module m: entries){
			HDLModule top = m.getHDLModule();
			if(format == OutputFormat.VHDL){
				PrintWriter dest = new PrintWriter(new FileOutputStream(String.format("%s.vhd", m.getName())), true); 
				top.dumpAsVHDL(dest);
				dest.close();
			}else{
				PrintWriter dest = new PrintWriter(new FileOutputStream(String.format("%s.v", m.getName())), true); 
				top.dumpAsVerilogHDL(dest);
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
