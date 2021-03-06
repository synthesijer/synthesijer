package synthesijer.jcfrontend;

import com.sun.source.tree.Tree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.util.TreeScanner;
import synthesijer.SynthesijerUtils;
import synthesijer.ast.Expr;
import synthesijer.ast.Op;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.expr.ArrayAccess;
import synthesijer.ast.expr.AssignExpr;
import synthesijer.ast.expr.AssignOp;
import synthesijer.ast.expr.BinaryExpr;
import synthesijer.ast.expr.CondExpr;
import synthesijer.ast.expr.FieldAccess;
import synthesijer.ast.expr.Ident;
import synthesijer.ast.expr.Literal;
import synthesijer.ast.expr.MethodInvocation;
import synthesijer.ast.expr.NewArray;
import synthesijer.ast.expr.NewClassExpr;
import synthesijer.ast.expr.ParenExpr;
import synthesijer.ast.expr.TypeCast;
import synthesijer.ast.expr.UnaryExpr;
import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.ComponentType;

public class JCExprVisitor extends TreeScanner<Void, Void>{

	public final Scope scope;

	private Expr expr;

	public JCExprVisitor(Scope scope){
		this.scope = scope;
	}

	public Expr getExpr(){
		return expr;
	}

	@Override
	public Void visitIdentifier(IdentifierTree that, Void aVoid){
		Ident tmp = new Ident(scope);
		tmp.setIdent(that.toString());
		expr = tmp;
		//return super.visitIdentifier(that, aVoid);
		return null;
	}

	private Expr stepIn(ExpressionTree expr){
		JCExprVisitor visitor = new JCExprVisitor(scope);
		expr.accept(visitor, null);
		return visitor.getExpr();
	}

	@Override
	public Void visitBinary(BinaryTree that, Void aVoid){
		//System.out.println(that);
		BinaryExpr tmp = new BinaryExpr(scope);
		Expr lhs = stepIn(that.getLeftOperand());
		tmp.setLhs(lhs);
		Expr rhs = stepIn(that.getRightOperand());
		//System.out.println(lhs + " <-[cast]- "+ rhs);
		setForceTypeCast(lhs, rhs);
		tmp.setRhs(rhs);
		tmp.setOp(Op.getOp(that.getKind()));
		expr = tmp;
		//return super.visitBinary(that, aVoid);
		return null;
	}

	@Override
	public Void visitUnary(UnaryTree that, Void aVoid){
		boolean postfix = false;
		if(that.getKind() == Tree.Kind.POSTFIX_DECREMENT ||
				that.getKind() == Tree.Kind.POSTFIX_INCREMENT){
			postfix = true;
		}
		UnaryExpr tmp = new UnaryExpr(scope);
		tmp.setOp(Op.getOp(that.getKind()));
		tmp.setArg(stepIn(that.getExpression()));
		tmp.setPostfix(postfix);
		expr = tmp;
		//return super.visitUnary(that, aVoid);
		return null;
	}

	@Override
	public Void visitMethodInvocation(MethodInvocationTree that, Void aVoid){
		MethodInvocation tmp = new MethodInvocation(scope);
		tmp.setMethod(stepIn(that.getMethodSelect()));
		for(ExpressionTree param: that.getArguments()){
			tmp.addParameter(stepIn(param));
		}
		expr = tmp;
		//return super.visitMethodInvocation(that, aVoid);
		return null;
	}

	@Override
	public Void visitMemberSelect(MemberSelectTree that, Void aVoid){
		FieldAccess tmp = new FieldAccess(scope);
		tmp.setSelected(stepIn(that.getExpression()));
		Ident id = new Ident(scope);
		id.setIdent(that.getIdentifier().toString());
		tmp.setIdent(id);
		expr = tmp;
		//return super.visitMemberSelect(that, aVoid);
		return null;
	}

	@Override
	public Void visitLiteral(LiteralTree that, Void aVoid){
		Literal tmp = new Literal(scope);
		switch(that.getKind()){
			case INT_LITERAL:     tmp.setValue((int)(that.getValue()));     break;
			case BOOLEAN_LITERAL: tmp.setValue((boolean)(that.getValue())); break;
			case CHAR_LITERAL:    tmp.setValue((char)(that.getValue()));    break;
			case DOUBLE_LITERAL:  tmp.setValue((double)(that.getValue()));  break;
			case FLOAT_LITERAL:   tmp.setValue((float)(that.getValue()));   break;
			case LONG_LITERAL:    tmp.setValue((long)(that.getValue()));    break;
			case STRING_LITERAL:  tmp.setValue((String)(that.getValue()));  break;
			case NULL_LITERAL:    tmp.setNull(); break;
			default: tmp.setUndefined(); break;
		}
		expr = tmp;
		//return super.visitLiteral(that, aVoid);
		return null;
	}

	private void setForceTypeCast(Expr lhs, Expr rhs){
		if(rhs instanceof Literal == false) return;
		Type ltype, rtype;
		ltype = lhs.getType();
		if(ltype instanceof ComponentType == true) return; // TODO
		while(ltype instanceof ArrayType){
			ltype = ((ArrayType)ltype).getElemType();
		}
		rtype = rhs.getType();
		if(ltype == rtype) return;
		//SynthesijerUtils.dump(lhs.getClass());
		//System.out.printf("JCExprVisitor: RHS is casted into %s from %s\n", ltype, rtype);
		//((Literal)rhs).castType(ltype);
	}

	@Override
	public Void visitAssignment(AssignmentTree that, Void aVoid){
		AssignExpr tmp = new AssignExpr(scope);
		tmp.setLhs(stepIn(that.getVariable()));
		tmp.setRhs(stepIn(that.getExpression()));
		setForceTypeCast(tmp.getLhs(), tmp.getRhs());
		expr = tmp;
		//return super.visitAssignment(that, aVoid);
		return null;
	}

	@Override
	public Void visitCompoundAssignment(CompoundAssignmentTree that, Void aVoid){
		AssignOp tmp = new AssignOp(scope);
		tmp.setLhs(stepIn(that.getVariable()));
		tmp.setRhs(stepIn(that.getExpression()));
		tmp.setOp(Op.getOp(that.getKind()));
		expr = tmp;
		//return super.visitCompoundAssignment(that, aVoid);
		return null;
	}

	@Override
	public Void visitNewArray(NewArrayTree that, Void aVoid){
		NewArray tmp = new NewArray(scope);
		for(ExpressionTree dim: that.getDimensions()){
			tmp.addDimExpr(stepIn(dim));
		}

		if(that.getInitializers() != null){ // ad-hoc: to support array initialization 
			for(ExpressionTree expr: that.getInitializers()){
				tmp.addElem(stepIn(expr));
			}
			if(that.getDimensions().size() == 0 && that.getInitializers().size() > 0){
				Literal d = new Literal(scope);
				d.setValue(that.getInitializers().size());
				tmp.addDimExpr(d);
//				SynthesijerUtils.warn("In " + scope.getModule().getName());
//				SynthesijerUtils.warn("Initialization with new expression is not supported.");
//				SynthesijerUtils.warn("Initialization values, " + that + " are not used.");				
			}
		}

		expr = tmp;

		//return super.visitNewArray(that, aVoid);
		return null;
	}

	@Override
	public Void visitArrayAccess(ArrayAccessTree that, Void aVoid){
		ArrayAccess tmp = new ArrayAccess(scope);
		{
			tmp.setIndexed(stepIn(that.getExpression()));
		}
		{
			tmp.setIndex(stepIn(that.getIndex()));
		}
		expr = tmp;
		//return super.visitArrayAccess(that, aVoid);
		return null;
	}

	@Override
	public Void visitTypeCast(TypeCastTree that, Void aVoid){
		TypeCast tmp = new TypeCast(scope);
		tmp.setExpr(stepIn(that.getExpression()));
		tmp.setTargetType(TypeBuilder.genType(that.getType()));
		expr = tmp;
		//return super.visitTypeCast(that, aVoid);
		return null;
	}

	@Override
	public Void visitParenthesized(ParenthesizedTree that, Void aVoid){
		ParenExpr tmp = new ParenExpr(scope);
		tmp.setExpr(stepIn(that.getExpression()));
		expr = tmp;
		//return super.visitParenthesized(that, aVoid);
		return null;
	}

	@Override
	public Void visitNewClass(NewClassTree that, Void aVoid){
		NewClassExpr tmp = new NewClassExpr(scope);
		tmp.setClassName(that.getIdentifier().toString());
		for(ExpressionTree arg: that.getArguments()){
			tmp.addParam(stepIn(arg));
		}
		expr = tmp;
		//return super.visitNewClass(that, aVoid);
		return null;
	}

	@Override
	public Void visitConditionalExpression(ConditionalExpressionTree that, Void aVoid){
		CondExpr tmp = new CondExpr(scope);
		tmp.setCond(stepIn(that.getCondition()));
		tmp.setTruePart(stepIn(that.getTrueExpression()));
		tmp.setFalsePart(stepIn(that.getFalseExpression()));
		expr = tmp;
		//return super.visitConditionalExpression(that, aVoid);
		return null;
	}

	@Override
	public Void visitOther(Tree t, Void aVoid){
		SynthesijerUtils.error("[JCExprVisitor] The following is unexpected in this context.");
		SynthesijerUtils.dump(t);
		return super.visitOther(t, aVoid);
	}
}
