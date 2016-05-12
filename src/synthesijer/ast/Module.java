package synthesijer.ast;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;

import synthesijer.Manager;
import synthesijer.SynthesijerUtils;
import synthesijer.UnknownModuleException;
import synthesijer.ast.expr.Ident;
import synthesijer.ast.expr.MethodInvocation;
import synthesijer.ast.statement.ExprStatement;
import synthesijer.ast.statement.VariableDecl;
import synthesijer.ast.type.PrimitiveTypeKind;

public class Module implements Scope, SynthesijerAstTree{
	
    
    // properties
    private final Scope parent;
	private final String name;
	private final Hashtable<String, String> importTable;
	private final String extending;
	private final ArrayList<String> implementing;

    // inner data
    private LinkedHashMap<String, Method> methodTable = new LinkedHashMap<>();
    private LinkedHashMap<String, Variable> variableTable = new LinkedHashMap<>();
    private ArrayList<VariableDecl> variableDecls = new ArrayList<>();
    
    private boolean synthesijerHDLFlag = false;
	private final Method moduleInitMethod;
	
    private ArrayList<Scope> scopes = new ArrayList<>();
	
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
		//this.moduleInitMethod = new Method(this, "synthesijer_class_init_" + name, PrimitiveTypeKind.VOID);
		//this.moduleInitMethod.setPrivateFlag(true);
		this.moduleInitMethod = null;
	}
	
    public Hashtable<String, String> getImportTable(){
        return importTable;
    }
    
    public ArrayList<String> getImplementingList(){
        return implementing;
    }

	public void addScope(Scope s){
		scopes.add(s);
	}
	
	public Scope[] getChildScope(){
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
		return moduleInitMethod;
	}

	public void addMethod(Method m){
		methodTable.put(m.getName(), m);
	}
	
	public Method searchMethod(String name){
		return methodTable.get(name);
	}
	
	public void addVariableDecl(VariableDecl v){
		variableTable.put(v.getName(), v.getVariable());
		variableDecls.add(v);
	}
	
	public VariableDecl[] getVariableDecls(){
		return variableDecls.toArray(new VariableDecl[]{});
	}

	public Variable[] getVariables(){
		return variableTable.values().toArray(new Variable[]{});
	}

	public Method[] getMethods(){
		return methodTable.values().toArray(new Method[]{});
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
		if(getExtending().equals("HDLModule")){
			// skip
		}else if(getExtending().equals("Thread")){ //TODO experimental
			addThread(this);
		}else{
			System.out.println("extends: " + getExtending());
			Module ext;
			try{
				ext = Manager.INSTANCE.searchModule(getExtending());
			}catch(UnknownModuleException e){
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
				if(!variableTable.containsKey(v.getName())){
					addVariableDecl(v);
				}
			}
		}
	}
	
	public boolean isSynthesijerHDL(){
		return this.synthesijerHDLFlag;
	}

	public void setSynthesijerHDL(boolean flag){
		this.synthesijerHDLFlag = flag;
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
