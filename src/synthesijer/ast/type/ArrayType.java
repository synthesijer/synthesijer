package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstTypeVisitor;
import synthesijer.ast.Type;

public class ArrayType implements Type{
	
	private final Type elemType;
	
	public ArrayType(Type k){
		elemType = k;
	}
	
	public Type getElemType(){
		return elemType;
	}
	
	public void accept(SynthesijerAstTypeVisitor v){
		v.visitArrayType(this);
	}
	
	public String toString(){
		return "ArrayType::" + elemType;
	}

}
