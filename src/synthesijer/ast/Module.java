package synthesijer.ast;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import synthesijer.ast.statement.VariableDecl;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLType;
import synthesijer.hdl.literal.HDLSymbol;
import synthesijer.model.State;
import synthesijer.model.StateMachine;

public class Module implements Scope, SynthesijerAstTree{
	
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
	
	public void addVariableDecl(VariableDecl v){
		variableTable.put(v.getName(), v);
		variables.add(v);
	}
	
	public ArrayList<VariableDecl> getVariables(){
		return variables;
	}

	public ArrayList<Method> getMethods(){
		return methods;
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
		ArrayList<HDLSymbol> methodIds = new ArrayList<HDLSymbol>();
		methodIds.add(new HDLSymbol("methodId_IDLE"));
		for(Method m: methods){
			if(m.isConstructor()) continue;
			methodIds.add(new HDLSymbol(m.getUniqueName("methodID_")));
		}
		HDLType t = HDLType.genUserDefType("methodId", methodIds.toArray(new HDLSymbol[]{}), 0);
		hm.addSignal(new HDLSignal(hm, "methodId", t, HDLSignal.ResourceKind.REGISTER));
		for(Method method: methods){
			method.generateHDL(hm);
		}
		for(Method method: methods){
			method.generateHDL(hm);
		}
		for(StateMachine m: statemachines){
			hm.addStateMachine(m.genHDLSequencer(hm));
		}
		return hm;
	}
	
	public void accept(SynthesijerAstVisitor v){
		v.visitModule(this);
	}

}
