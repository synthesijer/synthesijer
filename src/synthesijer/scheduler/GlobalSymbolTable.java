package synthesijer.scheduler;

import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Variable;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;

public enum GlobalSymbolTable {

	INSTANCE;

	Hashtable<String, ClassInfo> map = new Hashtable<>();

	public void add(Module m){

		ClassInfo i = new ClassInfo();
		map.put(m.getName(), i);
		for(Method method: m.getMethods()){
			i.methods.put(method.getName(), new MethodInfo(method));
		}
		for(Variable v: m.getVariables()){
			i.variables.put(v.getName(), new VariableInfo(v));
		}
	}

	public void add(String name, HDLModule m){
		ClassInfo i = new ClassInfo();
		map.put(name, i);
		for(HDLPort p: m.getPorts()){
			i.variables.put(p.getName(), new VariableInfo(p));
		}
	}

	public VariableInfo searchVariable(String klass, String name){
		ClassInfo info = map.get(klass);
		if(info == null) return null;
		VariableInfo var = info.variables.get(name);
		return var;
	}

}

class ClassInfo{

	Hashtable<String, MethodInfo> methods = new Hashtable<>();

	Hashtable<String, VariableInfo> variables = new Hashtable<>();

}

class MethodInfo{

	String name;
	ArrayList<VariableOperand> params;

	public MethodInfo(Method m){

	}

}

class VariableInfo{

	Variable var = null;
	HDLPort port = null;

	public VariableInfo(HDLPort p){
		port = p;
	}

	public VariableInfo(Variable v){
		var = v;
	}

}
