package synthesijer.stream;

import com.sun.source.util.*;
import com.sun.source.tree.*;

import java.util.ArrayList;
import java.util.List;

public class BaseScanner<R,P> extends TreeScanner<R, P>{

	@Override
	public R visitCompilationUnit(CompilationUnitTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}

	@Override
	public R visitClass(ClassTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	
	@Override
	public R visitMethod(MethodTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}

	@Override
	public R visitVariable(VariableTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}

	@Override
	public R visitOther(Tree t, P p){
		debug("unimplemented: " + t.getClass().getName());
		return null;
	}

	@Override
	public R visitAnnotatedType(AnnotatedTypeTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}

	@Override
	public R visitAnnotation(AnnotationTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitArrayAccess(ArrayAccessTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitArrayType(ArrayTypeTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitAssert(AssertTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitAssignment(AssignmentTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitBinary(BinaryTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitBlock(BlockTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitBreak(BreakTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitCase(CaseTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitCatch(CatchTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitCompoundAssignment(CompoundAssignmentTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitConditionalExpression(ConditionalExpressionTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitContinue(ContinueTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitDoWhileLoop(DoWhileLoopTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitEmptyStatement(EmptyStatementTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitEnhancedForLoop(EnhancedForLoopTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitErroneous(ErroneousTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitExports(ExportsTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitExpressionStatement(ExpressionStatementTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitForLoop(ForLoopTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitIdentifier(IdentifierTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitIf(IfTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitImport(ImportTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitInstanceOf(InstanceOfTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitIntersectionType(IntersectionTypeTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitLabeledStatement(LabeledStatementTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitLambdaExpression(LambdaExpressionTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitLiteral(LiteralTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitMemberReference(MemberReferenceTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitMemberSelect(MemberSelectTree node, P p){
		debug("unimplemented:fi " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitMethodInvocation(MethodInvocationTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitModifiers(ModifiersTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitModule(ModuleTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitNewArray(NewArrayTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitNewClass(NewClassTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitOpens(OpensTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitPackage(PackageTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitParameterizedType(ParameterizedTypeTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitParenthesized(ParenthesizedTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitPrimitiveType(PrimitiveTypeTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitProvides(ProvidesTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitRequires(RequiresTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitReturn(ReturnTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitSwitch(SwitchTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitSynchronized(SynchronizedTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitThrow(ThrowTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitTry(TryTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitTypeCast(TypeCastTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitTypeParameter(TypeParameterTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitUnary(UnaryTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitUnionType(UnionTypeTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitUses(UsesTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitWhileLoop(WhileLoopTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}
	@Override
	public R visitWildcard(WildcardTree node, P p){
		debug("unimplemented: " + node.getClass().getName());
		return null;
	}

	private void debug(String mesg){
		System.out.println(mesg);
	}

}

