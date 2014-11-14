package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstTypeVisitor;
import synthesijer.ast.Type;

public enum PrimitiveTypeKind implements Type{

	BOOLEAN,
	BYTE(8),
	CHAR(16),
	INT(32),
	LONG(64),
	SHORT(16),
	VOID,
	OTHER,
	DECLARED,
	ARRAY,
	DOUBLE(64),
	ERROR,
	EXECUTABLE,
	FLOAT(32),
	NONE,
	NULL,
	PACKAGE,
	TYPEVAR,
	WILDCARD,
	UNDEFIEND;

	private final int width;
	
	private PrimitiveTypeKind(int w){
		this.width = w;
	}
	
	private PrimitiveTypeKind(){
		this.width = -1;
	}
	
	public void accept(SynthesijerAstTypeVisitor v){
		v.visitPrimitiveTypeKind(this);
	}
	
	public boolean isInteger(){
		switch(this){
		case BYTE:
		case CHAR:
		case INT:
		case LONG:
		case SHORT: return true;
		default: return false;
		}
	}
	
	public boolean isFloating(){
		switch(this){
		case FLOAT:
		case DOUBLE: return true;
		default: return false;
		}
	}
	
	public int getWidth(){
		return width;
	}
	
}
