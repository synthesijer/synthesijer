package synthesijer.scheduler;

import java.util.ArrayList;

import synthesijer.ast.Type;

public class InstanceRefOperand extends VariableOperand{
	
	public final String className;
	
	private final ArrayList<ParamPair> parameters = new ArrayList<>();
	
	public InstanceRefOperand(String name, Type type, String className){
		super(name, type);
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

}
