package synthesijer.scheduler;

import java.util.ArrayList;

import synthesijer.ast.Type;

public class InstanceRefOperand implements Operand{
	
	public final String className;
	
	private final ArrayList<ParamPair> parameters = new ArrayList<>();
	
	private final String name;
	
	private final Type type;
	
	public InstanceRefOperand(String name, Type type, String className){
		this.name = name;
		this.type = type;
		this.className = className;
	}
	
	public void addParameter(String key, String value){
		this.parameters.add(new ParamPair(key, value));
	}

	public ArrayList<ParamPair> getParameters(){
		return parameters;
	}

	class ParamPair{
		public final String key;
		public final String value;
		public ParamPair(String k, String v){
			this.key = k;
			this.value = v;
		}
	}
	
	@Override
	public boolean isChaining(SchedulerItem ctx) {
		return false;
	};
		
	@Override
	public Type getType(){
		return type;
	};

	@Override
	public String info(){
		String s = "InstanceRef<" + className + ">(";
		String sep = "";
		for(ParamPair p: parameters){
			s += sep + p.key + "->" + p.value;
			sep = ", ";
		}
		s += ")";
		return s;
	}

}
