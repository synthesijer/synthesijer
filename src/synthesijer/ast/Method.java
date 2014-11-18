package synthesijer.ast;

import java.util.Hashtable;

import synthesijer.ast.statement.BlockStatement;
import synthesijer.ast.statement.VariableDecl;
import synthesijer.model.State;
import synthesijer.model.Statemachine;

public class Method implements Scope, SynthesijerAstTree{
	
	private final Scope parent;
	private final String name;
	private final Type type;
	
	private boolean unsynthesizableFlag;
	private boolean autoFlag;
	private boolean synchronizedFlag;
	private boolean privateFlag;
	private boolean rawFlag;
	private boolean combinationFlag;
	private boolean parallelFlag;
	private boolean noWaitFlag;
	private boolean constructorFlag;
	
	private Statemachine stateMachine;
	
	private VariableDecl[] args;
	private final BlockStatement body;
	
	private Hashtable<String, Variable> varTable = new Hashtable<>();
	
	private int uniq_id;
	
	// A method, for which this method must wait. 
	private Method waitWith;
	
	public Method(Scope parent, String name, Type type){
		this.parent = parent;
		this.name = name;
		this.type = type;
		this.body = new BlockStatement(this);
		parent.addScope(this);
		args = new VariableDecl[]{}; // dummy
		uniq_id = 0;
	}
	
	public void addScope(Scope s){
		parent.addScope(s);
	}
	
	public Scope getParentScope(){
		return parent;
	}
	
	public Module getModule(){
		return parent.getModule();
	}
	
	public Method getMethod(){
		return this;
	}
	
	public void setArgs(VariableDecl[] args){
		this.args = args;
	}
	
	public VariableDecl[] getArgs(){
		return args;
	}
	
	public void setUnsynthesizableFlag(boolean f){
		unsynthesizableFlag = f;
	}
	
	public boolean isUnsynthesizable(){
		return unsynthesizableFlag;
	}
	
	public void setAutoFlag(boolean f){
		autoFlag = f;
	}

	public boolean isAuto(){
		return autoFlag;
	}

	public void setSynchronizedFlag(boolean f){
		synchronizedFlag = f;
	}

	public boolean isSynchronized(){
		return synchronizedFlag;
	}

	public void setPrivateFlag(boolean f){
		privateFlag = f;
	}
	
	public boolean isPrivate(){
		return privateFlag;
	}
	
	public void setRawFlag(boolean f){
		rawFlag = f;
	}
	
	public boolean isRaw(){
		return rawFlag;
	}
	
	public void setCombinationFlag(boolean f){
		combinationFlag = f;
	}
	
	public boolean isCombination(){
		return combinationFlag;
	}
	
	public void setParallelFlag(boolean f){
		parallelFlag = f;
	}
	
	public boolean isParallel(){
		return parallelFlag;
	}
	
	public void setNoWaitFlag(boolean f){
		noWaitFlag = f;
	}
	
	public boolean isNoWait(){
		return noWaitFlag;
	}
	
	public void setConstructorFlag(boolean f){
		constructorFlag = f;
	}
	
	public boolean isConstructor(){
		return constructorFlag;
	}
	
	public void setWaitWithMethod(Method method){
		this.waitWith = method;
	}
		
	public Method getWaitWithMethod(){
		return waitWith;
	}
		
	public String getName(){
		if(constructorFlag) return getModule().getName();
		else return name;
	}
	
	public String getUniqueName(){
		return name;
	}
	
	public Type getType(){
		return type;
	}
	
	public BlockStatement getBody(){
		return body;
	}
		
	public void addVariableDecl(VariableDecl v){
		varTable.put(v.getVariable().getName(), v.getVariable());
	}
	
	public Variable[] getVariables(){
		return varTable.values().toArray(new Variable[]{});
	}
	
	public Variable search(String name){
		Variable v = varTable.get(name);
		if(v != null) return v;
		return parent.search(name);
	}
	
	public void genStateMachine(){
		stateMachine = new Statemachine(getUniqueName());
		State terminal = stateMachine.newState("function_exit", true);
		State s = body.genStateMachine(stateMachine, terminal, terminal, null, null);
		stateMachine.setEntryState(s);
	}
	
	public Statemachine getStateMachine(){
		return stateMachine;
	}
	
	@Override
	public void accept(SynthesijerAstVisitor v){
		v.visitMethod(this);
	}

	public void accept(SynthesijerMethodVisitor v){
		v.visitMethod(this);
	}
	
	public int getUniqId(){
		int v = uniq_id;
		uniq_id++;
		return v;
	}

}
