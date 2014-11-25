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
	SIMPLE_LSHIFT,
	SIMPLE_LOGIC_RSHIFT,
	SIMPLE_ARITH_RSHIFT,
	LSHIFT32(1),
	LOGIC_RSHIFT32(1),
	ARITH_RSHIFT32(1),
	LSHIFT64(1),
	LOGIC_RSHIFT64(1),
	ARITH_RSHIFT64(1),
	JP(true),
	JT(true),
	RETURN(true),
	SELECT(true), // switch selector
	AND,
	NOT,
	LAND,
	LOR,
	OR,
	XOR,
	LNOT,
	ARRAY_ACCESS,
	CALL,
	EXT_CALL,
	FIELD_ACCESS,
	BREAK(true),
	CONTINUE,
	CAST,
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
		this(false, latency, PrimitiveTypeKind.UNDEFIEND);
	}

	/**
	 * 
	 * @param flag branch instruction or not  
	 */
	private Op(boolean flag){
		this(flag, 0, PrimitiveTypeKind.UNDEFIEND);
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
		this(false, 0, PrimitiveTypeKind.UNDEFIEND);
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
			if(rhs instanceof ConstantOperand) return SIMPLE_LSHIFT;
			if(isLong(lhs)) return LSHIFT64;
			else return LSHIFT32;
		}
		case LOGIC_RSHIFT: {
			if(rhs instanceof ConstantOperand) return SIMPLE_LOGIC_RSHIFT;
			if(isLong(lhs)) return LOGIC_RSHIFT64;
			else return LOGIC_RSHIFT32;
		}
		case ARITH_RSHIFT: {
			if(rhs instanceof ConstantOperand) return SIMPLE_ARITH_RSHIFT;
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
			System.out.println("undefiend:" + o);
			return UNDEFINED;
		}
	}
	
	public boolean isForcedType(){
		return (type != PrimitiveTypeKind.UNDEFIEND);
	}

	public Type getType(){
		return type;
	}

}
