package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstTypeVisitor;
import synthesijer.ast.Type;

public class ComponentRef implements Type{
	
	private final ComponentType baseType;
	private final Type refType;
	
	public ComponentRef(ComponentType k, Type t){
		baseType = k;
		refType = t;
	}
	
	public ComponentType getBaseType(){
		return baseType;
	}
	
	public Type getRefType(){
		return refType;
	}
	
	public void accept(SynthesijerAstTypeVisitor v){
		v.visitComponentRef(this);
	}
	
	public String toString(){
		return "ComponentRef::" + refType;
	}

}
