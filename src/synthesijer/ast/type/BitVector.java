package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstTypeVisitor;
import synthesijer.ast.Type;

public class BitVector implements Type{
	
	final int width;
	
	public BitVector(int width){
		this.width = width;
	}
	
	public int getWidth(){
		return width;
	}
	
	public void accept(SynthesijerAstTypeVisitor v){
		
	}
	
	public String toString(){
		return "BitVecotr::" + width;
	}

}
