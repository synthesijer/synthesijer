package synthesijer.ast;

import java.util.Hashtable;

import synthesijer.ast.statement.BlockStatement;
import synthesijer.ast.statement.VariableDecl;
import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.ComponentType;
import synthesijer.ast.type.PrimitiveTypeKind;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLType;
import synthesijer.model.State;
import synthesijer.model.StateMachine;

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
	
	private VariableDecl[] args;
	private final BlockStatement body;
	
	private Hashtable<String, Variable> varTable = new Hashtable<String, Variable>();	
	
	public Method(Scope parent, String name, Type type){
		this.parent = parent;
		this.name = name;
		this.type = type;
		this.body = new BlockStatement(this);
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
		
	public String getName(){
		if(constructorFlag) return getModule().getName();
		else return name;
	}
	
	public String getUniqueName(String prefix){
		return prefix + name;
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
	
	public Variable search(String name){
		Variable v = varTable.get(name);
		if(v != null) return v;
		return parent.search(name);
	}
	
	public void genStateMachine(StateMachine s){
		State terminal = s.newState("function_exit", true);
		body.genStateMachine(s, terminal, terminal, null, null);
	}
	
	public void accept(SynthesijerAstVisitor v){
		v.visitMethod(this);
	}
		
	public void generateHDL(HDLModule m){
		for(VariableDecl v: args){
			v.genHDLPort(m);
		}
		genMethodReturnPort(m);
		body.generateHDL(m);
	}
	
	private HDLSignal hdlSignal = null;
	public HDLSignal getHDLReturnSignal(HDLModule m, String name, HDLType t){
		if(hdlSignal == null){
			hdlSignal = new HDLSignal(m, name, t, HDLSignal.ResourceKind.REGISTER);
		}
		return hdlSignal;
	}
	
	public HDLSignal getHDLReturnSignal(){
		return hdlSignal;
	}
	
	private void genMethodReturnPort(HDLModule m){
		if(type == PrimitiveTypeKind.VOID) return;
		HDLType t = type.getHDLType();
		if(type instanceof PrimitiveTypeKind){
			m.newPort(name + "_return", HDLPort.DIR.OUT, t);
		}else if(type instanceof ArrayType){
			System.err.println("unsupported type: " + type);
		}else if(type instanceof ComponentType){
			System.err.println("unsupported type: " + type);
		}else{
			System.err.printf("unkonw type: %s(%s)\n", type, type.getClass());
		}
	}
	

}
