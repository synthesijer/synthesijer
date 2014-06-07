package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Type;

public class MySelfType implements Type{
		
	public void accept(SynthesijerAstVisitor v){
		v.visitMySelfType(this);
	}
}
