package synthesijer.hdl;

import synthesijer.hdl.expr.HDLValue;

public class HDLParameter implements HDLTree{
	
	private final String name;
	private final HDLPrimitiveType type;
	private final HDLValue defaultValue;
	private final HDLValue value;
	
	public HDLParameter(String name, HDLPrimitiveType type, HDLValue defaultValue, HDLValue value){
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.value = value;
	}

	public String getName(){
		return name;
	}
	
	public HDLType getType(){
		return type;
	}
	
	public HDLValue getDefaultValue(){
		return defaultValue;
	}
	
	public HDLValue getValue(){
		return value;
	}

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLParameter(this);		
	}
}
