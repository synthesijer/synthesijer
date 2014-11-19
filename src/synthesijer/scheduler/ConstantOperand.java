package synthesijer.scheduler;

import synthesijer.ast.Type;

public class ConstantOperand implements Operand{
	
	private final String value;
	
	private Type type;
	
	public ConstantOperand(String value, Type type){
		this.value = value;
		this.type = type;
	}
	
	@Override
	public Type getType(){
		return type;
	}
	
	public void setType(Type t){
		this.type = t;
	}
	
	public String getValue(){
		return value;
	}
	
	@Override
	public String info(){
		return value + ":" + type;
	}


}
