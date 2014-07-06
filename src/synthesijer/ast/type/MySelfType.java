package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstTypeVisitor;
import synthesijer.ast.Type;

public class MySelfType implements Type{
		
	public void accept(SynthesijerAstTypeVisitor v){
		v.visitMySelfType(this);
	}
}
