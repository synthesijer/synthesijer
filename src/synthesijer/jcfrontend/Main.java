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
import synthesijer.ast.Module;


/**
 * 
 * The programmatic interface for the Java Programming Language,
 * which generates ASTs for synthesijer compilation.
 * 
 * @author miyo
 *
 */
public class Main {
	
	/**
	 * 
	 * @param importTable the table of importing classes. 
	 * @param name target class name.
	 * @return the absolute extending class name.  
	 */
	private static String getExtendingClassName(Hashtable<String, String> importTable, JCTree name){
		if(name == null) return null;
		if(importTable.containsKey(name.toString())){
			return importTable.get(name.toString());
		}else{
			return name.toString();
		}
	}

	/**
	 * 
	 * @param importTable the table of importing classes. 
	 * @param name target interface name.
	 * @return the absolute extending interface name.  
	 */
	private static String getImplementingIntarfaceName(Hashtable<String, String> importTable, String name){
		if(name == null) return null;
		if(importTable.containsKey(name)){
			return importTable.get(name);
		}else{
			return name;
		}
	}

	private static boolean isHDLModule(String extending, Hashtable<String, String> importTable){
		if(extending == null) return false;
		if(extending.equals("HDLModule")) return true; // ad-hoc
		if(extending.equals("synthesijer.hdl.HDLModule")) return true;
		return false;
	}

	/**
	 * starts to parse a new class in order to generate a instance of Module from a given instance of JCClassDecl.
	 * @param env
	 * @param decl
	 */
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
		
		boolean synthesizeFlag = true;
		if(isHDLModule(extending, importTable) == true){
			synthesizeFlag = false;
		}
		
		Module module = new Module(decl.sym.toString(), importTable, extending, implementing);
		module.setSynthesijerHDL(    JCFrontendUtils.isAnnotatedBy(decl.mods.annotations, "synthesijerhdl"));
		
		JCTopVisitor visitor = new JCTopVisitor(module);
		decl.accept(visitor);
		
		Manager.INSTANCE.addModule(module, synthesizeFlag);			
		/*
		if(syntheisizeFlag){
			Manager.INSTANCE.addModule(module);			
		}else{
			Manager.INSTANCE.registUserHDLModule(decl.sym.toString());
		}
		*/
	}
	
}
