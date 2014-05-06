package synthesijer.jcfrontend;

import openjdk.com.sun.tools.javac.tree.JCTree;
import openjdk.com.sun.tools.javac.tree.JCTree.JCArrayTypeTree;
import openjdk.com.sun.tools.javac.tree.JCTree.JCIdent;
import openjdk.com.sun.tools.javac.tree.JCTree.JCPrimitiveTypeTree;
import synthesijer.SynthesijerUtils;
import synthesijer.ast.Type;
import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.ComponentType;
import synthesijer.ast.type.PrimitiveTypeKind;

public class TypeBuilder {

	public static Type genType(JCTree that){
		Type type;
		if(that instanceof JCPrimitiveTypeTree){
			JCPrimitiveTypeTree t = (JCPrimitiveTypeTree)that;
			type = getPrimitiveType(t.getPrimitiveTypeKind());
		}else if(that instanceof JCIdent){
			type = new ComponentType(((JCIdent) that).sym.toString());  
		}else if(that instanceof JCArrayTypeTree){
			JCArrayTypeTree t = (JCArrayTypeTree)that;
			type = new ArrayType(genType(t.elemtype));
		}else{
			SynthesijerUtils.error(String.format("Unknown type: %s (%s)\n", that, that.getClass()));
			type = PrimitiveTypeKind.UNDEFIEND;
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
		default: knd = PrimitiveTypeKind.UNDEFIEND; flag = false;
		}
		if(!flag) System.err.println("unsupported type: " + k);
		return knd;
	}

}
