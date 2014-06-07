package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Type;

public class ArrayType implements Type{
	
	private final Type elemType;
	
	public ArrayType(Type k){
		elemType = k;
	}
	
	public Type getElemType(){
		return elemType;
	}
	
	public void accept(SynthesijerAstVisitor v){
		v.visitArrayType(this);
	}

}
