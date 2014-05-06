package synthesijer.jcfrontend;

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

public class ExprBuilder {

	public static Expr genExpr(JCExpression that, Scope scope){
		if(that instanceof JCIdent){
			return genIdent((JCIdent)that, scope);
		}else if(that instanceof JCBinary){
			return genBinaryExpr((JCBinary)that, scope);
		}else if(that instanceof JCUnary){
			return genUnaryExpr((JCUnary)that, scope);
		}else if(that instanceof JCMethodInvocation){
			return genMethodInvocation((JCMethodInvocation)that, scope);
		}else if(that instanceof JCFieldAccess){
			return genFieldAccess((JCFieldAccess)that, scope);
		}else if(that instanceof JCLiteral){
			return genLiteral((JCLiteral)that, scope); 
		}else if(that instanceof JCAssign){
			return genAssignExpr((JCAssign)that, scope);
		}else if(that instanceof JCAssignOp){
			return genAssignOp((JCAssignOp)that, scope);
		}else if(that instanceof JCNewArray){
			return genNewArray((JCNewArray)that, scope);
		}else if(that instanceof JCArrayAccess){
			return genArrayAccess((JCArrayAccess)that, scope);
		}else if(that instanceof JCTypeCast){
			return genTypeCast((JCTypeCast)that, scope);
		}else if(that instanceof JCParens){
			return genParenExpr((JCParens)that, scope);
		}else if(that instanceof JCNewClass){
			return genNewClassExpr((JCNewClass)that, scope);
		}else{
			System.err.printf("unsupported expr: %s (%s)\n", that, that.getClass());
		}
		return null;
	}
	
	private static Ident genIdent(JCIdent that, Scope scope){
		Ident expr = new Ident(scope);
		expr.setIdent(that.toString());
		return expr;
	}
	
	private static BinaryExpr genBinaryExpr(JCBinary that, Scope scope){
		BinaryExpr expr = new BinaryExpr(scope);
		expr.setLhs(genExpr(that.lhs, scope));
		expr.setRhs(genExpr(that.rhs, scope));
		expr.setOp(Op.getOp(that.operator.name.toString()));
		return expr;
	}
	
	private static UnaryExpr genUnaryExpr(JCUnary that, Scope scope){
		UnaryExpr expr = new UnaryExpr(scope);
		expr.setOp(Op.getOp(that.operator.name.toString()));
		expr.setArg(genExpr(that.arg, scope));
		return expr;
	}
	
	private static MethodInvocation genMethodInvocation(JCMethodInvocation that, Scope scope){
		MethodInvocation expr = new MethodInvocation(scope);
		expr.setMethod(genExpr(that.getMethodSelect(), scope));
		for(JCExpression param: that.args){
			expr.addParameter(genExpr(param, scope));
		}
		return expr;
	}
	
	private static FieldAccess genFieldAccess(JCFieldAccess that, Scope scope){
		FieldAccess expr = new FieldAccess(scope);
		expr.setSelecteExpr(genExpr(that.selected, scope));
		Ident id = new Ident(scope);
		id.setIdent(that.name.toString());
		expr.setIdent(id);
		return expr;
	}
	
	private static Literal genLiteral(JCLiteral that, Scope scope){
		Literal expr = new Literal(scope);
		switch(that.getKind()){
		case INT_LITERAL:     expr.setValue((int)(that.getValue()));     break;
		case BOOLEAN_LITERAL: expr.setValue((boolean)(that.getValue())); break;
		case CHAR_LITERAL:    expr.setValue((char)(that.getValue()));    break;
		case DOUBLE_LITERAL:  expr.setValue((double)(that.getValue()));  break;
		case FLOAT_LITERAL:   expr.setValue((float)(that.getValue()));   break;
		case LONG_LITERAL:    expr.setValue((long)(that.getValue()));    break;
		case NULL_LITERAL:    expr.setValue(Literal.LITERAL_KIND.NULL);  break;
		case STRING_LITERAL:  expr.setValue((String)(that.getValue()));  break;
		default: expr.setValue(Literal.LITERAL_KIND.UNKNOWN);            break;
		}
		return expr;
	}
	
	private static AssignExpr genAssignExpr(JCAssign that, Scope scope){
		AssignExpr expr = new AssignExpr(scope);
		expr.setLhs(genExpr(that.lhs, scope));
		expr.setRhs(genExpr(that.rhs, scope));
		return expr;
	}
	
	private static AssignOp genAssignOp(JCAssignOp that, Scope scope){
		AssignOp expr = new AssignOp(scope);
		expr.setLhs(genExpr(that.lhs, scope));
		expr.setRhs(genExpr(that.rhs, scope));
		expr.setOp(Op.getOp(that.operator.name.toString()));
		return expr;
	}
	
	private static NewArray genNewArray(JCNewArray that, Scope scope){
		NewArray expr = new NewArray(scope);
		for(JCExpression e: that.dims){
			expr.addDimExpr(genExpr(e, scope));
		}
		return expr;
	}
	
	private static ArrayAccess genArrayAccess(JCArrayAccess that, Scope scope){
		ArrayAccess expr = new ArrayAccess(scope);
		expr.setIndexed(genExpr(that.indexed, scope));
		expr.setIndex(genExpr(that.index, scope));
		return expr;
	}
	
	private static TypeCast genTypeCast(JCTypeCast that, Scope scope){
		TypeCast expr = new TypeCast(scope);
		expr.setExpr(genExpr(that.expr, scope));
		return expr;
	}
	
	private static ParenExpr genParenExpr(JCParens that, Scope scope){
		ParenExpr expr = new ParenExpr(scope);
		expr.setExpr(genExpr(that.expr, scope));
		return expr;
	}

	private static NewClassExpr genNewClassExpr(JCNewClass that, Scope scope){
		NewClassExpr expr = new NewClassExpr(scope);
		expr.setClassName(that.constructor.owner.toString());
		for(JCExpression e: that.args){
			expr.addParam(ExprBuilder.genExpr(e, scope));
		}
		return expr;
	}

}
