package synthesijer.hdl;

public enum HDLOp {
		
	ADD(KIND.INFIX, "+"),
	SUB(KIND.INFIX, "-"),
	AND(KIND.INFIX, "and", "&&"),
	OR(KIND.INFIX, "or", "||"),
	XOR(KIND.INFIX, "xor", "^"),
	NOT(KIND.OTHER, "not", "~"),
	EQ(KIND.COMP, "=", "=="),
	LT(KIND.COMP, "<", "<"),
	GT(KIND.COMP, ">", ">"),
	LEQ(KIND.COMP, "<=", "<="),
	GEQ(KIND.COMP, ">=", ">="),
	NEQ(KIND.COMP, "/=", "!="),
	REF(KIND.OTHER),
	IF(KIND.OTHER),
	UNDEFINED(KIND.OTHER);

	enum KIND{
		INFIX, COMP, OTHER
	};

	private final String vhdlSym, verilogSym;
	private final KIND kind;
	
	private HDLOp(KIND kind, String vhdlSym, String verilogSym){
		this.kind = kind;
		this.vhdlSym = vhdlSym;
		this.verilogSym = verilogSym;
	}
	
	public boolean isInfix(){
		return kind == KIND.INFIX;
	}

	public boolean isCompare(){
		return kind == KIND.COMP;
	}

	private HDLOp(KIND kind, String sym){
		this(kind, sym, sym);
	}

	private HDLOp(KIND kind){
		this(kind, "", "");
	}
	
	public String getVHDL(){
		return vhdlSym;
	}

	public String getVerilogHDL(){
		return verilogSym;
	}

}
