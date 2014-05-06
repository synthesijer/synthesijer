package synthesijer.ast;

public enum Op {
	
	ASSIGN("="),
	PLUS("+"),
	MINUS("-"),
	MUL("*"),
	DIV("/"),
	MOD("%"),
	COMPEQ("=="),
	NEQ("!="),
	GT(">"),
	GEQ(">="),
	LT("<"),
	LEQ("<="),
	LSHIFT("<<"),
	LOGIC_RSHIFT(">>>"),
	ARITH_RSHIFT(">>"),
	AND("&"),
	NOT("~"),
	LAND("&&"),
	LOR("||"),
	OR("|"),
	XOR("^"),
	LNOT("!"),
	INC("++"),
	DEC("--"),
	UNDEFINED("UNDEFINED");
	
	private final String name;
	
	Op(String name){
		this.name = name;
	}
	
	public static Op getOp(String opName){
		if(ASSIGN.name.equals(opName))      return Op.ASSIGN;
		else if(PLUS.name.equals(opName))   return Op.PLUS;
		else if(MINUS.name.equals(opName))  return Op.MINUS;
		else if(MUL.name.equals(opName))    return Op.MUL;
		else if(DIV.name.equals(opName))    return Op.DIV;
		else if(MOD.name.equals(opName))    return Op.MOD;
		else if(COMPEQ.name.equals(opName)) return Op.COMPEQ;
		else if(NEQ.name.equals(opName))    return Op.NEQ;
		else if(GT.name.equals(opName))     return Op.GT;
		else if(GEQ.name.equals(opName))    return Op.GEQ;
		else if(LT.name.equals(opName))     return Op.LT;
		else if(LEQ.name.equals(opName))    return Op.LEQ;
		else if(LSHIFT.name.equals(opName)) return Op.LSHIFT;
		else if(LOGIC_RSHIFT.name.equals(opName)) return Op.LOGIC_RSHIFT;
		else if(ARITH_RSHIFT.name.equals(opName)) return Op.ARITH_RSHIFT;
		else if(AND.name.equals(opName))    return Op.AND;
		else if(NOT.name.equals(opName))    return Op.NOT;
		else if(LAND.name.equals(opName))   return Op.LAND;		
		else if(LOR.name.equals(opName))    return Op.LOR;
		else if(OR.name.equals(opName))     return Op.OR;
		else if(XOR.name.equals(opName))    return Op.XOR;
		else if(LNOT.name.equals(opName))   return Op.LNOT;
		else if(INC.name.equals(opName))    return Op.INC;
		else if(DEC.name.equals(opName))    return Op.DEC;
		else return Op.UNDEFINED;
	}

}
