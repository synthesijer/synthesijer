package synthesijer.scheduler;

import synthesijer.ast.Type;

public class ArrayRefOperand extends VariableOperand{
	
	public final int depth;
	public final int words;
	
	public ArrayRefOperand(String name, Type type, int depth, int words){
		super(name, type);
		this.depth = depth;
		this.words = words;  
	}

	public String info(){
		return "ArrayRef(depth=" + depth + ", words=" + words + ")";
	}
}
