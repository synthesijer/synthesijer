package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Type;

public class ComponentType implements Type{
	
	private final String name;
	
	public ComponentType(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void accept(SynthesijerAstVisitor v){
		v.visitComponentType(this);
	}
}
