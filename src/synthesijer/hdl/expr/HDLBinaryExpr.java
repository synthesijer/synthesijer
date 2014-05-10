package synthesijer.hdl.expr;

import synthesijer.SynthesijerUtils;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLTreeVisitor;
import synthesijer.hdl.HDLType;

public class HDLBinaryExpr implements HDLExpr{
	
	private final int uid;
	private final HDLOp op;
	private final HDLExpr arg1;
	private final HDLExpr arg2;
	
	private final HDLSignal result;
	
	public HDLBinaryExpr(HDLModule m, int uid, HDLOp op, HDLExpr arg1, HDLExpr arg2){
		this.uid = uid;
		this.op = op;
		this.arg1 = arg1;
		this.arg2 = arg2;
		HDLType type = decideExprType(op, arg1, arg2); 
		result = m.newSignal(String.format("tmp_%04d", uid), type, HDLSignal.ResourceKind.WIRE); 
	}
	
	public HDLType getType(){
		return result.getType();
	}
	
	private HDLType getPriorType(HDLType t1, HDLType t2){
		HDLType t = null;
		if(t1.getKind().hasWdith() && t1.getKind().isPrimitive() &&
           t2.getKind().hasWdith() && t2.getKind().isPrimitive()){
			t = ((HDLPrimitiveType)t1).getWidth() > ((HDLPrimitiveType)t2).getWidth() ? t1 : t2; 
		}else if(t1.getKind().hasWdith() && t1.getKind().isPrimitive()){
			t = t1;
		}else if(t1.getKind().hasWdith() && t1.getKind().isPrimitive()){
			t = t2;
		}
		return t;
	}
	
	
	private HDLType decideExprType(HDLOp op, HDLExpr arg1, HDLExpr arg2){
		HDLType t = getPriorType(arg1.getType(), arg2.getType());
		if(t == null){
			SynthesijerUtils.error("cannot decide type from " + arg1 + " and " + arg2);
		}
		switch(op){
		case ADD:
			return t;
		case REF:
			return HDLPrimitiveType.genBitType();
		default:
			return HDLPrimitiveType.genUnkonwType();
		}
	}

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLExpr(this);
	}

	@Override
	public String getVHDL() {
		switch(op){
		case ADD:
			return String.format("%s %s %s", arg1.getVHDL(), op.getVHDL(), arg2.getVHDL());
		case REF:
			return String.format("%s(%s)", arg1.getVHDL(), arg2.getVHDL());
		default:
			return "(" + op + " " + arg1 + " " + arg2 + ")"; 
		}
	}

	@Override
	public String getVerilogHDL() {
		switch(op){
		case ADD:
			return String.format("%s %s %s", arg1.getVHDL(), op.getVHDL(), arg2.getVHDL());
		case REF:
			return String.format("%s[%s]", arg1.getVHDL(), arg2.getVHDL());
		default:
			return "(" + op + " " + arg1 + " " + arg2 + ")"; 
		}
	}

	@Override
	public HDLExpr getResultExpr() {
		return result;
	}

}
