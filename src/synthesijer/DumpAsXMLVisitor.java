package synthesijer;

import java.io.PrintWriter;

import synthesijer.ast.Expr;
import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstTypeVisitor;
import synthesijer.ast.SynthesijerAstVisitor;
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
import synthesijer.ast.expr.SynthesijerExprVisitor;
import synthesijer.ast.expr.TypeCast;
import synthesijer.ast.expr.UnaryExpr;
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
import synthesijer.ast.statement.SynchronizedBlock;
import synthesijer.ast.statement.TryStatement;
import synthesijer.ast.statement.VariableDecl;
import synthesijer.ast.statement.WhileStatement;
import synthesijer.ast.type.ArrayRef;
import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.ComponentRef;
import synthesijer.ast.type.ComponentType;
import synthesijer.ast.type.MultipleType;
import synthesijer.ast.type.MySelfType;
import synthesijer.ast.type.PrimitiveTypeKind;

/**
 * A visitor class to dump module hierarchy of AST as XML.
 * 
 * @author miyo
 *
 */
public class DumpAsXMLVisitor implements SynthesijerAstVisitor, SynthesijerExprVisitor, SynthesijerAstTypeVisitor{
	
	private final PrintWriter dest;
	
	public DumpAsXMLVisitor(PrintWriter dest){
		this.dest = dest;
	}

	@Override
	public void visitModule(Module o){
		dest.printf("<module name=\"%s\">\n", o.getName());
		dest.println("<variables>");
		for(VariableDecl v: o.getVariableDecls()){ v.accept(this); }
		dest.println("</variables>");
		dest.println("<methods>");
		for(Method m: o.getMethods()){ m.accept(this); }
		dest.println("</methods>");
		dest.printf("</module>\n");
		
	}

	@Override
	public void visitMethod(Method o) {
		String options = "";
		options += " unsynthesizableFlag=\"%b\"";
		options += " autoFlag=\"%b\"";
		options += " synchronizedFlag=\"%b\"";
		options += " privateFlag=\"%b\"";
		options += " rawFlag=\"%b\"";
		options += " combinationFlag=\"%b\"";
		options += " parallelFlag=\"%b\"";
		options += " noWaitFlag=\"%b\"";
		options += " constructorFlag=\"%b\"";
		
		dest.printf("<method name=\"%s\"" + options + ">\n",
				SynthesijerUtils.escapeXML(o.getName()),
				o.isUnsynthesizable(),
				o.isAuto(),
				o.isSynchronized(),
				o.isPrivate(),
				o.isRaw(),
				o.isCombination(),
				o.isParallel(),
				o.isNoWait(),
				o.isConstructor());
		o.getType().accept(this);
		o.getBody().accept(this);
		dest.printf("</method>\n");
	}

	@Override
	public void visitArrayAccess(ArrayAccess o) {
		dest.printf("<expr kind=\"%s\">", "ArrayAccess");
		dest.printf("<indexed>");
		o.getIndexed().accept(this);
		dest.printf("</indexed>");
		dest.printf("<index>");
		o.getIndex().accept(this);
		dest.printf("</index>");
		dest.printf("</expr>", "ArrayAccess");
	}
	
	@Override
	public void visitAssignExpr(AssignExpr o) {
		dest.printf("<expr kind=\"%s\">", "AssignExpr");
		dest.printf("<lhs>");
		o.getLhs().accept(this);
		dest.printf("</lhs>");
		dest.printf("<rhs>");
		o.getRhs().accept(this);
		dest.printf("</rhs>");
		dest.printf("</expr>\n");
	}

	@Override
	public void visitAssignOp(AssignOp o) {
		dest.printf("<expr kind=\"%s\" op=\"%s\">", "AssignOp", SynthesijerUtils.escapeXML(o.getOp().toString()));
		dest.printf("<lhs>");
		o.getLhs().accept(this);
		dest.printf("</lhs>");
		dest.printf("<rhs>");
		o.getRhs().accept(this);
		dest.printf("</rhs>");
		dest.printf("</expr>", "AssignOp");
	}

	@Override
	public void visitBinaryExpr(BinaryExpr o) {
		dest.printf("<expr kind=\"%s\" op=\"%s\">", "BinaryExpr", o.getOp().toString());
		dest.printf("<lhs>\n");
		o.getLhs().accept(this);
		dest.printf("</lhs>\n");
		dest.printf("<rhs>\n");
		o.getRhs().accept(this);
		dest.printf("</rhs>\n");
		dest.printf("</expr>\n");
	}

	@Override
	public void visitFieldAccess(FieldAccess o) {
		dest.printf("<expr kind=\"%s\" ident=\"%s\">", "FieldAccess", o.getIdent().getSymbol());
		o.getSelected().accept(this);
		dest.printf("</expr>");
	}

	@Override
	public void visitIdent(Ident o) {
		dest.printf("<expr kind=\"%s\" value=\"%s\"/>", "Ident", o.getSymbol());
	}

	@Override
	public void visitLitral(Literal o) {
		dest.printf("<expr kind=\"%s\" ", "Literal");
		dest.printf("value=\"%s\" type=\"%s\"", o.getValueAsStr(), o.getType());
		dest.printf("/>");
	}

	@Override
	public void visitMethodInvocation(MethodInvocation o) {
		dest.printf("<expr kind=\"%s\">", "MethodInvocation");
		dest.printf("<method name=\"%s\">", o.getMethodName());
		Expr method = o.getMethod();
		if(method instanceof FieldAccess){
			method.accept(this);
		}
		dest.printf("</method>\n");
		dest.printf("<params>\n");
		for(Expr expr: o.getParameters()){
			expr.accept(this);
		}
		dest.printf("</params>\n");
		dest.printf("</expr>");
	}

	@Override
	public void visitNewArray(NewArray o) {
		dest.printf("<expr kind=\"%s\">", "NewArray");
		dest.printf("<dimension>");
		for(Expr expr: o.getDimExpr()){
			expr.accept(this);
		}
		dest.printf("</dimension>");
		dest.println("</expr>");
	}

	@Override
	public void visitNewClassExpr(NewClassExpr o) {
		dest.printf("<expr kind=\"%s\" class=\"%s\">", "NewClass", o.getClassName());
		dest.printf("<params>");
		for(Expr expr: o.getParameters()){
			expr.accept(this);
		}
		dest.printf("</params>");
		dest.printf("</expr>");
	}

	@Override
	public void visitParenExpr(ParenExpr o) {
		dest.printf("<expr kind=\"%s\">", "ParenExpr");
		o.getExpr().accept(this);
		dest.printf("</expr>");
	}

	@Override
	public void visitTypeCast(TypeCast o) {
		dest.printf("<expr kind=\"%s\">", "TypeCast");
		o.getExpr().accept(this);
		dest.printf("</expr>", "TypeCast");
	}

	@Override
	public void visitUnaryExpr(UnaryExpr o) {
		dest.printf("<expr kind=\"%s\" op=\"%s\">", "UnaryExpr", o.getOp());
		dest.printf("<arg>\n");
		o.getArg().accept(this);
		dest.printf("</arg>\n");
		dest.printf("</expr>\n");
	}
	
	@Override
	public void visitCondExpr(CondExpr o){
		dest.printf("<type kind=\"select\">\n");
		dest.printf("<cond>\n"); o.getCond().accept(this); dest.printf("</cond>\n"); 
		dest.printf("<true>\n"); o.getTruePart().accept(this); dest.printf("</true>\n"); 
		dest.printf("<false>\n"); o.getFalsePart().accept(this); dest.printf("</false>\n"); 
		dest.printf("</type>\n");
	}

	@Override
	public void visitBlockStatement(BlockStatement o) {
		dest.printf("<statement type=\"block\">\n");
		for(Statement s: o.getStatements()){
			s.accept(this);
		}
		dest.printf("</statement>\n");
	}

	@Override
	public void visitBreakStatement(BreakStatement o) {
		dest.printf("<statement type=\"break\"/>\n");
	}

	@Override
	public void visitContinueStatement(ContinueStatement o) {
		dest.printf("<statement type=\"continue\"/>\n");
	}

	@Override
	public void visitExprStatement(ExprStatement o) {
		dest.printf("<statement type=\"expr\">\n");
		o.getExpr().accept(this);
		dest.printf("</statement>\n");
	}

	@Override
	public void visitForStatement(ForStatement o) {
		dest.printf("<statement type=\"for\">\n");
		dest.printf("<init>\n");
		for (Statement s : o.getInitializations()){
			s.accept(this);
		}
		dest.printf("</init>\n");
		dest.printf("<condition>\n");
		o.getCondition().accept(this);
		dest.printf("</condition>");
		dest.printf("<update>\n");
		for (Statement s : o.getUpdates())
			s.accept(this);
		dest.printf("</update>");
		dest.printf("<body>\n");
		o.getBody().accept(this);
		dest.printf("</body>\n");
		dest.printf("</statement>\n");
	}

	@Override
	public void visitIfStatement(IfStatement o) {
		dest.printf("<statement type=\"if\">\n");
		dest.printf("<condition>\n");
		o.getCondition().accept(this);
		dest.printf("</condition>\n");
		dest.printf("<then>\n");
		o.getThenPart().accept(this);
		dest.printf("</then>\n");
		if(o.getElsePart() != null){
			dest.printf("<else>\n");
			o.getElsePart().accept(this);
			dest.printf("</else>\n");
		}
		dest.printf("</statement>\n");
	}

	@Override
	public void visitReturnStatement(ReturnStatement o) {
		dest.printf("<statement type=\"return\">\n");
		if(o.getExpr() != null) o.getExpr().accept(this);
		dest.printf("</statement>\n");
	}

	@Override
	public void visitSkipStatement(SkipStatement o) {
		dest.printf("<statement type=\"skip\"/>\n");
	}

	@Override
	public void visitSwitchStatement(SwitchStatement o) {
		dest.printf("<statement type=\"switch\">\n");
		dest.printf("<selector>\n");
		o.getSelector().accept(this);
		dest.printf("</selector>\n");
		dest.printf("<elements>\n");
		for(SwitchStatement.Elem elem: o.getElements()){
			elem.accept(this);
		}
		dest.printf("</elements>\n");
		dest.printf("</statement>\n");
	}
	
	public void visitSwitchCaseElement(SwitchStatement.Elem o){
		dest.printf("<element>\n");
		dest.printf("<pattern>\n");
		o.getPattern().accept(this);
		dest.printf("</pattern>\n");
		dest.printf("<statements>\n");
		for(Statement s: o.getStatements()) s.accept(this);
		dest.printf("</statements>\n");
		dest.printf("</element>\n");	
	}


	@Override
	public void visitSynchronizedBlock(SynchronizedBlock o) {
		dest.printf("<statement type=\"synchronized\">\n");
		visitBlockStatement(o);
		dest.printf("</statement>>\n");
	}

	@Override
	public void visitTryStatement(TryStatement o) {
		dest.printf("<statement type=\"try\">");
		o.getBody().accept(this);
		dest.printf("</statement>");
	}

	@Override
	public void visitVariableDecl(VariableDecl o) {
		dest.printf("<var name=\"%s\">\n", o.getVariable().getName());
		o.getVariable().getType().accept(this);
		if(o.getInitExpr() != null) o.getInitExpr().accept(this);
		dest.printf("</var>");
	}

	@Override
	public void visitWhileStatement(WhileStatement o) {
		dest.printf("<statement type=\"while\">\n");
		dest.printf("<condition>\n");
		o.getCondition().accept(this);
		dest.printf("</condition>\n");
		dest.printf("<body>\n");
		o.getBody().accept(this);
		dest.printf("</body>\n");
		dest.printf("</statement>\n");
	}

	@Override
	public void visitDoWhileStatement(DoWhileStatement o) {
		dest.printf("<statement type=\"do-while\">\n");
		dest.printf("<condition>\n");
		o.getCondition().accept(this);
		dest.printf("</condition>\n");
		dest.printf("<body>\n");
		o.getBody().accept(this);
		dest.printf("</body>\n");
		dest.printf("</statement>\n");
	}

	@Override
	public void visitArrayType(ArrayType o) {
		dest.printf("<type kind=\"array\">\n");
		o.getElemType().accept(this);
		dest.printf("</type>\n");
	}
	
	@Override
	public void visitArrayRef(ArrayRef o){
		dest.printf("<type kind=\"array_ref\">\n");
		o.getRefType().accept(this);
		dest.printf("</type>\n");
	}
	
	@Override
	public void visitComponentRef(ComponentRef o){
		dest.printf("<type kind=\"component_ref\">\n");
		o.getRefType().accept(this);
		dest.printf("</type>\n");
	}


	@Override
	public void visitComponentType(ComponentType o) {
		dest.printf("<type kind=\"component\" name=\"%s\"/>\n", o.getName());
	}

	@Override
	public void visitMySelfType(MySelfType o) {
		dest.printf("<type kind=\"myself\"/>\n");
	}

	@Override
	public void visitPrimitiveTypeKind(PrimitiveTypeKind o) {
		dest.printf("<type kind=\"primitive\" name=\"%s\"/>\n", o.toString());
	}

    @Override
	public void visitMultipleType(MultipleType o){
    	dest.printf("<type kind=\"multipe\" name\"%s\"/>\n", o.toString());
    }
}
