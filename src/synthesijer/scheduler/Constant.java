package synthesijer.scheduler;

import synthesijer.ast.Type;

public class Constant implements Operand{
	
	private final String value;
	
	private final Type type;
	
	public Constant(String value, Type type){
		this.value = value;
		this.type = type;
	}
	
	@Override
	public Type getType(){
		return type;
	}
	
	@Override
	public String info(){
		return value + ":" + type;
	}


}
