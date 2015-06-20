package synthesijer.jcfrontend;

import openjdk.com.sun.tools.javac.tree.JCTree;
import openjdk.com.sun.tools.javac.tree.JCTree.JCBlock;
import openjdk.com.sun.tools.javac.tree.JCTree.JCBreak;
import openjdk.com.sun.tools.javac.tree.JCTree.JCCase;
import openjdk.com.sun.tools.javac.tree.JCTree.JCContinue;
import openjdk.com.sun.tools.javac.tree.JCTree.JCDoWhileLoop;
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
import openjdk.com.sun.tools.javac.tree.JCTree.Visitor;
import synthesijer.SynthesijerUtils;
import synthesijer.ast.Expr;
import synthesijer.ast.Module;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.Type;
import synthesijer.ast.statement.BlockStatement;
import synthesijer.ast.statement.BreakStatement;
import synthesijer.ast.statement.ContinueStatement;
import synthesijer.ast.statement.DoWhileStatement;
import synthesijer.ast.statement.ExprStatement;
import synthesijer.ast.statement.ForStatement;
import synthesijer.ast.statement.IfStatement;
import synthesijer.ast.statement.ReturnStatement;
import synthesijer.ast.statement.SkipStatement;
import synthesijer.ast.statement.SwitchStatement;
import synthesijer.ast.statement.TryStatement;
import synthesijer.ast.statement.VariableDecl;
import synthesijer.ast.statement.WhileStatement;

public class JCStmtVisitor extends Visitor{
	
	public final Scope scope;
	
	private Statement stmt;
	
	public JCStmtVisitor(Scope scope){
		this.scope = scope;
	}
	
	public Statement getStatement(){
		return stmt;
	}
	
	private Statement stepIn(JCTree that, Scope scope){
		JCStmtVisitor visitor = new JCStmtVisitor(scope);
		that.accept(visitor);
		return visitor.getStatement();
	}
	
	private Expr stepIn(JCExpression that, Scope scope){
		JCExprVisitor visitor = new JCExprVisitor(scope);
		that.accept(visitor);
		return visitor.getExpr();
	}
	
	private BlockStatement wrapBlockStatement(Statement stmt){
		if(stmt instanceof BlockStatement){
			return (BlockStatement)stmt;
		}else{
			BlockStatement block = new BlockStatement(stmt.getScope());
			block.addStatement(stmt);
			return block;
		}
	}
	
	public void visitIf(JCIf that){
		IfStatement tmp = new IfStatement(scope);
		tmp.setCondition(stepIn(that.cond, scope));
		tmp.setThenPart(wrapBlockStatement(stepIn(that.thenpart, scope)));
		if(that.elsepart != null){
			tmp.setElsePart(wrapBlockStatement(stepIn(that.elsepart, scope)));
		}
		stmt = tmp;
	}
	
	public void visitForLoop(JCForLoop that){
		ForStatement tmp = new ForStatement(scope);
		for(JCStatement s: that.init){
			//tmp.addInitialize(stepIn(s, scope));
			tmp.addInitialize(stepIn(s, tmp));
		}
		tmp.setCondition(stepIn(that.cond, tmp));
		for(JCStatement s: that.step){
			tmp.addUpdate(stepIn(s, tmp));
		}
		tmp.setBody(wrapBlockStatement(stepIn(that.body, tmp)));
		stmt = tmp;
	}
	
	public void visitWhileLoop(JCWhileLoop that){
		WhileStatement tmp = new WhileStatement(scope);
		tmp.setCondition(stepIn(that.cond, scope));
		tmp.setBody(wrapBlockStatement(stepIn(that.body, scope)));
		stmt = tmp;
	}
	
	public void visitDoLoop(JCDoWhileLoop that){
		DoWhileStatement tmp = new DoWhileStatement(scope);
		tmp.setCondition(stepIn(that.cond, scope));
		tmp.setBody(wrapBlockStatement(stepIn(that.body, scope)));
		stmt = tmp;
	}
	
	public void visitBlock(JCBlock that){
		BlockStatement tmp = new BlockStatement(scope);
		for(JCStatement s: that.getStatements()){
			JCStmtVisitor visitor = new JCStmtVisitor(scope);
			s.accept(visitor);
			tmp.addStatement(visitor.getStatement());
		}
		stmt = tmp;
	}
	
	public void visitReturn(JCReturn that){
		ReturnStatement tmp = new ReturnStatement(scope);
		if(that.expr != null){
			JCExprVisitor visitor = new JCExprVisitor(scope);
			that.expr.accept(visitor);
			tmp.setExpr(visitor.getExpr());
		}
		stmt = tmp;
	}
	
	public void visitExec(JCExpressionStatement that){
		JCExprVisitor visitor = new JCExprVisitor(scope);
		that.expr.accept(visitor);
		stmt = new ExprStatement(scope, visitor.getExpr());
	}
	
	public void visitBreak(JCBreak that){
		stmt = new BreakStatement(scope);
	}
	
	public void visitContinue(JCContinue that){
		stmt = new ContinueStatement(scope);
	}
	
	public void visitSkip(JCSkip that){
		stmt = new SkipStatement(scope);
	}
	
	public void visitTry(JCTry that){
		TryStatement tmp = new TryStatement(scope);
		JCStmtVisitor visitor = new JCStmtVisitor(scope);
		that.body.accept(visitor);
		tmp.setBody(visitor.getStatement());
		stmt = tmp;
	}
	
	public void visitSynchronized(JCSynchronized that){
		visitBlock(that.body);
	}
	
	public void visitSwitch(JCSwitch that){
		SwitchStatement tmp = new SwitchStatement(scope);
		tmp.setSelector(stepIn(that.selector, scope));
		for(JCCase c: that.cases){
			SwitchStatement.Elem elem;
			if(c.pat != null){
				elem = tmp.newElement(stepIn(c.pat, scope));
			}else{
				elem = tmp.getDefaultElement();
			}
			for(JCStatement s: c.stats){
				elem.addStatement(stepIn(s, scope));
			}
		}
		stmt = tmp;
	}
	
	public void visitVarDef(JCVariableDecl that) {
		String name = that.getName().toString();
		Type type = TypeBuilder.genType(that.getType());
		Expr init;
		if(that.init != null){
			JCExprVisitor visitor = new JCExprVisitor(scope);
			that.init.accept(visitor);
			init = visitor.getExpr();
		}else{
			init = null;
		}
		VariableDecl tmp = new VariableDecl(scope, name, type, init);
		if(JCFrontendUtils.isGlobalConstant(that.mods)){
			tmp.setGlobalConstant(true);
		}
		if(JCFrontendUtils.isPrivate(that.mods) == false && scope instanceof Module){
			tmp.setPublic(true);
		}
		if(JCFrontendUtils.isVolatile(that.mods)){
			tmp.setVolatile(true);
		}
		scope.addVariableDecl(tmp);
		stmt = tmp;
	}
	
	public void visitTree(JCTree t){
		SynthesijerUtils.error("[JCStmtVisitor] The following is unexpected in this context.");
		SynthesijerUtils.dump(t);
	}

}
