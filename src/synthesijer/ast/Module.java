package synthesijer.ast;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import synthesijer.ast.statement.VariableDecl;
import synthesijer.hdl.HDLModule;
import synthesijer.model.State;
import synthesijer.model.StateMachine;

public class Module implements Scope{
	
	private final Scope parent;
	private final String name;
	private Hashtable<String, String> importTable;
	
	private Hashtable<String, Method> methodTable = new Hashtable<String, Method>();
	private Hashtable<String, VariableDecl> variableTable = new Hashtable<String, VariableDecl>();
	private ArrayList<Method> methods = new ArrayList<Method>();
	private ArrayList<VariableDecl> variables = new ArrayList<VariableDecl>();
	
	public Module(String name, Hashtable<String, String> importTable){
		this(null, name, importTable);
	}

	public Module(Scope parent, String name, Hashtable<String, String> importTable){
		this.parent = parent;
		this.name = name;
		this.importTable = importTable; 
	}
	
	public String getName(){
		return name;
	}
	
	public Scope getParentScope(){
		return parent;
	}
	
	public void registrate(Variable v){
		// should be already registrated
	}

	public Variable search(String name){
		VariableDecl decl = variableTable.get(name);
		if(decl != null)
			return decl.getVariable();
		if(parent != null)
			return parent.search(name);
		return null;
	}
	
	public Module getModule(){
		return this;
	}
	
	public Method getMethod(){
		return null;
	}

	public void addMethod(Method m){
		methodTable.put(m.getName(), m);
		methods.add(m);
	}
	
	public void addVariable(VariableDecl v){
		variableTable.put(v.getName(), v);
		variables.add(v);
	}
	
	public void makeCallGraph(){
		for(VariableDecl v: variables){
			v.makeCallGraph();
		}
		for(Method m: methods){
			m.makeCallGraph();
		}
	}
	
	private ArrayList<StateMachine> statemachines = new ArrayList<StateMachine>();
	
	public Iterator<StateMachine> getStatemachinesIterator(){
		return statemachines.iterator();
	}
	
	public void genModuleStateMachine(){
		genInitStateMachine();
		for(Method m: methods){
			StateMachine machine = new StateMachine(m.getName());
			m.genStateMachine(machine);
			statemachines.add(machine);
		}
	}

	private void genInitStateMachine(){
		StateMachine init = new StateMachine("module_variale_declararions");
		statemachines.add(init);
		State d = init.newState("init_end", true);
		for(int i = variables.size(); i > 0; i--){
			d = variables.get(i-1).genStateMachine(init, d, null, null, null);
		}
	}
		
	public HDLModule getHDLModule(){
		HDLModule hm = new HDLModule(name, "clk", "reset");
		for(VariableDecl v: variables){
			v.genHDLSignal(hm);
		}
		for(Method method: methods){
			method.generateHDL(hm);
		}
		for(StateMachine m: statemachines){
			hm.addStateMachine(m.genHDLSequencer(hm));
		}
		return hm;
	}

	public void dumpAsXML(PrintWriter dest){
		dest.printf("<module name=\"%s\">\n", getName());
		dest.println("<variables>");
		for(VariableDecl v: variables){ v.dumpAsXML(dest); }
		dest.println("</variables>");
		dest.println("<methods>");
		for(Method m: methods){ m.dumpAsXML(dest); }
		dest.println("</methods>");
		dest.printf("</module>\n");
	}

}
