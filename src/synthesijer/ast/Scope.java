package synthesijer.ast;

import synthesijer.ast.statement.VariableDecl;


public interface Scope extends SynthesijerAstTree{
	
	public Scope getParentScope();
	
	public void addVariableDecl(VariableDecl decl);
	
	public Variable search(String name);
	
	public Module getModule();
	
	public Method getMethod();
	
	public void addScope(Scope s);
	
	public Variable[] getVariables();

}
