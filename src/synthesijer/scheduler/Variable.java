package synthesijer.scheduler;

import synthesijer.ast.Type;

public class Variable implements Operand{
	
	private final String name;
	
	private final Type type;
	
	public Variable(String name, Type type){
		this.name = name;
		this.type = type;
	}
	
	@Override
	public Type getType(){
		return type;
	}

	@Override
	public String info(){
		return name + ":" + type;
	}

}
