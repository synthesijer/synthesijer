package synthesijer.ast;

public class Variable{

	private final String name;
	private final Type type;
	private final Method method;
	private final Expr init;
	
	public Variable(String n, Type t, Method method, Expr init){
		this.name = n;
		this.type = t;
		this.method = method;
		this.init = init;
	}
	
	public String getName(){
		return name;
	}
	
	public Type getType(){
		return type;
	}
	
	public Expr getInitExpr(){
		return init;
	}
	
	public String getUniqueName(){
		if(method != null){
			return method.getName() + "_" + name;
		}else{
			return name;
		}
	}
	
	public String toString(){
		return "Varialble: " + getUniqueName();
	}
	
}
