package synthesijer.ast;

public class Variable{

	private final String name;
	private final Type type;
	private final Method method;
	
	public Variable(String n, Type t, Method method){
		this.name = n;
		this.type = t;
		this.method = method;
	}
	
	public String getName(){
		return name;
	}
	
	public Type getType(){
		return type;
	}
	
	public String getUniqueName(){
		if(method != null){
			return method.getName() + "_" + name;
		}else{
			return name;
		}
	}
	
}
