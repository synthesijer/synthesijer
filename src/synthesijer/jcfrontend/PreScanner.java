package synthesijer.jcfrontend;

import java.util.Hashtable;

import com.sun.source.tree.Tree;
import com.sun.source.tree.ClassTree;
import com.sun.source.util.TreeScanner;

import synthesijer.jcfrontend.JCFrontendUtils;

public class PreScanner extends TreeScanner<Void, SourceInfo>{

	@Override
	public Void visitClass(ClassTree node, SourceInfo info) {
		info.className = node.getSimpleName().toString();
		//return super.visitClass(node, null);

		info.isAnnotation = JCFrontendUtils.isAnnotationDecl(node.getModifiers());
		info.isInterface = JCFrontendUtils.isInterfaceDecl(node.getModifiers());
		info.isSynthesijerHDL = JCFrontendUtils.isAnnotatedBy(node.getModifiers().getAnnotations(), "synthesijerhdl");

		info.extending = getExtendingClassName(info.importTable, node.getExtendsClause());
		for(Tree i: node.getImplementsClause()){
			info.implementing.add(getImplementingIntarfaceName(info.importTable, i.toString()));
		}

		return null;
	}

	private String getExtendingClassName(Hashtable<String, String> importTable, Tree name){
		if(name == null) return null;
		if(importTable.containsKey(name.toString())){
			return importTable.get(name.toString());
		}else{
			return name.toString();
		}
	}

	private String getImplementingIntarfaceName(Hashtable<String, String> importTable, String name){
		if(name == null) return null;
		if(importTable.containsKey(name)){
			return importTable.get(name);
		}else{
			return name;
		}
	}


}
