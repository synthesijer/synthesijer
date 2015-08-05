package synthesijer.scheduler;

import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.ast.Type;

public class VariableOperand implements Operand{
	
	private final String name;
	
	private final Type type;
	
	private Operand initSrc;
	
	private final boolean publicFlag;
	
	private final boolean globalConstantFlag;

	private final boolean methodParamFlag;
	
	private final String origName;
	
	private final String methodName;
	
	private final boolean privateMethodFlag;
	
	private final boolean volatileFlag;
	
	private boolean chaining = false;
	
	private boolean isExport = false;
	
	private Hashtable<SchedulerItem, SchedulerItem> predItemMap = new Hashtable<>(); // context -> predecessor
	
	public VariableOperand(String name, Type type){
		this(name, type, null, false, false, false, name, null, false, false);
	}

//	public VariableOperand(String name, Type type, Expr initExpr, boolean publicFlag, boolean globalConstantFlag, boolean methodParamFlag, String origName, String methodName, boolean privateMethodFlag, boolean volatileFlag){
	public VariableOperand(String name, Type type, Operand initSrc, boolean publicFlag, boolean globalConstantFlag, boolean methodParamFlag, String origName, String methodName, boolean privateMethodFlag, boolean volatileFlag){
		this.name = name;
		this.type = type;
		//this.initExpr = initExpr;
		this.initSrc = initSrc;
		this.publicFlag = publicFlag;
		this.globalConstantFlag = globalConstantFlag;
		this.methodParamFlag = methodParamFlag;
		this.origName = origName;
		this.methodName = methodName;
		this.privateMethodFlag = privateMethodFlag;
		this.volatileFlag = volatileFlag;
	}

	public String getName(){
		return name;
	}

	@Override
	public Type getType(){
		return type;
	}
	
//	public Expr getInitExpr(){
//	return initExpr;
//}

	public Operand getInitSrc(){
		return initSrc;
	}

	public void setInitSrc(Operand src){
		initSrc = src;
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

	public boolean isVolatileFlag(){
		return volatileFlag;
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

	public String dump(){
		String s = name
				+ ":" + type
				+ ", public=" + publicFlag 
				+ ", globalConstant=" + globalConstantFlag
				+ ", methodParam=" + methodParamFlag
				+ ", origName=" + origName
				+ ", methodName=" + methodName
				+ ", privateMethod=" + privateMethodFlag
				+ ", volatile=" + volatileFlag
				+ ", chaining=" + chaining;
		if(initSrc != null){
			s += ", init=" + initSrc.info();
		}
		return s;
	}

	public String toSexp(){
		String s = "";
		s += "(";
		s += "VAR";
		s += " " + type;
		s += " " + name;
	    s += " :public " + isPublic();
	    s += " :global_constant " + isGlobalConstant();
	    s += " :method_param " + methodParamFlag;
	    s += " :orginal " + getOrigName();
	    s += " :method " + getMethodName();
	    s += " :private_method " + isPrivateMethod();
	    s += " :volatile " + isVolatileFlag();
		s += " :chaining " + chaining;
		if(initSrc != null){
			s += " :init " + initSrc.info();
		}
		s += ")";

		return s;
	}

}
