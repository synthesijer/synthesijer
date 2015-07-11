package synthesijer.scheduler;

import synthesijer.ast.Type;

public class InstanceRefOperand extends VariableOperand{
	
	public final String className;
	
	public InstanceRefOperand(String name, Type type, String className){
		super(name, type);
		this.className = className;
	}

		
}
