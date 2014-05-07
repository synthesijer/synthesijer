package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Type;
import synthesijer.hdl.HDLType;

public class MySelfType implements Type{
	
	public HDLType getHDLType(){
		System.err.println("unsupported type: " + this);
		return null;
	}
	
	public void accept(SynthesijerAstVisitor v){
		v.visitMySelfType(this);
	}
}
