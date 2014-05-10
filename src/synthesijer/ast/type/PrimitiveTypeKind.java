package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Type;
import synthesijer.hdl.HDLPrimitiveType;

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
	
	public HDLPrimitiveType getHDLType(){
		if(this == PrimitiveTypeKind.BOOLEAN){
			return HDLPrimitiveType.genBitType(); 
		}else if(this == PrimitiveTypeKind.BYTE){
			return HDLPrimitiveType.genSignedType(8);
		}else if(this == PrimitiveTypeKind.CHAR){
			return HDLPrimitiveType.genVectorType(16);
		}else if(this == PrimitiveTypeKind.SHORT){
			return HDLPrimitiveType.genSignedType(16);
		}else if(this == PrimitiveTypeKind.INT){
			return HDLPrimitiveType.genSignedType(32);
		}else if(this == PrimitiveTypeKind.LONG){
			return HDLPrimitiveType.genSignedType(64);
		}else if(this == PrimitiveTypeKind.FLOAT){
			return HDLPrimitiveType.genVectorType(32);
		}else if(this == PrimitiveTypeKind.DOUBLE){
			return HDLPrimitiveType.genVectorType(64);
		}else{
			return HDLPrimitiveType.genUnkonwType();
		}
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitPrimitivyTypeKind(this);
	}
}
