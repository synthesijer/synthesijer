package synthesijer.jcfrontend;

import java.util.List;

import openjdk.com.sun.tools.javac.code.Flags;
import openjdk.com.sun.tools.javac.code.Symbol;
import openjdk.com.sun.tools.javac.tree.JCTree.JCAnnotation;
import openjdk.com.sun.tools.javac.tree.JCTree.JCExpression;
import openjdk.com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import openjdk.com.sun.tools.javac.tree.JCTree.JCIdent;
import openjdk.com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import openjdk.com.sun.tools.javac.tree.JCTree.JCModifiers;

public class JCFrontendUtils {

	public static boolean isAnnotatedBy(List<JCAnnotation> annotations, String key){
		for(JCAnnotation a: annotations){
			if(a.getAnnotationType() instanceof JCIdent){
				Symbol s = ((JCIdent)(a.getAnnotationType())).sym;
				if(s.toString().endsWith(key)) return true;
			}
		}
		return false;
	}
	
	public static boolean isSynchronized(JCModifiers mods){
		if(mods == null) return false;
		return (mods.flags & Flags.SYNCHRONIZED) == Flags.SYNCHRONIZED;
	}
	
	public static boolean isPrivate(JCModifiers mods){
		if(mods == null) return false;
		return (mods.flags & Flags.PRIVATE) == Flags.PRIVATE;
	}

	public static boolean isFinal(JCModifiers mods){
		if(mods == null) return false;
		return (mods.flags & Flags.FINAL) == Flags.FINAL;
	}

	public static boolean isGlobalConstant(JCModifiers mods){
		if(mods == null) return false;
		boolean f = true;
		f &= (mods.flags & Flags.PUBLIC) == Flags.PUBLIC;
		f &= (mods.flags & Flags.FINAL)  == Flags.FINAL;		
		f &= (mods.flags & Flags.STATIC) == Flags.STATIC;		
		return f;
	}

	public static boolean isAnnotationDecl(JCModifiers mods){
		return (mods.flags & Flags.ANNOTATION) == Flags.ANNOTATION;
	}
	
	public static boolean isInterfaceDecl(JCModifiers mods){
		return (mods.flags & Flags.INTERFACE) == Flags.INTERFACE;
	}
	
	public static boolean isImplemented(List<JCExpression> implementing, String key){
		for(JCExpression expr: implementing){
			if(expr instanceof JCIdent){
				JCIdent ident = (JCIdent) expr; 
				if(ident.sym.toString().equals(key)) return true;
			}else if(expr instanceof JCFieldAccess){
				JCFieldAccess fa = (JCFieldAccess)expr;
				if(fa.sym.toString().equals(key)) return true;
			}else{
				System.out.printf("unknown: %s (%s)\n", expr, expr.getClass());
			}
		}
		return false;
	}
	
	public static boolean isConstructor(JCMethodDecl decl){
		return "<init>".equals(decl.getName().toString());
	}

}
