package synthesijer.jcfrontend;

import java.util.Hashtable;

import openjdk.com.sun.tools.javac.code.Symbol;
import openjdk.com.sun.tools.javac.comp.AttrContext;
import openjdk.com.sun.tools.javac.comp.Env;
import openjdk.com.sun.tools.javac.tree.JCTree.JCClassDecl;
import synthesijer.Manager;
import synthesijer.SynthesijerUtils;
import synthesijer.ast.Module;

public class Main {
	
	public static void newModule(Env<AttrContext> env, JCClassDecl decl){
		if(JCFrontendUtils.isAnnotationDecl(decl.mods)){
			SynthesijerUtils.warn(decl.sym.toString() + " is skipped.");
			return;
		}
		if(JCFrontendUtils.isInterfaceDecl(decl.mods)){
			SynthesijerUtils.warn(decl.sym.toString() + " is skipped.");
			return;
		}
		if(JCFrontendUtils.isImplemented(decl.implementing, "net.wasamon.javarock.model.JavaRockComponentIface")){
			SynthesijerUtils.warn(decl.sym.toString() + " is skipped.");
			return;
		}
		Hashtable<String, String> importTable = new Hashtable<String, String>();
		for(Symbol s: env.outer.info.getLocalElements()){
			importTable.put(s.name.toString(), s.toString());
		}
		Module module = new Module(decl.sym.toString(), importTable);
		JCTopVisitor visitor = new JCTopVisitor(module);
		decl.accept(visitor);
		Manager.INSTANCE.addModule(module);
	}


}
