package synthesijer.ast;

public class Variable{

	private final String name;
	private final Type type;
	private final Method method;
	private final Expr init;
	private boolean flagGlobalConstant;
	private boolean flagPublic;
	
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
	
	public void setGlobalConstant(boolean f){
		flagGlobalConstant = f;
	}

	public boolean isGlobalConstant(){
		return flagGlobalConstant;
	}

	public void setPublic(boolean f){
		flagPublic = f;
	}

	public boolean isPublic(){
		return flagPublic;
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
