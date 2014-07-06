package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstTypeVisitor;
import synthesijer.ast.Type;

public class ComponentType implements Type{
	
	private final String name;
	
	public ComponentType(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void accept(SynthesijerAstTypeVisitor v){
		v.visitComponentType(this);
	}
}
