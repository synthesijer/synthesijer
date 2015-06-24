package synthesijer.jcfrontend;

import java.util.List;

import openjdk.com.sun.tools.javac.tree.JCTree;
import openjdk.com.sun.tools.javac.tree.JCTree.JCAnnotation;
import openjdk.com.sun.tools.javac.tree.JCTree.JCAssign;
import openjdk.com.sun.tools.javac.tree.JCTree.JCClassDecl;
import openjdk.com.sun.tools.javac.tree.JCTree.JCExpression;
import openjdk.com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import openjdk.com.sun.tools.javac.tree.JCTree.JCStatement;
import openjdk.com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import openjdk.com.sun.tools.javac.tree.JCTree.Visitor;
import synthesijer.SynthesijerUtils;
import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.statement.VariableDecl;
import synthesijer.ast.type.MySelfType;

/**
 * A visitor to generate an instance of Module from a given instance of JCClassDecl.
 * 
 * @author miyo
 *
 */
public class JCTopVisitor extends Visitor{
	
	private final Module module;
	
	public JCTopVisitor(Module m){
		this.module = m;
	}
	
	public Scope getScope(){
		return module;
	}
	
	public  void visitClassDef(JCClassDecl that){
		for (JCTree def : that.defs) {
			if(def == null){
				;
			}else if(def instanceof JCMethodDecl){
				def.accept(this);
			}else if(def instanceof JCVariableDecl){
				def.accept(new JCStmtVisitor(module));
			}else{
				System.err.printf("Unknown class: %s (%s)", def, def.getClass());
			}
		}
	}
	
	public void visitMethodDef(JCMethodDecl decl){
		String name = decl.getName().toString();
		Type type;
		if(JCFrontendUtils.isConstructor(decl)){
			type = new MySelfType();
		}else{
			type = TypeBuilder.genType(decl.getReturnType());
		}
		Method m = new Method(module, name, type);
		
		m.setArgs(parseArgs(decl.getParameters(), m));
		
		m.setUnsynthesizableFlag(JCFrontendUtils.isAnnotatedBy(decl.mods.annotations, "unsynthesizable"));
		/*
		m.setAutoFlag(JCFrontendUtils.isAnnotatedBy(decl.mods.annotations, "auto") & module.isSynthesijerHDL());
		if(JCFrontendUtils.isAnnotatedBy(decl.mods.annotations, "auto") & (!module.isSynthesijerHDL())){
			SynthesijerUtils.warn("@auto for " + module.getName() + "::" + name + " is skipped, because @synthesijerhdl is not set.");
		}
		*/
		m.setAutoFlag(JCFrontendUtils.isAnnotatedBy(decl.mods.annotations, "auto"));
		m.setSynchronizedFlag(JCFrontendUtils.isSynchronized(decl.mods));
		m.setPrivateFlag(JCFrontendUtils.isPrivate(decl.mods));
		m.setRawFlag(JCFrontendUtils.isAnnotatedBy(decl.mods.annotations, "raw"));
		m.setCombinationFlag(JCFrontendUtils.isAnnotatedBy(decl.mods.annotations, "combination"));
		m.setParallelFlag(JCFrontendUtils.isAnnotatedBy(decl.mods.annotations, "parallel"));
		m.setNoWaitFlag(JCFrontendUtils.isAnnotatedBy(decl.mods.annotations, "no_wait"));
		m.setConstructorFlag(JCFrontendUtils.isConstructor(decl));
		if(JCFrontendUtils.isAnnotatedBy(decl.mods.annotations, "CallStack")){
			m.setCallStackFlag(true);
			JCAnnotation a = JCFrontendUtils.getAnnotation(decl.mods.annotations, "CallStack");
			for(JCExpression expr: a.args){
				if(expr instanceof JCAssign){
					JCAssign assign = (JCAssign)expr;
					m.setCallStackSize(Integer.parseInt(assign.rhs.toString()));
				}else{
					SynthesijerUtils.warn("unexpected argument for @CallStack: " + expr);
					SynthesijerUtils.warn("Use \"value=immidiate\" or \"immidiate\"");
				}
			}
		}else{
			m.setCallStackFlag(false);
		}
		
		if(decl.body != null){
			for(JCStatement stmt: decl.body.getStatements()){
				JCStmtVisitor visitor = new JCStmtVisitor(m);
				stmt.accept(visitor);
				m.getBody().addStatement(visitor.getStatement());
			}
		}
		
		module.addMethod(m);
	}
	
	/**
	 * parse arguments of method declaration.
	 * @param args
	 * @param scope
	 * @return
	 */
	private VariableDecl[] parseArgs(List<JCVariableDecl> args, Scope scope){
		if(args == null || args.size() == 0) return new VariableDecl[0];
		VariableDecl[] v = new VariableDecl[args.size()];
		for(int i = 0; i < v.length; i++){
			JCStmtVisitor visitor = new JCStmtVisitor(scope);
			args.get(i).accept(visitor); // Since args.get(i) is an instance of JCVariableDecl,
			                             // this visitor should visit visitVarDef
			v[i] = (VariableDecl)(visitor.getStatement()); // this type cast should occur no errors.
			v[i].setMethodParam(true);
		}
		return v;
	}

	public void visitTree(JCTree t){
		SynthesijerUtils.error("[JCTopVisitor] The following is unexpected in this context.");
		SynthesijerUtils.dump(t);
	}
	
}