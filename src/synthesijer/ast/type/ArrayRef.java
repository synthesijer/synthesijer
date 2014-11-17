package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstTypeVisitor;
import synthesijer.ast.Type;

public class ArrayRef implements Type{
	
	private final ArrayType refType;
	
	public ArrayRef(ArrayType k){
		refType = k;
	}
	
	public ArrayType getRefType(){
		return refType;
	}
	
	public void accept(SynthesijerAstTypeVisitor v){
		v.visitArrayRef(this);
	}
	
	public String toString(){
		return "ArrayRef::" + refType;
	}

}
