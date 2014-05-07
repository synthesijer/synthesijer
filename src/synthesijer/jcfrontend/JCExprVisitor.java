package synthesijer.jcfrontend;

import openjdk.com.sun.tools.javac.tree.JCTree;
import openjdk.com.sun.tools.javac.tree.JCTree.JCArrayAccess;
import openjdk.com.sun.tools.javac.tree.JCTree.JCAssign;
import openjdk.com.sun.tools.javac.tree.JCTree.JCAssignOp;
import openjdk.com.sun.tools.javac.tree.JCTree.JCBinary;
import openjdk.com.sun.tools.javac.tree.JCTree.JCExpression;
import openjdk.com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import openjdk.com.sun.tools.javac.tree.JCTree.JCIdent;
import openjdk.com.sun.tools.javac.tree.JCTree.JCLiteral;
import openjdk.com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import openjdk.com.sun.tools.javac.tree.JCTree.JCNewArray;
import openjdk.com.sun.tools.javac.tree.JCTree.JCNewClass;
import openjdk.com.sun.tools.javac.tree.JCTree.JCParens;
import openjdk.com.sun.tools.javac.tree.JCTree.JCTypeCast;
import openjdk.com.sun.tools.javac.tree.JCTree.JCUnary;
import openjdk.com.sun.tools.javac.tree.JCTree.Visitor;
import synthesijer.SynthesijerUtils;
import synthesijer.ast.Expr;
import synthesijer.ast.Op;
import synthesijer.ast.Scope;
import synthesijer.ast.expr.ArrayAccess;
import synthesijer.ast.expr.AssignExpr;
import synthesijer.ast.expr.AssignOp;
import synthesijer.ast.expr.BinaryExpr;
import synthesijer.ast.expr.FieldAccess;
import synthesijer.ast.expr.Ident;
import synthesijer.ast.expr.Literal;
import synthesijer.ast.expr.MethodInvocation;
import synthesijer.ast.expr.NewArray;
import synthesijer.ast.expr.NewClassExpr;
import synthesijer.ast.expr.ParenExpr;
import synthesijer.ast.expr.TypeCast;
import synthesijer.ast.expr.UnaryExpr;

public class JCExprVisitor extends Visitor{
	
	public final Scope scope;
	
	private Expr expr;
	
	public JCExprVisitor(Scope scope){
		this.scope = scope;
	}
	
	public Expr getExpr(){
		return expr;
	}

	public void visitIdent(JCIdent that){
		Ident tmp = new Ident(scope);
		tmp.setIdent(that.toString());
		expr = tmp;
	}
		
	public void visitBinary(JCBinary that){
		BinaryExpr tmp = new BinaryExpr(scope);
		JCExprVisitor lhsVisitor = new JCExprVisitor(scope);
		JCExprVisitor rhsVisitor = new JCExprVisitor(scope);
		that.lhs.accept(lhsVisitor);
		that.lhs.accept(rhsVisitor);
		tmp.setLhs(lhsVisitor.getExpr());
		tmp.setRhs(rhsVisitor.getExpr());
		tmp.setOp(Op.getOp(that.operator.name.toString()));
		expr = tmp;
	}
	
	public void visitUnary(JCUnary that){
		UnaryExpr tmp = new UnaryExpr(scope);
		JCExprVisitor visitor = new JCExprVisitor(scope);
		that.arg.accept(visitor);
		tmp.setOp(Op.getOp(that.operator.name.toString()));
		tmp.setArg(visitor.getExpr());
		expr = tmp;
	}
	
	public void visitApply(JCMethodInvocation that){
		MethodInvocation tmp = new MethodInvocation(scope);
		JCExprVisitor selector = new JCExprVisitor(scope);
		that.getMethodSelect().accept(selector);
		tmp.setMethod(selector.getExpr());
		for(JCExpression param: that.args){
			JCExprVisitor arg = new JCExprVisitor(scope);
			param.accept(arg);
			tmp.addParameter(arg.getExpr());
		}
		expr = tmp;
	}
	
	public void visitSelect(JCFieldAccess that){
		FieldAccess tmp = new FieldAccess(scope);
		JCExprVisitor visitor = new JCExprVisitor(scope);
		that.selected.accept(visitor);
		tmp.setSelected(visitor.getExpr());
		Ident id = new Ident(scope);
		id.setIdent(that.name.toString());
		tmp.setIdent(id);
		expr = tmp;
	}
	
	public void visitLiteral(JCLiteral that){
		Literal tmp = new Literal(scope);
		switch(that.getKind()){
		case INT_LITERAL:     tmp.setValue((int)(that.getValue()));     break;
		case BOOLEAN_LITERAL: tmp.setValue((boolean)(that.getValue())); break;
		case CHAR_LITERAL:    tmp.setValue((char)(that.getValue()));    break;
		case DOUBLE_LITERAL:  tmp.setValue((double)(that.getValue()));  break;
		case FLOAT_LITERAL:   tmp.setValue((float)(that.getValue()));   break;
		case LONG_LITERAL:    tmp.setValue((long)(that.getValue()));    break;
		case NULL_LITERAL:    tmp.setValue(Literal.LITERAL_KIND.NULL);  break;
		case STRING_LITERAL:  tmp.setValue((String)(that.getValue()));  break;
		default: tmp.setValue(Literal.LITERAL_KIND.UNKNOWN);            break;
		}
		expr = tmp;
	}
	
	public void visitAssign(JCAssign that){
		AssignExpr tmp = new AssignExpr(scope);
		{
			JCExprVisitor visitor = new JCExprVisitor(scope);
			that.lhs.accept(visitor);
			tmp.setLhs(visitor.getExpr());
		}
		{
			JCExprVisitor visitor = new JCExprVisitor(scope);
			that.rhs.accept(visitor);
			tmp.setRhs(visitor.getExpr());
		}
		expr = tmp;
	}
	
	public void visitAssignop(JCAssignOp that){
		AssignOp tmp = new AssignOp(scope);
		{
			JCExprVisitor visitor = new JCExprVisitor(scope);
			that.lhs.accept(visitor);
			tmp.setLhs(visitor.getExpr());
		}
		{
			JCExprVisitor visitor = new JCExprVisitor(scope);
			that.rhs.accept(visitor);
			tmp.setRhs(visitor.getExpr());
		}
		tmp.setOp(Op.getOp(that.operator.name.toString()));
		expr = tmp;
	}
	
	public void visitNewArray(JCNewArray that){
		NewArray tmp = new NewArray(scope);
		for(JCExpression dim: that.dims){
			JCExprVisitor visitor = new JCExprVisitor(scope);
			dim.accept(visitor);
			tmp.addDimExpr(visitor.getExpr());
		}
		expr = tmp;
	}
	
	public void visitIndexed(JCArrayAccess that){
		ArrayAccess tmp = new ArrayAccess(scope);
		{
			JCExprVisitor visitor = new JCExprVisitor(scope);
			that.indexed.accept(visitor);
			tmp.setIndexed(visitor.getExpr());
		}
		{
			JCExprVisitor visitor = new JCExprVisitor(scope);
			that.index.accept(visitor);
			tmp.setIndex(visitor.getExpr());
		}
		expr = tmp;
	}
	
	public void visitTypeCast(JCTypeCast that){
		TypeCast tmp = new TypeCast(scope);
		JCExprVisitor visitor = new JCExprVisitor(scope);
		that.expr.accept(visitor);
		tmp.setExpr(visitor.getExpr());
		expr = tmp;
	}
	
	public void visitParens(JCParens that){
		ParenExpr tmp = new ParenExpr(scope);
		JCExprVisitor visitor = new JCExprVisitor(scope);
		that.expr.accept(visitor);
		tmp.setExpr(visitor.getExpr());
		expr = tmp;
	}

	public void visitNewClass(JCNewClass that){
		NewClassExpr tmp = new NewClassExpr(scope);
		tmp.setClassName(that.constructor.owner.toString());
		for(JCExpression arg: that.args){
			JCExprVisitor visitor = new JCExprVisitor(scope);
			arg.accept(visitor);
			tmp.addParam(visitor.getExpr());
		}
		expr = tmp;
	}

	public void visitTree(JCTree t){
		SynthesijerUtils.error("[JCExprVisitor] The following is unexpected in this context.");
		SynthesijerUtils.dump(t);
	}
}
