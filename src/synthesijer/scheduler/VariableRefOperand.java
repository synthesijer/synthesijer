package synthesijer.scheduler;

import synthesijer.ast.Type;

public class VariableRefOperand extends VariableOperand{
	
	private final VariableOperand ref; 
	
	public VariableRefOperand(String name, Type type, VariableOperand ref){
		super(name, type);
		this.ref = ref;
	}

	public VariableOperand getRef(){
		return ref;
	}
	
}
