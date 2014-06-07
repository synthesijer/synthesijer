package synthesijer.hdl.expr;

import java.util.ArrayList;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLTreeVisitor;
import synthesijer.hdl.HDLType;

public class HDLCombinationExpr implements HDLExpr{
	
	private final int uid;
	private final HDLOp op;
	private final HDLExpr[] args;
	
	private final HDLSignal result;
	
	public HDLCombinationExpr(HDLModule m, int uid, HDLOp op, HDLExpr... args){
		this.uid = uid;
		this.op = op;
		this.args = args;
		HDLType type = decideExprType(op, this.args);
		result = m.newSignal(String.format("tmp_%04d", uid), type, HDLSignal.ResourceKind.WIRE);
	}
	
	public HDLType getType(){
		return result.getType();
	}
	
	public HDLOp getOp(){
		return op;
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
		}else if(t1.getKind() == HDLType.KIND.BIT && t2.getKind() == HDLType.KIND.BIT){
			return t1;
		}else{
		}
		return t;
	}
	
	private String getArgsString(HDLExpr[] args){
		String s = "";
		for(HDLExpr a: args){ s += a.toString() + " "; }
		return s;
	}
	
	private HDLType decideExprType(HDLOp op, HDLExpr[] args){
		if(op.isInfix()){
			return getPriorType(args[0].getType(), args[1].getType());
		}else if(op.isCompare()){
			return HDLPrimitiveType.genBitType();
		}else{
			switch(op){
			case REF:
				return HDLPrimitiveType.genBitType();
			case IF:
				return getPriorType(args[1].getType(), args[2].getType());
			default:
				return HDLPrimitiveType.genUnknowType();
			}
			
		}
	}

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLExpr(this);
	}

	@Override
	public String getVHDL() {
		if(op.isInfix()){
			return String.format("%s %s %s", args[0].getResultExpr().getVHDL(), op.getVHDL(), args[1].getResultExpr().getVHDL());
		}else if(op.isCompare()){
			return String.format("'1' when %s %s %s else '0'", args[0].getResultExpr().getVHDL(), op.getVHDL(), args[1].getResultExpr().getVHDL());
		}else{
			switch(op){
			case REF:
				return String.format("%s(%s)", args[0].getResultExpr().getVHDL(), args[1].getResultExpr().getVHDL());
			case IF:
				return String.format("%s when %s = '1' else %s", args[1].getResultExpr().getVHDL(), args[0].getResultExpr().getVHDL(), args[2].getResultExpr().getVHDL());
			default:
				return "(" + op + " " + getArgsString(args) + ")"; 
			}
		}
	}

	@Override
	public String getVerilogHDL() {
		if(op.isInfix()){
			return String.format("%s %s %s", args[0].getResultExpr().getVerilogHDL(), op.getVerilogHDL(), args[1].getResultExpr().getVerilogHDL());
		}else if(op.isCompare()){
			return String.format("%s %s %s ? 1'b1 : 1'b0", args[0].getResultExpr().getVerilogHDL(), op.getVerilogHDL(), args[1].getResultExpr().getVerilogHDL());
		}else{
			switch(op){
			case REF:
				return String.format("%s[%s]", args[0].getResultExpr().getVerilogHDL(), args[1].getResultExpr().getVerilogHDL());
			case IF:
				return String.format("%s == 1'b1 ? %s : %s", args[0].getResultExpr().getVerilogHDL(), args[1].getResultExpr().getVerilogHDL(), args[2].getResultExpr().getVerilogHDL());
			default:
				return "(" + op + " " + getArgsString(args) + ")";
			}
		}
	}

	@Override
	public HDLExpr getResultExpr() {
		return result;
	}
	
	private void getSrcSignals(ArrayList<HDLSignal> list, HDLExpr arg){
		HDLSignal[] src = arg.getSrcSignals();
		if(src != null){
			for(HDLSignal s: src){ list.add(s); }
		}
		if(arg.getResultExpr() instanceof HDLSignal){
			list.add((HDLSignal)arg.getResultExpr());
		}
	}

	@Override
	public HDLSignal[] getSrcSignals() {
		ArrayList<HDLSignal> list = new ArrayList<HDLSignal>();
		for(HDLExpr arg: args){
			getSrcSignals(list, arg);
		}
		return list.toArray(new HDLSignal[]{});
	}
	
}
