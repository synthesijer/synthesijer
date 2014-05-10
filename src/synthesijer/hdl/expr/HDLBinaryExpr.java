package synthesijer.hdl.expr;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
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
		result = m.newSignal(String.format("tmp_%04d", uid), HDLType.genBitType(), HDLSignal.ResourceKind.WIRE); 
	}

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLExpr(this);
	}

	@Override
	public String getVHDL() {
		return "(" + op + " " + arg1 + " " + arg2 + ")"; 
	}

	@Override
	public String getVerilogHDL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HDLExpr getResultExpr() {
		return result;
	}

}
