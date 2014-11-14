package synthesijer.scheduler;

import synthesijer.ast.Type;
import synthesijer.ast.Variable;

public class VariableOperand implements Operand{
	
	private final String name;
	
	private final Type type;
	
	private final Variable var;
	
	public VariableOperand(String name, Type type){
		this(name, type, null);
	}

	public VariableOperand(String name, Type type, Variable var){
		this.name = name;
		this.type = type;
		this.var = var;
	}

	public String getName(){
		return name;
	}

	@Override
	public Type getType(){
		return type;
	}
	
	public Variable getVariable(){
		return var;
	}

	@Override
	public String info(){
		return name + ":" + type;
	}

}
