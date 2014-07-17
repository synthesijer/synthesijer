package synthesijer.jcfrontend;

import java.util.ArrayList;
import java.util.Hashtable;

import openjdk.com.sun.tools.javac.code.Symbol;
import openjdk.com.sun.tools.javac.comp.AttrContext;
import openjdk.com.sun.tools.javac.comp.Env;
import openjdk.com.sun.tools.javac.tree.JCTree;
import openjdk.com.sun.tools.javac.tree.JCTree.JCClassDecl;
import openjdk.com.sun.tools.javac.tree.JCTree.JCExpression;
import synthesijer.Manager;
import synthesijer.SynthesijerUtils;
import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.type.PrimitiveTypeKind;

public class Main {
	
	private static String getExtendingClassName(Hashtable<String, String> importTable, JCTree name){
		if(name == null) return null;
		if(importTable.containsKey(name.toString())){
			return importTable.get(name.toString());
		}else{
			return name.toString();
		}
	}

	private static String getImplementingIntarfaceName(Hashtable<String, String> importTable, String name){
		if(name == null) return null;
		if(importTable.containsKey(name)){
			return importTable.get(name);
		}else{
			return name;
		}
	}

	public static void newModule(Env<AttrContext> env, JCClassDecl decl){
		if(JCFrontendUtils.isAnnotationDecl(decl.mods)){
			SynthesijerUtils.warn(decl.sym.toString() + " is skipped.");
			return;
		}
		if(JCFrontendUtils.isInterfaceDecl(decl.mods)){
			SynthesijerUtils.warn(decl.sym.toString() + " is skipped.");
			return;
		}
		Hashtable<String, String> importTable = new Hashtable<>();
		for(Symbol s: env.outer.info.getLocalElements()){
			importTable.put(s.name.toString(), s.toString());
		}
		
		String extending = getExtendingClassName(importTable, decl.extending);
		ArrayList<String> implementing = new ArrayList<>();
		for(JCExpression i: decl.implementing){
			implementing.add(getImplementingIntarfaceName(importTable, i.toString()));
		}
		
		boolean syntheisizeFlag = true;
		if(extending != null && extending.equals("synthesijer.hdl.HDLModule")){
			syntheisizeFlag = false;
		}
		
		Module module = new Module(decl.sym.toString(), importTable, extending, implementing);		
		JCTopVisitor visitor = new JCTopVisitor(module);
		decl.accept(visitor);
		
		if(extending != null && extending.equals("Thread")){ //TODO experimental
			addThread(module);
		}

		if(syntheisizeFlag){
			Manager.INSTANCE.addModule(module);			
		}else{
			Manager.INSTANCE.registUserHDLModule(decl.sym.toString());
		}
	}

	// TODO experimental
	private static void addThread(Module m){
		Method start = new Method(m, "start", PrimitiveTypeKind.VOID);
		m.addMethod(start);
		Method join = new Method(m, "join", PrimitiveTypeKind.VOID);
		m.addMethod(join);
		Method yield = new Method(m, "yield", PrimitiveTypeKind.VOID);
		m.addMethod(yield);
	}
	
}
