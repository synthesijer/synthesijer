package synthesijer.ast2hdl;

import synthesijer.ast.Op;
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
import synthesijer.ast.expr.SynthesijerExprVisitor;
import synthesijer.ast.expr.TypeCast;
import synthesijer.ast.expr.UnaryExpr;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.expr.HDLValue;

public class GenerateHDLExprVisitor implements SynthesijerExprVisitor{
		
	private final GenerateHDLModuleVisitor parent;
	
	private HDLExpr result;
	
	public GenerateHDLExprVisitor(GenerateHDLModuleVisitor parent){
		this.parent = parent;
	}
	
	public HDLExpr getResult(){
		return result;
	}
	
	@Override
	public void visitArrayAccess(ArrayAccess o) {
		if(o.getIndexed() instanceof Ident){
			String rdata = ((Ident)o.getIndexed()).getSymbol() + "_rdata";
			result = parent.module.newSignal(rdata, HDLPrimitiveType.genVectorType(32));
		}else{
			throw new RuntimeException(String.format("%s(%s) cannot convert to HDL.", o.getIndexed(), o.getIndexed().getClass()));
		}
	}
	
	@Override
	public void visitAssignExpr(AssignExpr o) {
		GenerateHDLExprVisitor v = new GenerateHDLExprVisitor(parent);
		o.getLhs().accept(v);
		result = v.getResult();
	}
	
	private HDLOp convOp(Op op){
		switch(op){
		case PLUS : return HDLOp.ADD;
		case MINUS : return HDLOp.SUB;
		case AND : return HDLOp.AND;
		case LAND : return HDLOp.AND;		
		case LOR : return HDLOp.OR;
		case OR : return HDLOp.OR;
		case XOR : return HDLOp.XOR;
		case COMPEQ : return HDLOp.EQ;
		case NEQ : return HDLOp.NEQ;
		case GT : return HDLOp.GT;
		case GEQ : return HDLOp.GEQ;
		case LT : return HDLOp.LT;
		case LEQ : return HDLOp.LEQ;
		case INC : return HDLOp.ADD;
		case DEC : return HDLOp.SUB;
		default:
			return HDLOp.UNDEFINED;
		}
	}
	
	@Override
	public void visitAssignOp(AssignOp o) {
		GenerateHDLExprVisitor lhsVisitor = new GenerateHDLExprVisitor(parent);
		GenerateHDLExprVisitor rhsVisitor = new GenerateHDLExprVisitor(parent);
		HDLOp op = convOp(o.getOp());
		o.getLhs().accept(lhsVisitor);
		o.getRhs().accept(rhsVisitor);
		result = parent.module.newExpr(op, lhsVisitor.getResult(), rhsVisitor.getResult());
	}
	
	@Override
	public void visitBinaryExpr(BinaryExpr o) {
		result = parent.module.newSignal("binaryexpr_result_" + this.hashCode(), HDLPrimitiveType.genVectorType(32));
	}
	
	@Override
	public void visitFieldAccess(FieldAccess o) {
		result = parent.module.newSignal(String.format("%s_%s", o.getSelected(), o.getIdent()), HDLPrimitiveType.genVectorType(32));
	}
	
	@Override
	public void visitIdent(Ident o) {
		result = parent.module.newSignal(o.getSymbol(), HDLPrimitiveType.genVectorType(32));
	}
	
	@Override
	public void visitLitral(Literal o) {
		HDLPrimitiveType t = HDLPrimitiveType.genUnkonwType();
		result = new HDLValue(o.getValueAsStr(), t);
	}
	
	@Override
	public void visitMethodInvocation(MethodInvocation o) {
		result = parent.module.newSignal(o.getMethodName() + "_return_value", HDLPrimitiveType.genVectorType(32));
	}
	
	@Override
	public void visitNewArray(NewArray o) {
		// TODO Auto-generated method stub		
	}
	
	@Override
	public void visitNewClassExpr(NewClassExpr o) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void visitParenExpr(ParenExpr o) {
		GenerateHDLExprVisitor v = new GenerateHDLExprVisitor(parent);
		o.getExpr().accept(v);
		result = v.getResult();
	}
	
	@Override
	public void visitTypeCast(TypeCast o) {
		GenerateHDLExprVisitor v = new GenerateHDLExprVisitor(parent);
		o.getExpr().accept(v);
		result = v.getResult();
	}
	
	@Override
	public void visitUnaryExpr(UnaryExpr o) {
		result = parent.module.newSignal("binaryexpr_result_" + this.hashCode(), HDLPrimitiveType.genVectorType(32));
	}
	
}
