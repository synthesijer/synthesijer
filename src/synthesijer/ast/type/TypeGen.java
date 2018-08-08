package synthesijer.ast.type;

import synthesijer.ast.Type;

public class TypeGen {
	
	private static final String ARRAY_TYPE = "ArrayType::";  
	private static final String ARRAY_REF_TYPE = "ArrayRef::";  
	private static final String COMPONENT_TYPE = "ComponentType::";  
	
	public static Type get(String key) throws Exception{
		if(key.startsWith(ARRAY_TYPE)){
			return new ArrayType(get(key.substring(ARRAY_TYPE.length())));
		}else if(key.startsWith(ARRAY_REF_TYPE)){
			return new ArrayRef((ArrayType)(get(key.substring(ARRAY_REF_TYPE.length()))));
		}else if(key.startsWith(COMPONENT_TYPE)){
			return new ComponentType(key.substring(COMPONENT_TYPE.length()));
		}
		switch(key){
		case "BOOLEAN": return PrimitiveTypeKind.BOOLEAN;
		case "BYTE" : return PrimitiveTypeKind.BYTE;
		case "CHAR" : return PrimitiveTypeKind.CHAR;
		case "INT" : return PrimitiveTypeKind.INT;
		case "LONG" : return PrimitiveTypeKind.LONG;
		case "SHORT" : return PrimitiveTypeKind.SHORT;
		case "VOID" : return PrimitiveTypeKind.VOID;
		case "OTHER" : return PrimitiveTypeKind.OTHER;
		case "DECLARED" : return PrimitiveTypeKind.DECLARED;
		case "ARRAY" : return PrimitiveTypeKind.ARRAY;
		case "DOUBLE" : return PrimitiveTypeKind.DOUBLE;
		case "ERROR" : return PrimitiveTypeKind.ERROR;
		case "EXECUTABLE" : return PrimitiveTypeKind.EXECUTABLE;
		case "FLOAT" : return PrimitiveTypeKind.FLOAT;
		case "NONE" : return PrimitiveTypeKind.NONE;
		case "NULL" : return PrimitiveTypeKind.NULL;
		case "PACKAGE" : return PrimitiveTypeKind.PACKAGE;
		case "TYPEVAR" : return PrimitiveTypeKind.TYPEVAR;
		case "WILDCARD" : return PrimitiveTypeKind.WILDCARD;
		case "UNDEFINED" : return PrimitiveTypeKind.UNDEFINED;
		
		default:
			throw new Exception("Unknown keyword: " + key);
		}
	}
}

