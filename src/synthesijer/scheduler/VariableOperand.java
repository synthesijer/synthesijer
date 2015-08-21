package synthesijer.scheduler;

import java.util.Enumeration;
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
	
	private final boolean memberFlag;
	
	private Hashtable<SchedulerItem, SchedulerItem> predItemMap = new Hashtable<>(); // context -> predecessor
	
	public VariableOperand(String name, Type type, boolean memberFlag){
		this(name, type, null, false, false, false, name, null, false, false, memberFlag);
	}

	public VariableOperand(
			String name,
			Type type,
			Operand initSrc,
			boolean publicFlag,
			boolean globalConstantFlag,
			boolean methodParamFlag,
			String origName,
			String methodName,
			boolean privateMethodFlag,
			boolean volatileFlag,
			boolean memberFlag
			){
		this.name = name;
		this.type = type;
		this.initSrc = initSrc;
		this.publicFlag = publicFlag;
		this.globalConstantFlag = globalConstantFlag;
		this.methodParamFlag = methodParamFlag;
		this.origName = origName;
		this.methodName = methodName;
		this.privateMethodFlag = privateMethodFlag;
		this.volatileFlag = volatileFlag;
		this.memberFlag = memberFlag;
	}

	public String getName(){
		return name;
	}

	@Override
	public Type getType(){
		return type;
	}
	
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

	public boolean isMember(){
		return memberFlag;
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
	    if(chaining){
	    	String sep = "";
	    	s += " :chaining (";
	    	Enumeration<SchedulerItem> e = predItemMap.keys();
	    	while(e.hasMoreElements()){
	    		SchedulerItem ctx = e.nextElement();
	    		SchedulerItem pred = predItemMap.get(ctx);
	    		s += sep + "(" + ctx.getBoardName() + " " + ctx.getStepId() + " " + pred.getBoardName()+" "+pred.getStepId() + ")";
	    		sep = " ";
	    	}
			s += ")";
		}
		s += " :member " + isMember();
		if(initSrc != null){
			//s += " :init " + initSrc.toSexp();
			s += " :init (REF";
			if(initSrc instanceof VariableOperand){ s += " VAR "; }
			else if(initSrc instanceof ArrayRefOperand){ s += " ARRAY "; }
			else if(initSrc instanceof InstanceRefOperand){ s += " INSTANCE "; }
			else if(initSrc instanceof ConstantOperand){ s += " CONSTANT "; }
			else{s += " UNKNOWN "; }
			s += initSrc.getName();
			s += ")";
		}
		s += ")";

		return s;
	}

}
