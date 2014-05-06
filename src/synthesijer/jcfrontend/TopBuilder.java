package synthesijer.jcfrontend;

import java.util.Hashtable;
import java.util.List;

import openjdk.com.sun.tools.javac.code.Symbol;
import openjdk.com.sun.tools.javac.comp.AttrContext;
import openjdk.com.sun.tools.javac.comp.Env;
import openjdk.com.sun.tools.javac.tree.JCTree;
import openjdk.com.sun.tools.javac.tree.JCTree.JCClassDecl;
import openjdk.com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import openjdk.com.sun.tools.javac.tree.JCTree.JCStatement;
import openjdk.com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import synthesijer.Manager;
import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.statement.VariableDecl;
import synthesijer.ast.type.MySelfType;

public enum TopBuilder {
	
	INSTANCE;
	
	public void genNewEntry(Env<AttrContext> env, JCClassDecl decl){
		if(SynthesijerJCFrontendUtils.isAnnotationDecl(decl.mods)) return;
		if(SynthesijerJCFrontendUtils.isInterfaceDecl(decl.mods)) return;
		if(SynthesijerJCFrontendUtils.isImplemented(decl.implementing, "net.wasamon.javarock.model.JavaRockComponentIface")) return;
		Hashtable<String, String> importTable = new Hashtable<String, String>();
		for(Symbol s: env.outer.info.getLocalElements()){
			importTable.put(s.name.toString(), s.toString());
		}

		Module module = new Module(decl.sym.toString(), importTable);
		parseModule(decl, module);
		Manager.INSTANCE.addModule(module);
	}
	
	private void parseModule(JCClassDecl decl, Module module){
		for (JCTree def : decl.defs) {
			if(def == null){
				;
			}else if(def instanceof JCMethodDecl){
				addMethodDecl((JCMethodDecl)def, module);
			}else if(def instanceof JCVariableDecl){
				addVariableDecl((JCVariableDecl)def, module);
			}else{
				System.err.printf("Unknown class: %s (%s)", def, def.getClass());
			}
		}
	}
	
	private void addMethodDecl(JCMethodDecl decl, Module module){
		Method m = genMethod(decl, module);
		module.addMethod(m);
	}
	
	private void addVariableDecl(JCVariableDecl decl, Module module){
		VariableDecl v = StatementBuilder.genVariableDecl(decl, module);
		module.addVariable(v);
	}
	
	private Method genMethod(JCMethodDecl decl, Module module){
		String name = decl.getName().toString();
		Type type;
		if(SynthesijerJCFrontendUtils.isConstructor(decl)){
			type = new MySelfType();
		}else{
			type = TypeBuilder.genType(decl.getReturnType());
		}
		Method m = new Method(module, name, type);
		
		m.setArgs(parseArgs(decl.getParameters(), m));
		
		m.setUnsynthesizableFlag(SynthesijerJCFrontendUtils.isAnnotatedBy(decl.mods.annotations, "unsynthesizable"));
		m.setAutoFlag(SynthesijerJCFrontendUtils.isAnnotatedBy(decl.mods.annotations, "auto"));
		m.setSynchronizedFlag(SynthesijerJCFrontendUtils.isSynchronized(decl.mods));
		m.setPrivateFlag(SynthesijerJCFrontendUtils.isPrivate(decl.mods));
		m.setRawFlag(SynthesijerJCFrontendUtils.isAnnotatedBy(decl.mods.annotations, "raw"));
		m.setCombinationFlag(SynthesijerJCFrontendUtils.isAnnotatedBy(decl.mods.annotations, "combination"));
		m.setParallelFlag(SynthesijerJCFrontendUtils.isAnnotatedBy(decl.mods.annotations, "parallel"));
		m.setNoWaitFlag(SynthesijerJCFrontendUtils.isAnnotatedBy(decl.mods.annotations, "no_wait"));
		m.setConstructorFlag(SynthesijerJCFrontendUtils.isConstructor(decl));
		
		for(JCStatement stmt: decl.body.getStatements()){
			m.getBody().addStatement(StatementBuilder.buildStatement(stmt, m));
		}
		
		return m;
	}
	
	private VariableDecl[] parseArgs(List<JCVariableDecl> args, Scope scope){
		if(args == null || args.size() == 0) return new VariableDecl[0];
		VariableDecl[] v = new VariableDecl[args.size()];
		for(int i = 0; i < v.length; i++){
			v[i] = StatementBuilder.genVariableDecl(args.get(i), scope);
		}
		return v;
	}
		


}
