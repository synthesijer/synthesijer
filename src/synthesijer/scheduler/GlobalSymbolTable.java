package synthesijer.scheduler;

import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Variable;

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
	
	VariableOperand var;
	
	public VariableInfo(Variable v){
		var = new VariableOperand(v.getName(), v.getType(), v);
	}
	
}