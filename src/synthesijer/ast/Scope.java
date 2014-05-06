package synthesijer.ast;


public interface Scope {
	
	public Scope getParentScope();
	
	public void registrate(Variable v);
	
	public Variable search(String name);
	
	public Module getModule();
	
	public Method getMethod();

}
