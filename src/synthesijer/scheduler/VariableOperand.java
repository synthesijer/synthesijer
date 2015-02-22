package synthesijer.scheduler;

import synthesijer.ast.Expr;
import synthesijer.ast.Type;

public class VariableOperand implements Operand{
	
	private final String name;
	
	private final Type type;
	
	private final Expr initExpr;
	
	private final boolean publicFlag;
	
	private final boolean globalConstantFlag;

	private final boolean methodParamFlag;
	
	private final String origName;
	
	private final String methodName;
	
	private final boolean privateMethodFlag;
	
	public VariableOperand(String name, Type type){
		this(name, type, null, false, false, false, name, null, false);
	}

	public VariableOperand(String name, Type type, Expr initExpr, boolean publicFlag, boolean globalConstantFlag, boolean methodParamFlag, String origName, String methodName, boolean privateMethodFlag){
		this.name = name;
		this.type = type;
		this.initExpr = initExpr;
		this.publicFlag = publicFlag;
		this.globalConstantFlag = globalConstantFlag;
		this.methodParamFlag = methodParamFlag;
		this.origName = origName;
		this.methodName = methodName;
		this.privateMethodFlag = privateMethodFlag;
	}

	public String getName(){
		return name;
	}

	@Override
	public Type getType(){
		return type;
	}
	
	public Expr getInitExpr(){
		return initExpr;
	}
	
	public boolean isPublic(){
		return publicFlag;
	}
	
	public boolean isGlobalConstant(){
		return globalConstantFlag;
	}

	public boolean isMethodParam(){
		return methodParamFlag;
	}
	
	public String getOrigName(){
		return origName;
	}

	public String getMethodName(){
		return methodName;
	}
	
	public boolean isPrivateMethod(){
		return privateMethodFlag;
	}

	@Override
	public String info(){
		return name + ":" + type;
	}

}
