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
		for(HDLExpr expr: args){
			if(expr == null) throw new RuntimeException("An argument of HDLCombinationExpr is null.");
		}
		//System.out.println(this);
		HDLType type = decideExprType(op, this.args);
		result = m.newSignal(String.format("tmp_%04d", uid), type, HDLSignal.ResourceKind.WIRE);
		//System.out.println(result);
	}
	
	public HDLType getType(){
		return result.getType();
	}
	
	public HDLOp getOp(){
		return op;
	}
	
	private HDLType getPriorType(HDLType t1, HDLType t2){
		//System.out.println(t1);
		//System.out.println(t2);
		HDLType t = null;
		if(t1.getKind().hasWdith() && t1.getKind().isPrimitive() && t2.getKind().hasWdith() && t2.getKind().isPrimitive()){
			boolean signFlag = false;
			if(t1.isSigned() || t2.isSigned()) signFlag = true;
			HDLType tmp = ((HDLPrimitiveType)t1).getWidth() > ((HDLPrimitiveType)t2).getWidth() ? t1 : t2;
			if(signFlag){
				t = HDLPrimitiveType.genSignedType(((HDLPrimitiveType)tmp).getWidth());
			}else{
				t = HDLPrimitiveType.genVectorType(((HDLPrimitiveType)tmp).getWidth());
			}
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
	
	private HDLType getConcatType(HDLPrimitiveType t0, HDLPrimitiveType t1){
		return HDLPrimitiveType.genVectorType(t0.getWidth() + t1.getWidth());
	}
	
	private HDLType getDropHeadType(HDLPrimitiveType t, HDLValue v){
		if(t.isVector()){
			return HDLPrimitiveType.genVectorType(t.getWidth()-Integer.parseInt(v.getValue()));
		}else{
			return HDLPrimitiveType.genSignedType(t.getWidth()-Integer.parseInt(v.getValue()));
		}
	}
	
	private HDLType getPaddingHeadType(HDLPrimitiveType t, HDLValue v){
		if(t.isSigned()){
			return HDLPrimitiveType.genSignedType(t.getWidth()+Integer.parseInt(v.getValue()));
		}else{
			return HDLPrimitiveType.genVectorType(t.getWidth()+Integer.parseInt(v.getValue()));
		}
	}
	
	private HDLType decideExprType(HDLOp op, HDLExpr[] args){
		if(op.isInfix()){
			return getPriorType(args[0].getType(), args[1].getType());
		}else if(op.isCompare()){
			return HDLPrimitiveType.genBitType();
		}else{
			switch(op){
			case NOT:
				return args[0].getType();
			case REF:
				return HDLPrimitiveType.genBitType();
			case IF:
				return getPriorType(args[1].getType(), args[2].getType());
			case CONCAT:
				return getConcatType((HDLPrimitiveType)args[0].getType(), (HDLPrimitiveType)args[1].getType());
			case DROPHEAD:
				return getDropHeadType((HDLPrimitiveType)args[0].getType(), (HDLValue)args[1]);
			case PADDINGHEAD:
			case PADDINGHEAD_ZERO:
				return getPaddingHeadType((HDLPrimitiveType)args[0].getType(), (HDLValue)args[1]);
			case ARITH_RSHIFT:
			case LOGIC_RSHIFT:
			case LSHIFT:
				return args[0].getType();
			case ID:
				return args[0].getType();
			default:
				return HDLPrimitiveType.genUnknowType();
			}
			
		}
	}

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLExpr(this);
	}
	
	public String toString(){
		return "HDLCombination::(" + op + " " + getArgsString(args) + ")";
	}
	
	// TODO experimental code
	private String convType(HDLExpr expr){
		if(expr instanceof HDLPreDefinedConstant) return expr.getVHDL();
		if(expr instanceof HDLValue) return expr.getVHDL();
		if(expr.getType().isVector()){
			return String.format("signed(%s)", expr.getVHDL());
		}else{
			return expr.getVHDL();
		}
	}

	@Override
	public String getVHDL() {
		if(op.isInfix()){
			return String.format("%s %s %s", convType(args[0].getResultExpr()), op.getVHDL(), convType(args[1].getResultExpr()));
		}else if(op.isCompare()){
			if(args[0] instanceof HDLValue && args[0].getType().isBit()){
				return String.format("%s and %s", args[0].getResultExpr().getVHDL(), args[1].getResultExpr().getVHDL());
			}else{
				return String.format("'1' when %s %s %s else '0'", convType(args[0].getResultExpr()), op.getVHDL(), convType(args[1].getResultExpr()));
			}
		}else{
			switch(op){
			case NOT:
				return String.format("%s %s", op.getVHDL(), args[0].getResultExpr().getVHDL());
			case REF:
				return String.format("%s(%s)", args[0].getResultExpr().getVHDL(), args[1].getResultExpr().getVHDL());
			case IF:
				return String.format("%s when %s = '1' else %s", args[1].getResultExpr().getVHDL(), args[0].getResultExpr().getVHDL(), args[2].getResultExpr().getVHDL());
			case CONCAT:
				return String.format("%s & %s", args[0].getResultExpr().getVHDL(), args[1].getResultExpr().getVHDL());
			case DROPHEAD: {
				HDLPrimitiveType t = (HDLPrimitiveType)args[0].getResultExpr().getType();
				return String.format("%s(%d - %s - 1 downto 0)", args[0].getResultExpr().getVHDL(), t.getWidth(), args[1].getResultExpr().getVHDL());
			}
			case PADDINGHEAD:{
				HDLPrimitiveType t0 = (HDLPrimitiveType)args[0].getResultExpr().getType();
				HDLPrimitiveType t1 = (HDLPrimitiveType)decideExprType(op, args);
				return String.format("(%d-1 downto %d => %s(%d)) & %s", t1.getWidth(), t0.getWidth(), args[0].getResultExpr().getVHDL(), t0.getWidth()-1, args[0].getResultExpr().getVHDL());
			}
			case PADDINGHEAD_ZERO:{
				HDLPrimitiveType t0 = (HDLPrimitiveType)args[0].getResultExpr().getType();
				HDLPrimitiveType t1 = (HDLPrimitiveType)decideExprType(op, args);
				return String.format("(%d-1 downto %d => '0') & %s", t1.getWidth(), t0.getWidth(), args[0].getResultExpr().getVHDL(), t0.getWidth()-1, args[0].getResultExpr().getVHDL());
			}
			case ARITH_RSHIFT:{
				HDLPrimitiveType t0 = (HDLPrimitiveType)args[0].getResultExpr().getType();
				int shift = Integer.parseInt(((HDLValue)((HDLCombinationExpr)(args[1])).args[0]).getValue());
				if(shift > 1){
					return String.format("(%d-1 downto %d => %s(%d)) & %s(%d downto %d)",
							shift,
							0,
							args[0].getResultExpr().getVHDL(),
							t0.getWidth()-1,
							args[0].getResultExpr().getVHDL(),
							t0.getWidth()-1,
							shift
							);
				}else{
					return args[0].getResultExpr().getVHDL();
				}
			}
			case LOGIC_RSHIFT:{
				HDLPrimitiveType t0 = (HDLPrimitiveType)args[0].getResultExpr().getType();
				int shift = Integer.parseInt(((HDLValue)((HDLCombinationExpr)(args[1])).args[0]).getValue());
				if(shift > 1){
					return String.format("(%d-1 downto %d => '0') & %s(%d downto %d)",
							shift,
							0,
							args[0].getResultExpr().getVHDL(),
							t0.getWidth()-1,
							shift
							);
				}else{
					return args[0].getResultExpr().getVHDL();
				}
			}
			case LSHIFT:{
				HDLPrimitiveType t0 = (HDLPrimitiveType)args[0].getResultExpr().getType();
				int shift = Integer.parseInt(((HDLValue)((HDLCombinationExpr)(args[1])).args[0]).getValue());
				if(shift > 1){
					return String.format("%s(%d downto %d) & (%d-1 downto %d => '0')",
							args[0].getResultExpr().getVHDL(),
							t0.getWidth()-shift-1,
							0,
							shift,
							0
							);
				}else{
					return args[0].getResultExpr().getVHDL();
				}
			}
			case ID:
				return args[0].getResultExpr().getVHDL();
			default:
				return "(" + op + " " + getArgsString(args) + ")"; 
			}
		}
	}

	private String getPaddingBitInVerilog(String key, int idx, int len){
		String s = "";
		for(int i = 0; i < len; i++){
			s += key + "["+(idx-1) + "], ";
		}
		return s;
	}

	@Override
	public String getVerilogHDL() {
		if(op.isInfix()){
			return String.format("%s %s %s", args[0].getResultExpr().getVerilogHDL(), op.getVerilogHDL(), args[1].getResultExpr().getVerilogHDL());
		}else if(op.isCompare()){
			return String.format("%s %s %s ? 1'b1 : 1'b0", args[0].getResultExpr().getVerilogHDL(), op.getVerilogHDL(), args[1].getResultExpr().getVerilogHDL());
		}else{
			switch(op){
			case NOT:
				return String.format("%s%s", op.getVerilogHDL(), args[0].getResultExpr().getVHDL());
			case REF:
				return String.format("%s[%s]", args[0].getResultExpr().getVerilogHDL(), args[1].getResultExpr().getVerilogHDL());
			case IF:
				return String.format("%s == 1'b1 ? %s : %s", args[0].getResultExpr().getVerilogHDL(), args[1].getResultExpr().getVerilogHDL(), args[2].getResultExpr().getVerilogHDL());
			case CONCAT:
				return String.format("{%s, %s}", args[0].getResultExpr().getVHDL(), args[1].getResultExpr().getVHDL());
			case DROPHEAD:{
				HDLPrimitiveType t = (HDLPrimitiveType)args[0].getResultExpr().getType();
				return String.format("%s(%d - %s - 1 downto 0)", args[0].getResultExpr().getVerilogHDL(), t.getWidth(), args[1].getResultExpr().getVerilogHDL());
			}
			case PADDINGHEAD:{
				HDLPrimitiveType t0 = (HDLPrimitiveType)args[0].getResultExpr().getType();
				HDLPrimitiveType t1 = (HDLPrimitiveType)decideExprType(op, args);
				return String.format("{%s%s}", getPaddingBitInVerilog(args[0].getResultExpr().getVerilogHDL(), t0.getWidth(), t1.getWidth()-t0.getWidth()), args[0].getResultExpr().getVerilogHDL());
			}
			case PADDINGHEAD_ZERO:{
				HDLPrimitiveType t0 = (HDLPrimitiveType)args[0].getResultExpr().getType();
				HDLPrimitiveType t1 = (HDLPrimitiveType)decideExprType(op, args);
				return String.format("{%d'b0, %s}", t1.getWidth()-t0.getWidth(), args[0].getResultExpr().getVerilogHDL());
			}
			case ARITH_RSHIFT:{
				HDLPrimitiveType t0 = (HDLPrimitiveType)args[0].getResultExpr().getType();
				int shift = Integer.parseInt(((HDLValue)((HDLCombinationExpr)(args[1])).args[0]).getValue());
				if(shift > 1){
					String pad = getPaddingBitInVerilog(args[0].getResultExpr().getVerilogHDL(), t0.getWidth(), shift);
					return String.format("{%s%s[%d:%d]}",
							pad,
							args[0].getResultExpr().getVerilogHDL(),
							t0.getWidth()-1,
							shift
							);
				}else{
					return args[0].getResultExpr().getVerilogHDL();
				}
			}
			case LOGIC_RSHIFT:{
				HDLPrimitiveType t0 = (HDLPrimitiveType)args[0].getResultExpr().getType();
				int shift = Integer.parseInt(((HDLValue)((HDLCombinationExpr)(args[1])).args[0]).getValue());
				if(shift > 1){
					return String.format("{%d'b0,%s[%d:%d]}",
							shift,
							args[0].getResultExpr().getVerilogHDL(),
							t0.getWidth()-1,
							shift
							);
				}else{
					return args[0].getResultExpr().getVerilogHDL();
				}
			}
			case LSHIFT:{
				HDLPrimitiveType t0 = (HDLPrimitiveType)args[0].getResultExpr().getType();
				int shift = Integer.parseInt(((HDLValue)((HDLCombinationExpr)(args[1])).args[0]).getValue());
				if(shift > 1){
					return String.format("{%s[%d:%d],%d'b0}",
							args[0].getResultExpr().getVerilogHDL(),
							t0.getWidth()-shift-1,
							0,
							shift
							);
				}else{
					return args[0].getResultExpr().getVerilogHDL();
				}
			}
			case ID:
				return args[0].getResultExpr().getVerilogHDL();
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
		ArrayList<HDLSignal> list = new ArrayList<>();
		for(HDLExpr arg: args){
			getSrcSignals(list, arg);
		}
		return list.toArray(new HDLSignal[]{});
	}
	
}
