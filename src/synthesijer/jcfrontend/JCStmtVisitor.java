package synthesijer.jcfrontend;

import com.sun.source.tree.Tree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ReturnTree;
//import com.sun.source.tree.JCSkip;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.util.TreeScanner;
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

public class JCStmtVisitor extends TreeScanner<Void, Void>{
	
    public final Scope scope;
	
    private Statement stmt;
	
    public JCStmtVisitor(Scope scope){
		this.scope = scope;
    }
	
    public Statement getStatement(){
		return stmt;
    }
	
    private Statement stepIn(Tree that, Scope scope){
		JCStmtVisitor visitor = new JCStmtVisitor(scope);
		that.accept(visitor, null);
		return visitor.getStatement();
    }
	
    private Expr stepIn(ExpressionTree that, Scope scope){
		JCExprVisitor visitor = new JCExprVisitor(scope);
		that.accept(visitor, null);
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
	
	@Override
    public Void visitIf(IfTree that, Void aVoid){
		IfStatement tmp = new IfStatement(scope);
		tmp.setCondition(stepIn(that.getCondition(), scope));
		tmp.setThenPart(wrapBlockStatement(stepIn(that.getThenStatement(), scope)));
		if(that.getElseStatement() != null){
			tmp.setElsePart(wrapBlockStatement(stepIn(that.getElseStatement(), scope)));
		}
		stmt = tmp;
		return super.visitIf(that, aVoid);
    }
	
	@Override
    public Void visitForLoop(ForLoopTree that, Void aVoid){
		ForStatement tmp = new ForStatement(scope);
		for(StatementTree s: that.getInitializer()){
			//tmp.addInitialize(stepIn(s, scope));
			tmp.addInitialize(stepIn(s, tmp));
		}
		tmp.setCondition(stepIn(that.getCondition(), tmp));
		for(StatementTree s: that.getUpdate()){
			tmp.addUpdate(stepIn(s, tmp));
		}
		tmp.setBody(wrapBlockStatement(stepIn(that.getStatement(), tmp)));
		stmt = tmp;
		return super.visitForLoop(that, aVoid);
    }
	
	@Override
    public Void visitWhileLoop(WhileLoopTree that, Void aVoid){
		WhileStatement tmp = new WhileStatement(scope);
		tmp.setCondition(stepIn(that.getCondition(), scope));
		tmp.setBody(wrapBlockStatement(stepIn(that.getStatement(), scope)));
		stmt = tmp;
		return super.visitWhileLoop(that, aVoid);
    }
	
	@Override
    public Void visitDoWhileLoop(DoWhileLoopTree that, Void aVoid){
		DoWhileStatement tmp = new DoWhileStatement(scope);
		tmp.setCondition(stepIn(that.getCondition(), scope));
		tmp.setBody(wrapBlockStatement(stepIn(that.getStatement(), scope)));
		stmt = tmp;
		return super.visitDoWhileLoop(that, aVoid);
    }
	
	@Override
    public Void visitBlock(BlockTree that, Void aVoid){
		BlockStatement tmp = new BlockStatement(scope);
		for(StatementTree s: that.getStatements()){
			JCStmtVisitor visitor = new JCStmtVisitor(scope);
			s.accept(visitor, null);
			tmp.addStatement(visitor.getStatement());
		}
		stmt = tmp;
		return super.visitBlock(that, aVoid);
    }
	
	@Override
    public Void visitReturn(ReturnTree that, Void aVoid){
		ReturnStatement tmp = new ReturnStatement(scope);
		if(that.getExpression() != null){
			JCExprVisitor visitor = new JCExprVisitor(scope);
			that.getExpression().accept(visitor, null);
			tmp.setExpr(visitor.getExpr());
		}
		stmt = tmp;
		return super.visitReturn(that, aVoid);
    }
	
	@Override
    public Void visitExpressionStatement(ExpressionStatementTree that, Void aVoid){
		JCExprVisitor visitor = new JCExprVisitor(scope);
		that.getExpression().accept(visitor, null);
		stmt = new ExprStatement(scope, visitor.getExpr());
		return super.visitExpressionStatement(that, aVoid);
    }
	
	@Override
    public Void visitBreak(BreakTree that, Void aVoid){
		stmt = new BreakStatement(scope);
		return super.visitBreak(that, aVoid);
    }
	
	@Override
    public Void visitContinue(ContinueTree that, Void aVoid){
		stmt = new ContinueStatement(scope);
		return super.visitContinue(that, aVoid);
    }
	
    //public void visitSkip(JCSkip that){
	//	stmt = new SkipStatement(scope);
    //}
	
	@Override
    public Void visitTry(TryTree that, Void aVoid){
		TryStatement tmp = new TryStatement(scope);
		JCStmtVisitor visitor = new JCStmtVisitor(scope);
		that.getBlock().accept(visitor, null);
		tmp.setBody(visitor.getStatement());
		stmt = tmp;
		return super.visitTry(that, aVoid);
    }

	@Override	
    public Void visitSynchronized(SynchronizedTree that, Void aVoid){
		visitBlock(that.getBlock(), null);
		return super.visitSynchronized(that, aVoid);
    }
	
	@Override
    public Void visitSwitch(SwitchTree that, Void aVoid){
        SwitchStatement tmp = new SwitchStatement(scope);
        tmp.setSelector(stepIn(that.getExpression(), scope));
        for(CaseTree c: that.getCases()){
            SwitchStatement.Elem elem;
            if(c.getExpression() != null){
                elem = tmp.newElement(stepIn(c.getExpression(), scope));
            }else{
                elem = tmp.getDefaultElement();
            }
            for(StatementTree s: c.getStatements()){
                elem.addStatement(stepIn(s, scope));
            }
        }
        stmt = tmp;
		return super.visitSwitch(that, aVoid);
    }
	
	@Override
    public Void visitVariable(VariableTree that, Void aVoid) {
		String name = that.getName().toString();
		Type type = TypeBuilder.genType(that.getType());
		Expr init;
		if(that.getInitializer() != null){
			JCExprVisitor visitor = new JCExprVisitor(scope);
			that.getInitializer().accept(visitor, null);
			init = visitor.getExpr();
		}else{
			init = null;
		}
		VariableDecl tmp = new VariableDecl(scope, name, type, init);
		if(JCFrontendUtils.isGlobalConstant(that.getModifiers())){
			tmp.setGlobalConstant(true);
		}
		if(JCFrontendUtils.isPrivate(that.getModifiers()) == false && scope instanceof Module){
			tmp.setPublic(true);
		}
		if(JCFrontendUtils.isVolatile(that.getModifiers())){
			tmp.setVolatile(true);
		}
		if(JCFrontendUtils.isAnnotatedBy(that.getModifiers().getAnnotations(), "Debug")){
			tmp.setDebug(true);
		}
		scope.addVariableDecl(tmp);
		stmt = tmp;
		return super.visitVariable(that, aVoid);
    }
	
	@Override
    public Void visitOther(Tree t, Void aVoid){
		SynthesijerUtils.error("[JCStmtVisitor] The following is unexpected in this context.");
		SynthesijerUtils.dump(t);
		return super.visitOther(t, aVoid);
    }

}
