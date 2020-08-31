package synthesijer.scheduler;

import synthesijer.ast.Type;
import synthesijer.ast.type.PrimitiveTypeKind;

public enum Op {

	METHOD_ENTRY(true),
	METHOD_EXIT,
	ASSIGN,
	NOP,
	ADD,
	SUB,
	MUL32(1),
	MUL64(1),
	DIV32(1),
	DIV64(1),
	MOD32(1),
	MOD64(1),
	LT(PrimitiveTypeKind.BOOLEAN),
	LEQ(PrimitiveTypeKind.BOOLEAN),
	GT(PrimitiveTypeKind.BOOLEAN),
	GEQ(PrimitiveTypeKind.BOOLEAN),
	COMPEQ(PrimitiveTypeKind.BOOLEAN),
	NEQ(PrimitiveTypeKind.BOOLEAN),
	SIMPLE_LSHIFT32(PrimitiveTypeKind.INT),
	SIMPLE_LOGIC_RSHIFT32(PrimitiveTypeKind.INT),
	SIMPLE_ARITH_RSHIFT32(PrimitiveTypeKind.INT),
	SIMPLE_LSHIFT64(PrimitiveTypeKind.LONG),
	SIMPLE_LOGIC_RSHIFT64(PrimitiveTypeKind.LONG),
	SIMPLE_ARITH_RSHIFT64(PrimitiveTypeKind.LONG),
	LSHIFT32(false, 1, PrimitiveTypeKind.INT),
	LOGIC_RSHIFT32(false, 1, PrimitiveTypeKind.INT),
	ARITH_RSHIFT32(false, 1, PrimitiveTypeKind.INT),
	LSHIFT64(false, 1, PrimitiveTypeKind.LONG),
	LOGIC_RSHIFT64(false, 1, PrimitiveTypeKind.LONG),
	ARITH_RSHIFT64(false, 1, PrimitiveTypeKind.LONG),
	JP(true),
	JT(true),
	RETURN(true),
	MULTI_RETURN(true),
	SELECT(true), // switch selector
	AND,
	NOT,
	MSB_FLAP,
	LAND,
	LOR,
	OR,
	XOR,
	LNOT,
	ARRAY_ACCESS(1),
	ARRAY_ACCESS_WAIT,
	ARRAY_ACCESS0,
	ARRAY_INDEX,
	FIFO_WRITE,
	CALL(true),
	EXT_CALL(true),
	FIELD_ACCESS,
	BREAK,
	CONTINUE,
	CAST,
	COND,
	FADD32(1),
	FSUB32(1),
	FMUL32(1),
	FDIV32(1),
	FADD64(1),
	FSUB64(1),
	FMUL64(1),
	FDIV64(1),
	CONV_F2I(1),
	CONV_I2F(1),
	CONV_D2L(1),
	CONV_L2D(1),
	CONV_F2D(1),
	CONV_D2F(1),
	FLT32(false, 1, PrimitiveTypeKind.BOOLEAN),
	FLEQ32(false, 1, PrimitiveTypeKind.BOOLEAN),
	FGT32(false, 1, PrimitiveTypeKind.BOOLEAN),
	FGEQ32(false, 1, PrimitiveTypeKind.BOOLEAN),
	FCOMPEQ32(false, 1, PrimitiveTypeKind.BOOLEAN),
	FNEQ32(false, 1, PrimitiveTypeKind.BOOLEAN),
	FLT64(false, 1, PrimitiveTypeKind.BOOLEAN),
	FLEQ64(false, 1, PrimitiveTypeKind.BOOLEAN),
	FGT64(false, 1, PrimitiveTypeKind.BOOLEAN),
	FGEQ64(false, 1, PrimitiveTypeKind.BOOLEAN),
	FCOMPEQ64(false, 1, PrimitiveTypeKind.BOOLEAN),
	FNEQ64(false, 1, PrimitiveTypeKind.BOOLEAN),
	PHI,
	UNDEFINED;

	public final boolean isBranch;
	public final int latency;
	public final Type type;

	/**
	 *
	 * @param flag branch instruction or not
	 * @param latency fixed clock latency
	 * @param type result type  
	 */
	private Op(boolean flag, int latency, Type type){
		this.isBranch = flag;
		this.latency = latency;
		this.type = type;
	}

	/**
	 *
	 * @param latency fixed clock latency  
	 */
	private Op(int latency){
		this(false, latency, PrimitiveTypeKind.UNDEFINED);
	}

	/**
	 *
	 * @param flag branch instruction or not  
	 */
	private Op(boolean flag){
		this(flag, 0, PrimitiveTypeKind.UNDEFINED);
	}

	/**
	 *
	 * @param type return type
	 */
	private Op(Type type){
		this(false, 0, type);
	}

	/**
	 * Default constructor: not branch, latency=0, type=UNDEFINED
	 */
	private Op(){
		this(false, 0, PrimitiveTypeKind.UNDEFINED);
	}

	private static boolean isFloat(Operand operand){
		Type t = operand.getType();
		if(t instanceof PrimitiveTypeKind == false) return false;
		return t == PrimitiveTypeKind.FLOAT;
	}

	private static boolean isDouble(Operand operand){
		Type t = operand.getType();
		if(t instanceof PrimitiveTypeKind == false) return false;
		return t == PrimitiveTypeKind.DOUBLE;
	}

	private static boolean isLong(Operand operand){
		Type t = operand.getType();
		if(t instanceof PrimitiveTypeKind == false) return false;
		return t == PrimitiveTypeKind.LONG;
	}

	private static boolean isInt(Operand operand){
		Type t = operand.getType();
		if(t instanceof PrimitiveTypeKind == false) return false;
		return t == PrimitiveTypeKind.INT;
	}

	public static Op get(synthesijer.ast.Op o, Operand lhs, Operand rhs){
		switch(o){
			case PLUS: {
				if(isDouble(lhs) || isDouble(rhs)) return FADD64;
				else if(isFloat(lhs) || isFloat(rhs)) return FADD32;
				else return ADD;
			}
			case MINUS: {
				if(isDouble(lhs) || isDouble(rhs)) return FSUB64;
				else if(isFloat(lhs) || isFloat(rhs)) return FSUB32;
				else return SUB;
			}
			case MUL: {
				if(isDouble(lhs) || isDouble(rhs)) return FMUL64;
				else if(isFloat(lhs) || isFloat(rhs)) return FMUL32;
				if(isLong(lhs) || isLong(rhs)) return MUL64;
				else return MUL32;
			}
			case DIV: {
				if(isDouble(lhs) || isDouble(rhs)) return FDIV64;
				if(isFloat(lhs) || isFloat(rhs)) return FDIV32;
				if(isLong(lhs) || isLong(rhs)) return DIV64;
				else return DIV32;
			}
			case MOD: {
				if(isLong(lhs) || isLong(rhs)) return MOD64;
				else return MOD32;
			}
			case LSHIFT: {
				if(rhs instanceof ConstantOperand){
					if(isLong(lhs) || isDouble(lhs)){
						return SIMPLE_LSHIFT64;
					}else{
						return SIMPLE_LSHIFT32;
					}
				}
				if(isLong(lhs)) return LSHIFT64;
				else return LSHIFT32;
			}
			case LOGIC_RSHIFT: {
				if(rhs instanceof ConstantOperand){
					if(isLong(lhs) || isDouble(lhs)){
						return SIMPLE_LOGIC_RSHIFT64;
					}else{
						return SIMPLE_LOGIC_RSHIFT32;
					}
				}
				if(isLong(lhs)) return LOGIC_RSHIFT64;
				else return LOGIC_RSHIFT32;
			}
			case ARITH_RSHIFT: {
				if(rhs instanceof ConstantOperand){
					if(isLong(lhs) || isDouble(lhs)){
						return SIMPLE_ARITH_RSHIFT64;
					}else{
						return SIMPLE_ARITH_RSHIFT32;
					}
				}
				if(isLong(lhs)) return ARITH_RSHIFT64;
				else return ARITH_RSHIFT32;
			}
			case COMPEQ:{
				if(isDouble(lhs) || isDouble(rhs)) return FCOMPEQ64;
				else if(isFloat(lhs) || isFloat(rhs)) return FCOMPEQ32;
				else return COMPEQ;
			}
			case NEQ:{
				if(isDouble(lhs) || isDouble(rhs)) return FNEQ64;
				else if(isFloat(lhs) || isFloat(rhs)) return FNEQ32;
				else return NEQ;
			}
			case GT:{
				if(isDouble(lhs) || isDouble(rhs)) return FGT64;
				else if(isFloat(lhs) || isFloat(rhs)) return FGT32;
				else return GT;
			}
			case LT:{
				if(isDouble(lhs) || isDouble(rhs)) return FLT64;
				else if(isFloat(lhs) || isFloat(rhs)) return FLT32;
				else return LT;
			}
			case GEQ:{
				if(isDouble(lhs) || isDouble(rhs)) return FGEQ64;
				else if(isFloat(lhs) || isFloat(rhs)) return FGEQ32;
				else return GEQ;
			}
			case LEQ:{
				if(isDouble(lhs) || isDouble(rhs)) return FLEQ64;
				else if(isFloat(lhs) || isFloat(rhs)) return FLEQ32;
				else return LEQ;
			}

			default:
				return get(o);
		}
	}

	public static Op get(synthesijer.ast.Op o){
		switch(o){
			case ASSIGN:
			case PLUS: return ADD;
			case MINUS: return SUB;
			case MUL: return MUL32;
			case DIV: return DIV32;
			case MOD: return MOD32;
			case COMPEQ: return COMPEQ;
			case NEQ: return NEQ;
			case GT: return GT;
			case LT: return LT;
			case GEQ: return GEQ;
			case LEQ: return LEQ;
			case LSHIFT: return LSHIFT32;
			case LOGIC_RSHIFT: return LOGIC_RSHIFT32;
			case ARITH_RSHIFT: return ARITH_RSHIFT32;
			case AND: return AND;
			case NOT: return NOT;
			case LAND: return LAND;
			case LOR: return LOR;
			case OR: return OR;
			case XOR: return XOR;
			case LNOT: return LNOT;
			default:
				System.out.println("undefined:" + o);
				return UNDEFINED;
		}
	}

	public boolean isForcedType(){
		return (type != PrimitiveTypeKind.UNDEFINED);
	}

	public Type getType(){
		return type;
	}

	public static Op parseOp(String k) throws Exception{
		switch(k){
			case "METHOD_ENTRY": return METHOD_ENTRY;
			case "METHOD_EXIT" : return METHOD_EXIT;
			case "ASSIGN" : return ASSIGN;
			case "NOP" : return NOP;
			case "ADD" : return ADD;
			case "SUB" : return SUB;
			case "MUL32" : return MUL32;
			case "MUL64" : return MUL64;
			case "DIV32" : return DIV32;
			case "DIV64" : return DIV64;
			case "MOD32" : return MOD32;
			case "MOD64" : return MOD64;
			case "LT" : return LT;
			case "LEQ" : return LEQ;
			case "GT" : return GT;
			case "GEQ" : return GEQ;
			case "COMPEQ" : return COMPEQ;
			case "NEQ" : return NEQ;
			case "SIMPLE_LSHIFT32" : return SIMPLE_LSHIFT32;
			case "SIMPLE_LOGIC_RSHIFT32" : return SIMPLE_LOGIC_RSHIFT32;
			case "SIMPLE_ARITH_RSHIFT32" : return SIMPLE_ARITH_RSHIFT32;
			case "SIMPLE_LSHIFT64" : return SIMPLE_LSHIFT64;
			case "SIMPLE_LOGIC_RSHIFT64" : return SIMPLE_LOGIC_RSHIFT64;
			case "SIMPLE_ARITH_RSHIFT64" : return SIMPLE_ARITH_RSHIFT64;
			case "LSHIFT32" : return LSHIFT32;
			case "LOGIC_RSHIFT32": return LOGIC_RSHIFT32;
			case "ARITH_RSHIFT32" : return ARITH_RSHIFT32;
			case "LSHIFT64" : return LSHIFT64;
			case "LOGIC_RSHIFT64" : return LOGIC_RSHIFT64;
			case "ARITH_RSHIFT64" : return ARITH_RSHIFT64;
			case "JP" : return JP;
			case "JT" : return JT;
			case "RETURN" : return RETURN;
			case "MULTI_RETURN" : return MULTI_RETURN;
			case "SELECT" : return SELECT;
			case "AND" : return AND;
			case "NOT" : return NOT;
			case "MSB_FLAP":  return MSB_FLAP;
			case "LAND" : return LAND;
			case "LOR" : return LOR;
			case "OR" : return OR;
			case "XOR" : return XOR;
			case "LNOT" : return LNOT;
			case "ARRAY_ACCESS" : return ARRAY_ACCESS;
			case "ARRAY_ACCESS0" : return ARRAY_ACCESS0;
			case "ARRAY_ACCESS_WAIT" : return ARRAY_ACCESS_WAIT;
			case "ARRAY_INDEX" : return ARRAY_INDEX;
			case "CALL" : return CALL;
			case "EXT_CALL" : return EXT_CALL;
			case "FIELD_ACCESS" : return FIELD_ACCESS;
			case "BREAK" : return BREAK;
			case "CONTINUE" : return CONTINUE;
			case "CAST" : return CAST;
			case "COND" : return COND;
			case "FADD32" : return FADD32;
			case "FSUB32" : return FSUB32;
			case "FMUL32" : return FMUL32;
			case "FDIV32" : return FDIV32;
			case "FADD64" : return FADD64;
			case "FSUB64" : return FSUB64;
			case "FMUL64" : return FMUL64;
			case "FDIV64" : return FDIV64;
			case "CONV_F2I" : return CONV_F2I;
			case "CONV_I2F" : return CONV_I2F;
			case "CONV_D2L" : return CONV_D2L;
			case "CONV_L2D" : return CONV_L2D;
			case "CONV_F2D" : return CONV_F2D;
			case "CONV_D2F" : return CONV_D2F;
			case "FLT32" : return FLT32;
			case "FLEQ32" : return FLEQ32;
			case "FGT32" : return FGT32;
			case "FGEQ32" : return FGEQ32;
			case "FCOMPEQ32" : return FCOMPEQ32;
			case "FNEQ32" : return FNEQ32;
			case "FLT64" : return FLT64;
			case "FLEQ64" : return FLEQ64;
			case "FGT64" : return FGT64;
			case "FGEQ64" : return FGEQ64;
			case "FCOMPEQ64" : return FCOMPEQ64;
			case "FNEQ64" : return FNEQ64;
			case "FIFO_WRITE" : return FIFO_WRITE;
			case "PHI" : return PHI;
			case "UNDEFINED" : return UNDEFINED;
			default:
				throw new Exception("Undefined operation : " + k);
		}
	}

}
