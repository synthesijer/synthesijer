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
	MUL,
	DIV,
	MOD,
	LT(PrimitiveTypeKind.BOOLEAN),
	LEQ(PrimitiveTypeKind.BOOLEAN),
	GT(PrimitiveTypeKind.BOOLEAN),
	GEQ(PrimitiveTypeKind.BOOLEAN),
	COMPEQ(PrimitiveTypeKind.BOOLEAN),
	NEQ(PrimitiveTypeKind.BOOLEAN),
	LSHIFT,
	LOGIC_RSHIFT,
	ARITH_RSHIFT,
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
	BREAK,
	CONTINUE,
	CAST,
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
	
	public static Op get(synthesijer.ast.Op o){
		switch(o){
		case ASSIGN: 
		case PLUS: return ADD;
		case MINUS: return SUB;
		case MUL: return MUL;
		case DIV: return DIV;
		case MOD: return MOD;
		case COMPEQ: return COMPEQ;
		case NEQ: return NEQ; 
		case GT: return GT;
		case LT: return LT;
		case GEQ: return GEQ;
		case LEQ: return LEQ;
		case LSHIFT: return LSHIFT;
		case LOGIC_RSHIFT: return LOGIC_RSHIFT;
		case ARITH_RSHIFT: return ARITH_RSHIFT;
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
