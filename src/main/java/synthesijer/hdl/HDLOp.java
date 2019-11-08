package synthesijer.hdl;

public enum HDLOp {

	ADD(KIND.INFIX, 2, "+"),
	SUB(KIND.INFIX, 2, "-"),
	MUL(KIND.INFIX, 2, "*"),
	AND(KIND.INFIX, 2, "and", "&"),
	OR(KIND.INFIX, 2, "or", "|"),
	XOR(KIND.INFIX, 2, "xor", "^"),
	NOT(KIND.OTHER, 1, "not", "~"),
	EQ(KIND.COMP, 2, "=", "=="),
	IS(KIND.OTHER, 2, "=", "=="),
	LT(KIND.COMP, 2, "<", "<"),
	GT(KIND.COMP, 2, ">", ">"),
	LEQ(KIND.COMP, 2, "<=", "<="),
	GEQ(KIND.COMP, 2, ">=", ">="),
	NEQ(KIND.COMP, 2, "/=", "!="),
	REF(KIND.OTHER, 2),
	IF(KIND.OTHER, 3),
	CONCAT(KIND.OTHER, 2),
	DROPHEAD(KIND.OTHER, 2),
	TAKE(KIND.OTHER, 2),
	PADDINGHEAD(KIND.OTHER, 2),
	PADDINGHEAD_ZERO(KIND.OTHER, 2),
	ID(KIND.OTHER, 1),
	ARITH_RSHIFT(KIND.OTHER, 2),
	LOGIC_RSHIFT(KIND.OTHER, 2),
	LSHIFT(KIND.OTHER, 2),
	ARITH_RSHIFT32(KIND.OTHER, 2),
	LOGIC_RSHIFT32(KIND.OTHER, 2),
	LSHIFT32(KIND.OTHER, 2),
	ARITH_RSHIFT64(KIND.OTHER, 2),
	LOGIC_RSHIFT64(KIND.OTHER, 2),
	LSHIFT64(KIND.OTHER, 2),
	MSB_FLAP(KIND.OTHER, 1),
	HDLMUL(KIND.OTHER, 2, "*"),
	PHI(KIND.OTHER, -1),
	UNDEFINED(KIND.OTHER, 0),
	;

	enum KIND{
		INFIX, COMP, OTHER
	};

	private final String vhdlSym, verilogSym;
	private final KIND kind;
	private final int argc;

	private HDLOp(KIND kind, int argc, String vhdlSym, String verilogSym){
		this.kind = kind;
		this.argc = argc;
		this.vhdlSym = vhdlSym;
		this.verilogSym = verilogSym;
	}

	private HDLOp(KIND kind, int argc, String sym){
		this(kind, argc, sym, sym);
	}

	private HDLOp(KIND kind, int argc){
		this(kind, argc, "", "");
	}

	public boolean isInfix(){
		return kind == KIND.INFIX;
	}

	public boolean isCompare(){
		return kind == KIND.COMP;
	}

	public int getArgNums(){
		return argc;
	}

	public String getVHDL(){
		return vhdlSym;
	}

	public String getVerilogHDL(){
		return verilogSym;
	}

}
