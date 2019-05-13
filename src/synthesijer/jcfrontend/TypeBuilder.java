package synthesijer.jcfrontend;

import com.sun.source.tree.Tree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.PrimitiveTypeTree;
import synthesijer.SynthesijerUtils;
import synthesijer.ast.Type;
import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.ComponentType;
import synthesijer.ast.type.PrimitiveTypeKind;

public class TypeBuilder {

	public static Type genType(Tree that){
		Type type;
		if(that instanceof PrimitiveTypeTree){
			PrimitiveTypeTree t = (PrimitiveTypeTree)that;
			type = getPrimitiveType(t.getPrimitiveTypeKind());
		}else if(that instanceof IdentifierTree){
			type = new ComponentType(((IdentifierTree) that).getName().toString());
		}else if(that instanceof ArrayTypeTree){
			ArrayTypeTree t = (ArrayTypeTree)that;
			type = new ArrayType(genType(t.getType()));
		}else{
			SynthesijerUtils.error(String.format("Unknown type: %s (%s)\n", that, that.getClass()));
			type = PrimitiveTypeKind.UNDEFINED;
		}
		return type;
	}

	private static PrimitiveTypeKind getPrimitiveType(javax.lang.model.type.TypeKind k){
		boolean flag = true;
		PrimitiveTypeKind knd;
		switch(k){
			case BOOLEAN:    knd = PrimitiveTypeKind.BOOLEAN; break;
			case BYTE:       knd = PrimitiveTypeKind.BYTE;    break;
			case CHAR:       knd = PrimitiveTypeKind.CHAR;    break;
			case INT:        knd = PrimitiveTypeKind.INT;     break;
			case LONG:       knd = PrimitiveTypeKind.LONG;    break;
			case SHORT:      knd = PrimitiveTypeKind.SHORT;   break;
			case VOID:       knd = PrimitiveTypeKind.VOID;    break;
			case OTHER:      knd = PrimitiveTypeKind.OTHER;      flag = false; break;
			case DECLARED:   knd = PrimitiveTypeKind.DECLARED;   flag = false; break;
			case ARRAY:      knd = PrimitiveTypeKind.ARRAY;      flag = false; break;
			case DOUBLE:     knd = PrimitiveTypeKind.DOUBLE;  break;
			case ERROR:      knd = PrimitiveTypeKind.ERROR;      flag = false; break;
			case EXECUTABLE: knd = PrimitiveTypeKind.EXECUTABLE; flag = false; break;
			case FLOAT:      knd = PrimitiveTypeKind.FLOAT;   break;
			case NONE:       knd = PrimitiveTypeKind.NONE;       flag = false; break;
			case NULL:       knd = PrimitiveTypeKind.NULL;       flag = false; break;
			case PACKAGE:    knd = PrimitiveTypeKind.PACKAGE;    flag = false; break;
			case TYPEVAR:    knd = PrimitiveTypeKind.TYPEVAR;    flag = false; break;
			case WILDCARD:   knd = PrimitiveTypeKind.WILDCARD;   flag = false; break;
			default: knd = PrimitiveTypeKind.UNDEFINED; flag = false;
		}
		if(!flag) System.err.println("unsupported type: " + k);
		return knd;
	}

}
