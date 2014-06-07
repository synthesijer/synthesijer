package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Type;

public enum PrimitiveTypeKind implements Type{

	BOOLEAN,
	BYTE,
	CHAR,
	INT,
	LONG,
	SHORT,
	VOID,
	OTHER,
	DECLARED,
	ARRAY,
	DOUBLE,
	ERROR,
	EXECUTABLE,
	FLOAT,
	NONE,
	NULL,
	PACKAGE,
	TYPEVAR,
	WILDCARD,
	UNDEFIEND;
	
	public void accept(SynthesijerAstVisitor v){
		v.visitPrimitiveTypeKind(this);
	}
}
