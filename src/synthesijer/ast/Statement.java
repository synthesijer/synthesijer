package synthesijer.ast;

public abstract class Statement implements SynthesijerAstTree{
	
	private final Scope scope; 
	
	public Statement(Scope scope){
		this.scope = scope;
	}
	
	public Scope getScope(){
		return scope;
	}

}
