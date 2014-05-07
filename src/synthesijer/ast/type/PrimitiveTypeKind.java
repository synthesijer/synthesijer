package synthesijer.ast.type;

import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Type;
import synthesijer.hdl.HDLType;

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
	
	public HDLType getHDLType(){
		if(this == PrimitiveTypeKind.BOOLEAN){
			return HDLType.genBitType(); 
		}else if(this == PrimitiveTypeKind.BYTE){
			return HDLType.genSignedType(8);
		}else if(this == PrimitiveTypeKind.CHAR){
			return HDLType.genVectorType(16);
		}else if(this == PrimitiveTypeKind.SHORT){
			return HDLType.genSignedType(16);
		}else if(this == PrimitiveTypeKind.INT){
			return HDLType.genSignedType(32);
		}else if(this == PrimitiveTypeKind.LONG){
			return HDLType.genSignedType(64);
		}else if(this == PrimitiveTypeKind.FLOAT){
			return HDLType.genVectorType(32);
		}else if(this == PrimitiveTypeKind.DOUBLE){
			return HDLType.genVectorType(64);
		}else{
			return HDLType.genUnkonwType();
		}
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitPrimitivyTypeKind(this);
	}
}
