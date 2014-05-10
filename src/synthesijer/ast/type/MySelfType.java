package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Type;
import synthesijer.hdl.HDLPrimitiveType;

public class MySelfType implements Type{
	
	public HDLPrimitiveType getHDLType(){
		System.err.println("unsupported type: " + this);
		return null;
	}
	
	public void accept(SynthesijerAstVisitor v){
		v.visitMySelfType(this);
	}
}
