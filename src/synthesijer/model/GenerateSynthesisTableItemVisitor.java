package synthesijer.model;

import java.io.PrintStream;

import synthesijer.ast.Expr;
import synthesijer.ast.Op;
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

public class GenerateSynthesisTableItemVisitor implements SynthesijerExprVisitor{

	private final SynthesisTableItem.Entry entry;
	private final PrintStream out;
	
	public GenerateSynthesisTableItemVisitor(PrintStream out, SynthesisTableItem.Entry entry){
		this.entry = entry;
		this.out = out;
	}

	@Override
	public void visitArrayAccess(ArrayAccess o) {
		out.println(o);
		o.getIndexed().accept(this);
		o.getIndex().accept(this);
	}

	@Override
	public void visitAssignExpr(AssignExpr o) {
		out.println(o);
		if(o.getLhs() instanceof Ident){
			Ident id = (Ident)(o.getLhs());
			entry.dest = id.getScope().search(id.getSymbol());
		}else{
			o.getLhs().accept(this);
		}
		o.getRhs().accept(this);
		entry.op = Op.ASSIGN;
	}

	@Override
	public void visitAssignOp(AssignOp o) {
		out.println(o);
		o.getLhs().accept(this);
		o.getRhs().accept(this);
		entry.op = Op.ASSIGN;
	}

	@Override
	public void visitBinaryExpr(BinaryExpr o) {
		out.println(o);
		o.getLhs().accept(this);
		o.getRhs().accept(this);
		entry.op = o.getOp();
	}

	@Override
	public void visitFieldAccess(FieldAccess o) {
		out.println(o);
		o.getSelected().accept(this);
		o.getIdent().accept(this);
	}

	@Override
	public void visitIdent(Ident o) {
		out.println(o);
		entry.addSrc(o);
	}

	@Override
	public void visitLitral(Literal o) {
		out.println(o);
		entry.addSrc(o);
	}

	@Override
	public void visitMethodInvocation(MethodInvocation o) {
		out.println(o);
		o.getMethod().accept(this);
		for(Expr param:o.getParameters()){
			param.accept(this);
		}
		entry.op = Op.CALL;
	}

	@Override
	public void visitNewArray(NewArray o) {
		out.println(o);
	}

	@Override
	public void visitNewClassExpr(NewClassExpr o) {
		out.println(o);
	}

	@Override
	public void visitParenExpr(ParenExpr o) {
		out.println(o);
		o.getExpr().accept(this);
	}

	@Override
	public void visitTypeCast(TypeCast o) {
		out.println(o);
		o.getExpr().accept(this);
	}

	@Override
	public void visitUnaryExpr(UnaryExpr o) {
		// TODO Auto-generated method stub		
		o.getArg().accept(this);
	}

	@Override
	public void visitCondExpr(CondExpr o) {
		out.println(o);
		o.getCond().accept(this);
		o.getTruePart().accept(this);
		o.getFalsePart().accept(this);
		entry.op = Op.SELECT;
	}
	
}
