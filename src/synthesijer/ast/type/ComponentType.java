package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Type;
import synthesijer.hdl.HDLType;

public class ComponentType implements Type{
	
	private final String name;
	
	public ComponentType(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public HDLType getHDLType(){
		System.err.println("unsupported type: " + this);
		return null;
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitComponentType(this);
	}
}
