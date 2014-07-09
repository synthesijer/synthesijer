package synthesijer.hdl;


public class HDLParameter implements HDLTree{
	
	private final String name;
	private final HDLPrimitiveType type;
	private final String defaultValue;
	private final String value;
	
	public HDLParameter(String name, HDLPrimitiveType type, String defaultValue, String value){
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
	
	public String getDefaultValue(){
		return defaultValue;
	}
	
	public String getValue(){
		return value;
	}

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLParameter(this);		
	}
}
