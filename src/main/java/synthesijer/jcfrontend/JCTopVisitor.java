package synthesijer.jcfrontend;

import java.util.List;

import com.sun.source.tree.Tree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
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
public class JCTopVisitor extends TreeScanner<Void, Void>{

	private final Module module;

	public JCTopVisitor(Module m){
		this.module = m;
	}

	public Scope getScope(){
		return module;
	}

	@Override
	public  Void visitClass(ClassTree that, Void aVoid){
		for (Tree def : that.getMembers()) {
			if(def == null){
				;
			}else if(def instanceof MethodTree){
				def.accept(this, null);
			}else if(def instanceof VariableTree){
				def.accept(new JCStmtVisitor(module), null);
			}else{
				System.err.printf("Unknown class: %s (%s)", def, def.getClass());
			}
		}
		//return super.visitClass(that, aVoid);
		return null;
	}

	@Override
	public Void visitMethod(MethodTree decl, Void aVoid){
		String name = decl.getName().toString();
		Type type;
		if(JCFrontendUtils.isConstructor(decl)){
			type = new MySelfType();
		}else{
			type = TypeBuilder.genType(decl.getReturnType());
		}
		Method m = new Method(module, name, type);

		m.setArgs(parseArgs(decl.getParameters(), m));

		if(JCFrontendUtils.isAnnotatedBy(decl.getModifiers().getAnnotations(), "unsynthesizable")){
			//return super.visitMethod(decl, aVoid);
			return null;
		}

		m.setUnsynthesizableFlag(JCFrontendUtils.isAnnotatedBy(decl.getModifiers().getAnnotations(), "unsynthesizable"));
		m.setAutoFlag(JCFrontendUtils.isAnnotatedBy(decl.getModifiers().getAnnotations(), "auto"));
		m.setSynchronizedFlag(JCFrontendUtils.isSynchronized(decl.getModifiers()));
		m.setPrivateFlag(JCFrontendUtils.isPrivate(decl.getModifiers()));
		m.setRawFlag(JCFrontendUtils.isAnnotatedBy(decl.getModifiers().getAnnotations(), "raw"));
		m.setCombinationFlag(JCFrontendUtils.isAnnotatedBy(decl.getModifiers().getAnnotations(), "combination"));
		m.setParallelFlag(JCFrontendUtils.isAnnotatedBy(decl.getModifiers().getAnnotations(), "parallel"));
		m.setNoWaitFlag(JCFrontendUtils.isAnnotatedBy(decl.getModifiers().getAnnotations(), "no_wait"));
		m.setConstructorFlag(JCFrontendUtils.isConstructor(decl));
		if(JCFrontendUtils.isAnnotatedBy(decl.getModifiers().getAnnotations(), "CallStack")){
			m.setCallStackFlag(true);
			AnnotationTree a = JCFrontendUtils.getAnnotation(decl.getModifiers().getAnnotations(), "CallStack");
			for(ExpressionTree expr: a.getArguments()){
				if(expr instanceof AssignmentTree){
					AssignmentTree assign = (AssignmentTree)expr;
					m.setCallStackSize(Integer.parseInt(assign.getExpression().toString()));
				}else{
					SynthesijerUtils.warn("unexpected argument for @CallStack: " + expr);
					SynthesijerUtils.warn("Use \"value=immidiate\" or \"immidiate\"");
				}
			}
		}else{
			m.setCallStackFlag(false);
		}

		if(decl.getBody() != null){
			for(StatementTree stmt: decl.getBody().getStatements()){
				JCStmtVisitor visitor = new JCStmtVisitor(m);
				stmt.accept(visitor, null);
				m.getBody().addStatement(visitor.getStatement());
			}
		}

		module.addMethod(m);
		//return super.visitMethod(decl, aVoid);
		return null;
	}

	/**
	 * parse arguments of method declaration.
	 * @param args
	 * @param scope
	 * @return
	 */
	private VariableDecl[] parseArgs(List<? extends VariableTree> args, Scope scope){
		if(args == null || args.size() == 0) return new VariableDecl[0];
		VariableDecl[] v = new VariableDecl[args.size()];
		for(int i = 0; i < v.length; i++){
			JCStmtVisitor visitor = new JCStmtVisitor(scope);
			args.get(i).accept(visitor, null); // Since args.get(i) is an instance of JCVariableDecl,
			// this visitor should visit visitVarDef
			v[i] = (VariableDecl)(visitor.getStatement()); // this type cast should occur no errors.
			v[i].setMethodParam(true);
		}
		return v;
	}

	@Override
	public Void visitOther(Tree t, Void aVoid){
		SynthesijerUtils.error("[JCTopVisitor] The following is unexpected in this context.");
		SynthesijerUtils.dump(t);
		return super.visitOther(t, aVoid);
	}

}
