package synthesijer.ast;

import synthesijer.hdl.HDLType;


public interface Type extends SynthesijerAstTree{
	
	public HDLType getHDLType();
	
}
