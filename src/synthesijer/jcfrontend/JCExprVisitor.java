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
import synthesijer.ast.Type;
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
import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.ComponentType;

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
	
	private Expr stepIn(JCExpression expr){
		JCExprVisitor visitor = new JCExprVisitor(scope);
		expr.accept(visitor);
		return visitor.getExpr();
	}
			
	public void visitBinary(JCBinary that){
		BinaryExpr tmp = new BinaryExpr(scope);
		Expr lhs = stepIn(that.lhs);
		tmp.setLhs(lhs);
		Expr rhs = stepIn(that.rhs);
		tmp.setRhs(rhs);
		tmp.setOp(Op.getOp(that.operator.name.toString()));
		expr = tmp;
	}
	
	public void visitUnary(JCUnary that){
		UnaryExpr tmp = new UnaryExpr(scope);
		tmp.setOp(Op.getOp(that.operator.name.toString()));
		tmp.setArg(stepIn(that.arg));
		expr = tmp;
	}
	
	public void visitApply(JCMethodInvocation that){
		MethodInvocation tmp = new MethodInvocation(scope);
		tmp.setMethod(stepIn(that.getMethodSelect()));
		for(JCExpression param: that.args){
			tmp.addParameter(stepIn(param));
		}
		expr = tmp;
	}
	
	public void visitSelect(JCFieldAccess that){
		FieldAccess tmp = new FieldAccess(scope);
		tmp.setSelected(stepIn(that.selected));
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
		case STRING_LITERAL:  tmp.setValue((String)(that.getValue()));  break;
		case NULL_LITERAL:    tmp.setNull(); break;
		default: tmp.setUndefined(); break;
		}
		expr = tmp;
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
		((Literal)rhs).castType(ltype);
	}
	
	public void visitAssign(JCAssign that){
		AssignExpr tmp = new AssignExpr(scope);
		tmp.setLhs(stepIn(that.lhs));
		tmp.setRhs(stepIn(that.rhs));
		setForceTypeCast(tmp.getLhs(), tmp.getRhs());
		expr = tmp;
	}
	
	public void visitAssignop(JCAssignOp that){
		AssignOp tmp = new AssignOp(scope);
		tmp.setLhs(stepIn(that.lhs));
		tmp.setRhs(stepIn(that.rhs));
		tmp.setOp(Op.getOp(that.operator.name.toString()));
		expr = tmp;
	}
	
	public void visitNewArray(JCNewArray that){
		NewArray tmp = new NewArray(scope);
		for(JCExpression dim: that.dims){
			tmp.addDimExpr(stepIn(dim));
		}
		if(that.elems != null){
			for(JCExpression expr: that.elems){
				tmp.addElem(stepIn(expr));
			}
		}
		expr = tmp;
	}
	
	public void visitIndexed(JCArrayAccess that){
		ArrayAccess tmp = new ArrayAccess(scope);
		{
			tmp.setIndexed(stepIn(that.indexed));
		}
		{
			tmp.setIndex(stepIn(that.index));
		}
		expr = tmp;
	}
	
	public void visitTypeCast(JCTypeCast that){
		TypeCast tmp = new TypeCast(scope);
		tmp.setExpr(stepIn(that.expr));
		expr = tmp;
	}
	
	public void visitParens(JCParens that){
		ParenExpr tmp = new ParenExpr(scope);
		tmp.setExpr(stepIn(that.expr));
		expr = tmp;
	}

	public void visitNewClass(JCNewClass that){
		NewClassExpr tmp = new NewClassExpr(scope);
		tmp.setClassName(that.constructor.owner.toString());
		for(JCExpression arg: that.args){
			tmp.addParam(stepIn(arg));
		}
		expr = tmp;
	}

	public void visitTree(JCTree t){
		SynthesijerUtils.error("[JCExprVisitor] The following is unexpected in this context.");
		SynthesijerUtils.dump(t);
	}
}
