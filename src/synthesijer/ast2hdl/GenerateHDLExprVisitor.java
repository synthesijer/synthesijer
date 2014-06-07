package synthesijer.ast2hdl;

import synthesijer.ast.Expr;
import synthesijer.ast.Op;
import synthesijer.ast.Variable;
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
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLVariable;
import synthesijer.hdl.expr.HDLConstant;
import synthesijer.hdl.expr.HDLValue;

public class GenerateHDLExprVisitor implements SynthesijerExprVisitor{
		
	private final GenerateHDLModuleVisitor parent;
	private final HDLSequencer.SequencerState state;
	
	private HDLExpr result;
	
	public GenerateHDLExprVisitor(GenerateHDLModuleVisitor parent, HDLSequencer.SequencerState state){
		this.parent = parent;
		this.state = state;
	}
	
	public HDLExpr getResult(){
		return result;
	}
	
	private HDLExpr stepIn(Expr expr){
		GenerateHDLExprVisitor visitor = new GenerateHDLExprVisitor(parent, state);
		expr.accept(visitor);
		return visitor.getResult();
	}

	@Override
	public void visitArrayAccess(ArrayAccess o) {
		if(o.getIndexed() instanceof Ident){
			ArrayAccess aa = (ArrayAccess)o;
			Ident id = (Ident)aa.getIndexed();
			HDLVariable var = parent.getHDLVariable(o.getScope().search(id.getSymbol()));
			HDLInstance inst = (HDLInstance)var;
			// address
			HDLSignal addr = inst.getSignalForPort("address"); // see synthsijer.lib.BlockRAM
			addr.setAssign(state, stepIn(aa.getIndex()));
			// write-enable
			HDLSignal we = inst.getSignalForPort("we"); // see synthsijer.lib.BlockRAM
			we.setAssign(state, HDLConstant.LOW);
			// data
			result = inst.getSignalForPort("dout"); // see synthsijer.lib.BlockRAM
			state.setConstantDelay(2);
		}else{
			throw new RuntimeException(String.format("%s(%s) cannot convert to HDL.", o.getIndexed(), o.getIndexed().getClass()));
		}
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
	public void visitAssignExpr(AssignExpr o) {
		HDLExpr expr = stepIn(o.getRhs());
		Expr lhs = o.getLhs();
		if(lhs instanceof Ident){
			Ident id = (Ident)lhs;
			HDLVariable sig = parent.getHDLVariable(o.getScope().search(id.getSymbol()));
			sig.setAssign(state, expr);
		}else if(lhs instanceof ArrayAccess){
			ArrayAccess aa = (ArrayAccess)lhs;
			Ident id = (Ident)aa.getIndexed();
			HDLVariable var = parent.getHDLVariable(o.getScope().search(id.getSymbol()));
			HDLInstance inst = (HDLInstance)var;
			// address
			HDLSignal addr = inst.getSignalForPort("address"); // see synthsijer.lib.BlockRAM
			addr.setAssign(state, stepIn(aa.getIndex()));
			// write-enable
			HDLSignal we = inst.getSignalForPort("we"); // see synthsijer.lib.BlockRAM
			we.setAssign(state, HDLConstant.HIGH);
			we.setDefaultValue(HDLConstant.LOW);
			// data
			HDLSignal din = inst.getSignalForPort("din"); // see synthsijer.lib.BlockRAM
			din.setAssign(state, expr);
		}else{
			throw new RuntimeException("unsupported yet.");
		}
		result = expr;
	}
	
	@Override
	public void visitAssignOp(AssignOp o) {
		HDLExpr expr = parent.module.newExpr(convOp(o.getOp()), stepIn(o.getLhs()), stepIn(o.getRhs()));
		Ident id = (Ident)o.getLhs();
		HDLVariable sig = parent.getHDLVariable(o.getScope().search(id.getSymbol()));
		sig.setAssign(state, expr);
		result = expr;
	}
		
	@Override
	public void visitBinaryExpr(BinaryExpr o) {
		HDLExpr lhs = stepIn(o.getLhs());
		HDLExpr rhs = stepIn(o.getRhs());
		result = parent.module.newExpr(convOp(o.getOp()), lhs, rhs);
		//HDLSignal sig = parent.module.newSignal("binaryexpr_result_" + this.hashCode(), HDLPrimitiveType.genVectorType(32));
		//sig.setAssign(null, parent.module.newExpr(convOp(o.getOp()), lhs, rhs));
		//result = sig;
	}
	
	@Override
	public void visitFieldAccess(FieldAccess o) {
		Ident id = (Ident)o.getSelected();
		HDLVariable var = parent.getHDLVariable(o.getScope().search(id.getSymbol()));
		HDLInstance inst = (HDLInstance)var;
		HDLSignal sig = inst.getSignalForPort(o.getIdent().getSymbol());
		result = sig.getResultExpr();
	}
	
	@Override
	public void visitIdent(Ident o) {
		Variable v = o.getScope().search(o.getSymbol());
		result = parent.getHDLVariable(v);
	}
	
	private HDLPrimitiveType convToHDLType(Literal.LITERAL_KIND kind){
		switch(kind){
		case BOOLEAN: return HDLPrimitiveType.genBitType();
		case BYTE:    return HDLPrimitiveType.genSignedType(8);
		case CHAR:    return HDLPrimitiveType.genVectorType(16);
		case SHORT:   return HDLPrimitiveType.genSignedType(16);
		case INT:     return HDLPrimitiveType.genSignedType(32);
		case LONG:    return HDLPrimitiveType.genSignedType(64);
		case DOUBLE:  return HDLPrimitiveType.genVectorType(64);
		case FLOAT:   return HDLPrimitiveType.genVectorType(32);
		case STRING:  return HDLPrimitiveType.genStringType();
		default: return HDLPrimitiveType.genUnknowType();
		}
	}
	
	@Override
	public void visitLitral(Literal o) {
		result = new HDLValue(o.getValueAsStr(), convToHDLType(o.getKind()));
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
		result = stepIn(o.getExpr());
	}
	
	@Override
	public void visitTypeCast(TypeCast o) {
		result = stepIn(o.getExpr());
	}
	
	@Override
	public void visitUnaryExpr(UnaryExpr o) {
		result = parent.module.newSignal("binaryexpr_result_" + this.hashCode(), HDLPrimitiveType.genVectorType(32));
	}
	
}
