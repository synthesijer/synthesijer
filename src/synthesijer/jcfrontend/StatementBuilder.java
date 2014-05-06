package synthesijer.jcfrontend;

import openjdk.com.sun.tools.javac.tree.JCTree.JCBlock;
import openjdk.com.sun.tools.javac.tree.JCTree.JCBreak;
import openjdk.com.sun.tools.javac.tree.JCTree.JCCase;
import openjdk.com.sun.tools.javac.tree.JCTree.JCContinue;
import openjdk.com.sun.tools.javac.tree.JCTree.JCExpression;
import openjdk.com.sun.tools.javac.tree.JCTree.JCExpressionStatement;
import openjdk.com.sun.tools.javac.tree.JCTree.JCForLoop;
import openjdk.com.sun.tools.javac.tree.JCTree.JCIf;
import openjdk.com.sun.tools.javac.tree.JCTree.JCReturn;
import openjdk.com.sun.tools.javac.tree.JCTree.JCSkip;
import openjdk.com.sun.tools.javac.tree.JCTree.JCStatement;
import openjdk.com.sun.tools.javac.tree.JCTree.JCSwitch;
import openjdk.com.sun.tools.javac.tree.JCTree.JCSynchronized;
import openjdk.com.sun.tools.javac.tree.JCTree.JCTry;
import openjdk.com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import openjdk.com.sun.tools.javac.tree.JCTree.JCWhileLoop;
import synthesijer.SynthesijerUtils;
import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.Type;
import synthesijer.ast.statement.BlockStatement;
import synthesijer.ast.statement.BreakStatement;
import synthesijer.ast.statement.ContinueStatement;
import synthesijer.ast.statement.ExprStatement;
import synthesijer.ast.statement.ForStatement;
import synthesijer.ast.statement.IfStatement;
import synthesijer.ast.statement.ReturnStatement;
import synthesijer.ast.statement.SkipStatement;
import synthesijer.ast.statement.SwitchStatement;
import synthesijer.ast.statement.SynchronizedBlock;
import synthesijer.ast.statement.TryStatement;
import synthesijer.ast.statement.VariableDecl;
import synthesijer.ast.statement.WhileStatement;

public class StatementBuilder {

	public static Statement buildStatement(JCStatement that, Scope scope){
		if(that instanceof JCIf){
			return genIfStatement((JCIf)that, scope);
		}else if(that instanceof JCForLoop){
			return genForStatement((JCForLoop)that, scope);
		}else if(that instanceof JCWhileLoop){
			return genWhileStatement((JCWhileLoop)that, scope);
		}else if(that instanceof JCBlock){
			return genBlockStatement((JCBlock)that, scope);
		}else if(that instanceof JCReturn){
			return genReturnStatement((JCReturn)that, scope);
		}else if(that instanceof JCExpressionStatement){
			return genExprStatement((JCExpressionStatement)that, scope);
		}else if(that instanceof JCVariableDecl){
			return genVariableDecl((JCVariableDecl)that, scope);
		}else if(that instanceof JCBreak){
			return genBreakStatement((JCBreak)that, scope);
		}else if(that instanceof JCContinue){
			return genContinueStatement((JCContinue)that, scope);
		}else if(that instanceof JCSkip){
			return genSkipStatement((JCSkip)that, scope);
		}else if(that instanceof JCTry){
			return genTryStatement((JCTry)that, scope);
		}else if(that instanceof JCSynchronized){
			return genSynchronizedBlock((JCSynchronized)that, scope);
		}else if(that instanceof JCSwitch){
			return genSwitchStatement((JCSwitch)that, scope);
		}else{
			SynthesijerUtils.error(String.format("else@buildStatement: %s (%s)\n", that, that.getClass()));
		}
		return null;
	}
	
	private static IfStatement genIfStatement(JCIf that, Scope scope){
		IfStatement stmt = new IfStatement(scope);
		stmt.setCondition(ExprBuilder.genExpr(that.cond, scope));
		stmt.setThenPart(buildStatement(that.thenpart, scope));
		if(that.elsepart != null){
			stmt.setElsePart(buildStatement(that.elsepart, scope));
		}
		return stmt;
	}
	
	private static ForStatement genForStatement(JCForLoop that, Scope scope){
		ForStatement stmt = new ForStatement(scope);
		for(JCStatement s: that.init){
			stmt.addInitialize(buildStatement(s, stmt));
		}
		stmt.setCondition(ExprBuilder.genExpr(that.cond, stmt));
		for(JCStatement s: that.step){
			stmt.addUpdate(buildStatement(s, stmt));
		}
		stmt.setBody(buildStatement(that.body, stmt));
		return stmt;
	}
	
	private static WhileStatement genWhileStatement(JCWhileLoop that, Scope scope){
		WhileStatement stmt = new WhileStatement(scope);
		stmt.setCondition(ExprBuilder.genExpr(that.cond, scope));
		stmt.setBody(buildStatement(that.body, scope));
		return stmt;
	}
	
	private static BlockStatement genBlockStatement(JCBlock that, Scope scope){ 
		BlockStatement stmt = new BlockStatement(scope);
		for(JCStatement s: that.getStatements()){
			stmt.addStatement(buildStatement(s, stmt));
		}
		return stmt;
	}
	
	private static ReturnStatement genReturnStatement(JCReturn that, Scope scope){
		ReturnStatement stmt = new ReturnStatement(scope);
		if(that.expr != null){
			stmt.setExpr(ExprBuilder.genExpr(that.expr, scope));
		}
		return stmt;
	}
	
	private static ExprStatement genExprStatement(JCExpressionStatement that, Scope scope){
		return new ExprStatement(scope, ExprBuilder.genExpr(that.expr, scope));
	}
	
	private static BreakStatement genBreakStatement(JCBreak that, Scope scope){
		return new BreakStatement(scope);
	}
	
	private static ContinueStatement genContinueStatement(JCContinue that, Scope scope){
		return new ContinueStatement(scope);
	}
	
	private static SkipStatement genSkipStatement(JCSkip that, Scope scope){
		return new SkipStatement(scope);
	}
	
	private static TryStatement genTryStatement(JCTry that, Scope scope){
		TryStatement stmt = new TryStatement(scope);
		stmt.setBody(buildStatement(that.body, scope));
		return stmt;
	}
	
	private static SynchronizedBlock genSynchronizedBlock(JCSynchronized that, Scope scope){
		return new SynchronizedBlock(scope);
	}
	
	private static SwitchStatement genSwitchStatement(JCSwitch that, Scope scope){
		SwitchStatement stmt = new SwitchStatement(scope);
		stmt.setSelector(ExprBuilder.genExpr(that.selector, scope));
		for(JCCase c: that.cases){
			Expr pat = (c.pat == null) ? null : ExprBuilder.genExpr(c.pat, stmt);
			SwitchStatement.Elem elem = stmt.newElement(pat);
			for(JCStatement s: c.stats){
				elem.addStatement(buildStatement(s, stmt));
			}
		}
		return stmt;
	}
	
	public static VariableDecl genVariableDecl(JCVariableDecl that, Scope scope){
		String name = that.getName().toString();
		Type type = TypeBuilder.genType(that.getType());
		Expr init;
		if(that.init != null){
			if(that.init instanceof JCExpression){
				init = ExprBuilder.genExpr((JCExpression)(that.init), scope);
			}else{
				init = null;
				SynthesijerUtils.error(String.format("Illegal context: %s (%s)", that.init, that.init.getClass()));
			}
		}else{
			init = null;
		}
		return new VariableDecl(scope, name, type, init);
	}

}
