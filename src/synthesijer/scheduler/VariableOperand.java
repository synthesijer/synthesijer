package synthesijer.scheduler;

import java.util.Hashtable;

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
	
	private boolean chaining = false;
	
	private Hashtable<SchedulerItem, SchedulerItem> predItemMap = new Hashtable<>(); // context -> predecessor
	
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
	
	public void setChaining(SchedulerItem ctx, SchedulerItem pred){
		chaining = true;
		predItemMap.put(ctx, pred);
	}
	
	@Override
	public boolean isChaining(SchedulerItem ctx){
		return predItemMap.containsKey(ctx);
	}

	public SchedulerItem getPredItem(SchedulerItem ctx){
		return predItemMap.get(ctx);
	}

	@Override
	public String info(){
		return name + ":" + type;
	}

}
