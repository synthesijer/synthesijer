package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Type;
import synthesijer.hdl.HDLPrimitiveType;

public class ArrayType implements Type{
	
	private final Type elemType;
	
	public ArrayType(Type k){
		elemType = k;
	}
	
	public Type getElemType(){
		return elemType;
	}
	
	public HDLPrimitiveType getHDLType(){
		System.err.println("unsupported type: " + this);
		return null;
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitArrayType(this);
	}

}
