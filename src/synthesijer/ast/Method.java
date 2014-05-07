package synthesijer.ast;

import java.io.PrintWriter;
import java.util.Hashtable;

import synthesijer.SynthesijerUtils;
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

public class Method implements Scope, SynthsijerAstTree{
	
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
	
	public void setAutoFlag(boolean f){
		autoFlag = f;
	}
	
	public void setSynchronizedFlag(boolean f){
		synchronizedFlag = f;
	}
	
	public void setPrivateFlag(boolean f){
		privateFlag = f;
	}
	
	public void setRawFlag(boolean f){
		rawFlag = f;
	}
	
	public void setCombinationFlag(boolean f){
		combinationFlag = f;
	}
	
	public void setParallelFlag(boolean f){
		parallelFlag = f;
	}
	
	public void setNoWaitFlag(boolean f){
		noWaitFlag = f;
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
	
	public BlockStatement getBody(){
		return body;
	}
	
	public void makeCallGraph(){
		body.makeCallGraph();
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
	
	public void dumpAsXML(PrintWriter dest){
		String options = "";
		options += " unsynthesizableFlag=\"%b\"";
		options += " autoFlag=\"%b\"";
		options += " synchronizedFlag=\"%b\"";
		options += " privateFlag=\"%b\"";
		options += " rawFlag=\"%b\"";
		options += " combinationFlag=\"%b\"";
		options += " parallelFlag=\"%b\"";
		options += " noWaitFlag=\"%b\"";
		options += " constructorFlag=\"%b\"";
		
		dest.printf("<method name=\"%s\"" + options + ">\n",
					SynthesijerUtils.escapeXML(name),
					unsynthesizableFlag,
					autoFlag,
					synchronizedFlag,
					privateFlag,
					rawFlag,
					combinationFlag,
					parallelFlag,
					noWaitFlag,
					constructorFlag);
		type.dumpAsXML(dest);
		body.dumpAsXML(dest);
		dest.printf("</method>\n");
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
			HDLPort port = new HDLPort(name + "_return_out", HDLPort.DIR.OUT, t);
			m.addPort(port);
			port.setSrcSignal(getHDLReturnSignal(m, name + "_return", t));
			m.addSignal(getHDLReturnSignal(m, name + "_return", t));
		}else if(type instanceof ArrayType){
			System.err.println("unsupported type: " + type);
		}else if(type instanceof ComponentType){
			System.err.println("unsupported type: " + type);
		}else{
			System.err.printf("unkonw type: %s(%s)\n", type, type.getClass());
		}
	}
	

}
