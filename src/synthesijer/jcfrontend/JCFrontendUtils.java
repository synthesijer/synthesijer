package synthesijer.jcfrontend;

import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;

public class JCFrontendUtils {

    public static boolean isAnnotatedBy(List<? extends AnnotationTree> annotations, String key){
		for(AnnotationTree a: annotations){
			if(a.getAnnotationType() instanceof IdentifierTree){
				Name s = ((IdentifierTree)(a.getAnnotationType())).getName();
				if(s.toString().endsWith(key)) return true;
			}else if(a.getAnnotationType() instanceof MemberSelectTree){
				Name s = ((MemberSelectTree)(a.getAnnotationType())).getIdentifier();
				if(s.toString().endsWith(key)) return true;
			}
		}
		return false;
    }

    public static AnnotationTree getAnnotation(List<? extends AnnotationTree> annotations, String key){
		for(AnnotationTree a: annotations){
			if(a.getAnnotationType() instanceof IdentifierTree){
				Name s = ((IdentifierTree)(a.getAnnotationType())).getName();
				if(s.toString().endsWith(key)) return a;
			}
		}
		return null;
    }

    public static boolean isSynchronized(ModifiersTree mods){
		if(mods == null) return false;
		return (mods.getFlags().contains(Modifier.SYNCHRONIZED));
    }
	
    public static boolean isPrivate(ModifiersTree mods){
		if(mods == null) return false;
		return (mods.getFlags().contains(Modifier.PRIVATE));
    }

    public static boolean isFinal(ModifiersTree mods){
		if(mods == null) return false;
		return (mods.getFlags().contains(Modifier.FINAL));
    }

    public static boolean isVolatile(ModifiersTree mods){
		if(mods == null) return false;
		return (mods.getFlags().contains(Modifier.VOLATILE));
    }

    public static boolean isGlobalConstant(ModifiersTree mods){
		if(mods == null) return false;
		boolean f = true;
		f &= (mods.getFlags().contains(Modifier.PUBLIC));
		f &= (mods.getFlags().contains(Modifier.FINAL));
		f &= (mods.getFlags().contains(Modifier.STATIC));
		return f;
    }

    public static boolean isAnnotationDecl(ModifiersTree mods){
		//return (mods.getFlags().contains(Modifier.ANNOTATION));
		return false;
    }
	
    public static boolean isInterfaceDecl(ModifiersTree mods){
		//return (mods.getFlags().contains(Modifier.INTERFACE));
		//return (mods.getFlags().contains(Modifier.ABSTRACT));
		return false;
    }
	
    public static boolean isImplemented(List<ExpressionTree> implementing, String key){
		System.out.println(implementing);
		for(ExpressionTree expr: implementing){
			if(expr instanceof IdentifierTree){
				IdentifierTree ident = (IdentifierTree) expr; 
				if(ident.getName().toString().equals(key)) return true;
			}else if(expr instanceof MemberSelectTree){
				MemberSelectTree fa = (MemberSelectTree)expr;
				if(fa.getIdentifier().toString().equals(key)) return true;
			}else{
				System.out.printf("unknown: %s (%s)\n", expr, expr.getClass());
			}
		}
		return false;
    }
	
    public static boolean isConstructor(MethodTree decl){
		return "<init>".equals(decl.getName().toString());
    }

}
