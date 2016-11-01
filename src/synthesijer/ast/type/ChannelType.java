package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstTypeVisitor;
import synthesijer.ast.Type;

public class ChannelType implements Type {

	public static final String KEY = "CHANNEL";
	
	private final Type elemType;

	public ChannelType(Type k){
			elemType = k;
		}

	public Type getElemType() {
		return elemType;
	}

	public void accept(SynthesijerAstTypeVisitor v) {
		v.visitChannelType(this);
	}

	public String toString() {
		return "(" + KEY + " " + elemType + ")";
	}

}