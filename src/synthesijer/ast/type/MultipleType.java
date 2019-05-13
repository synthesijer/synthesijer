package synthesijer.ast.type;

import java.util.ArrayList;
import java.util.Collection;

import synthesijer.ast.SynthesijerAstTypeVisitor;
import synthesijer.ast.Type;

public class MultipleType implements Type{

	public static final String KEY = "MULTIPLE";

	private ArrayList<Type> types = new ArrayList<>();

	public MultipleType(Collection<Type> types){
		this.types.addAll(types);
	}

	public Type get(int i){
		return types.get(i);
	}

	public int size(){
		return types.size();
	}

	public void accept(SynthesijerAstTypeVisitor v){
		v.visitMultipleType(this);
	}

	public String toString(){
		String s = "(";
		s += KEY;
		for(Type t: types){
			s += " " + t.toString();
		}
		s += ")";
		return s;
	}

}
