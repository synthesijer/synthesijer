package synthesijer.ast;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.management.RuntimeErrorException;

import synthesijer.Manager;
import synthesijer.SynthesijerUtils;
import synthesijer.ast.expr.Ident;
import synthesijer.ast.expr.MethodInvocation;
import synthesijer.ast.statement.ExprStatement;
import synthesijer.ast.statement.VariableDecl;
import synthesijer.ast.type.PrimitiveTypeKind;
import synthesijer.model.State;
import synthesijer.model.Statemachine;

public class Module implements Scope, SynthesijerAstTree{
	
	private final Scope parent;
	private final String name;
	private Hashtable<String, String> importTable;
	private final String extending;
	private final ArrayList<String> implementing;
	
	private Hashtable<String, Method> methodTable = new Hashtable<>();
	private Hashtable<String, Variable> variableTable = new Hashtable<>();
	private ArrayList<Method> methods = new ArrayList<>();
	private ArrayList<VariableDecl> variables = new ArrayList<>();
	private ArrayList<Scope> scopes = new ArrayList<>();
	
	private Statemachine statemachine;
	
	public Module(String name, Hashtable<String, String> importTable, String extending, ArrayList<String> implementing){
		this(null, name, importTable, extending, implementing);
	}

	public Module(Scope parent, String name, Hashtable<String, String> importTable, String extending, ArrayList<String> implementing){
		this.parent = parent;
		this.name = name;
		this.importTable = importTable;
		this.extending = extending;
		this.implementing = implementing;
		scopes.add(this);
	}

	public void addScope(Scope s){
		scopes.add(s);
	}
	
	public Scope[] getScope(){
		return scopes.toArray(new Scope[]{});
	}
	
	public String getName(){
		return name;
	}
	
	public Scope getParentScope(){
		return parent;
	}
	
	public String getExtending(){
		return extending;
	}
	
	public Variable search(String name){
		Variable var = variableTable.get(name);
		if(var != null)
			return var;
		if(parent != null)
			return parent.search(name);
		return null;
	}
	
	public Module getModule(){
		return this;
	}
	
	public Method getMethod(){
		return null;
	}

	public void addMethod(Method m){
		methodTable.put(m.getName(), m);
		methods.add(m);
	}
	
	public Method searchMethod(String name){
		return methodTable.get(name);
	}
	
	public void addVariableDecl(VariableDecl v){
		variableTable.put(v.getName(), v.getVariable());
		variables.add(v);
	}
	
	public VariableDecl[] getVariableDecls(){
		return variables.toArray(new VariableDecl[]{});
	}

	public Variable[] getVariables(){
		return variableTable.values().toArray(new Variable[]{});
	}

	public ArrayList<Method> getMethods(){
		return methods;
	}	
	
	public void genStateMachine(){
		genInitStateMachine();
		for(Method m: methods){
			m.genStateMachine();
		}
	}

	private void genInitStateMachine(){
		statemachine = new Statemachine("module_variale_declararions");
		State d = statemachine.newState("init_end", true);
		for(int i = variables.size(); i > 0; i--){
			d = variables.get(i-1).genStateMachine(statemachine, d, null, null, null);
		}
	}
	
	public void accept(SynthesijerModuleVisitor v){
		v.visitModule(this);
	}

	@Override
	public void accept(SynthesijerAstVisitor v) {
		v.visitModule(this);
	}

	public void resolveExtends(){
		if(getExtending() == null) return;
		if(getExtending().equals("Thread")){ //TODO experimental
			addThread(this);
		}else{
			System.out.println("exnteds: " + getExtending());
			Module ext = Manager.INSTANCE.searchModule(getExtending());
			if(ext == null){
				SynthesijerUtils.error("cannot find the extending class:" + getExtending());
				throw new RuntimeException("cannot find the extending class:" + getExtending());
			}
			ext.resolveExtends();
			for(Method m: ext.getMethods()){
				if(!methodTable.containsKey(m.getName())){
					addMethod(m);
				}
			}
			for(VariableDecl v: ext.getVariableDecls()){
				if(!variableTable.contains(v.getName())){
					addVariableDecl(v);
				}
			}
		}
	}

	// TODO experimental
	private void addThread(Module m){
		// start
		Method start = new Method(m, "start", PrimitiveTypeKind.VOID);
		m.addMethod(start);
		MethodInvocation tmp = new MethodInvocation(start);
		Ident run = new Ident(start);
		run.setIdent("run");
		tmp.setMethod(run);
		start.getBody().addStatement(new ExprStatement(start, tmp));
		start.setNoWaitFlag(true);

		// join
		Method join = new Method(m, "join", PrimitiveTypeKind.VOID);
		m.addMethod(join);
		join.setWaitWithMethod(start); // "join" must wait for "start".
		
		Method yield = new Method(m, "yield", PrimitiveTypeKind.VOID);
		m.addMethod(yield);
	}

}
