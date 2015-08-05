package synthesijer.scheduler;

import synthesijer.ast.Type;

public class VariableRefOperand extends VariableOperand{
	
	private final VariableOperand ref; 
	
	public VariableRefOperand(String name, Type type, VariableOperand ref, boolean memberFlag){
		super(name, type, memberFlag);
		this.ref = ref;
	}

	public VariableOperand getRef(){
		return ref;
	}
	
}
