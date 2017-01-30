package synthesijer.ast;

import synthesijer.hdl.HDLOp;

public enum Op{
	
    ASSIGN("=", HDLOp.UNDEFINED),
    PLUS("+", HDLOp.ADD),
    MINUS("-", HDLOp.SUB),
    MMMINUS("---", HDLOp.SUB),
//  SIGN_MINUS("---", HDLOp.SUB),
//  SIGN_MINUS("---", HDLOp.SIGNFLAP),
    MUL("*", HDLOp.MUL),
    DIV("/", HDLOp.UNDEFINED),
    MOD("%", HDLOp.UNDEFINED),
    COMPEQ("==", HDLOp.EQ),
    NEQ("!=", HDLOp.NEQ),
    GT(">", HDLOp.GT),
    GEQ(">=", HDLOp.GEQ),
    LT("<", HDLOp.LT),
    LEQ("<=", HDLOp.LEQ),
    LSHIFT("<<", HDLOp.LSHIFT32),
    LOGIC_RSHIFT(">>>", HDLOp.LOGIC_RSHIFT32),
    ARITH_RSHIFT(">>", HDLOp.ARITH_RSHIFT32),
    AND("&", HDLOp.AND),
    NOT("~", HDLOp.NOT),
    LAND("&&", HDLOp.AND),
    LOR("||", HDLOp.OR),
    OR("|", HDLOp.OR),
    XOR("^", HDLOp.XOR),
    LNOT("!", HDLOp.NOT),
    INC("++", HDLOp.ADD),
    DEC("--", HDLOp.SUB),
    RETURN("return", HDLOp.UNDEFINED),
    MULTI_RETURN("multi_return", HDLOp.UNDEFINED),
    CALL("call", HDLOp.UNDEFINED),
    JC("jc", HDLOp.UNDEFINED),
    JEQ("jeq", HDLOp.UNDEFINED),
    J("j", HDLOp.UNDEFINED),
    SELECT("select", HDLOp.UNDEFINED),
    UNDEFINED("UNDEFINED", HDLOp.UNDEFINED);
	
	private final String name;
	private final HDLOp hdlOp;
	
	Op(String name, HDLOp hdlOp){
		this.name = name;
		this.hdlOp = hdlOp;
	}
	
	public HDLOp getHDLOp(){
		return hdlOp;
	}
	
	public static Op getOp(String opName){
		if(ASSIGN.name.equals(opName))      return Op.ASSIGN;
		else if(PLUS.name.equals(opName))   return Op.PLUS;
		else if(MINUS.name.equals(opName))  return Op.MINUS;
		else if(MMMINUS.name.equals(opName))  return Op.MINUS;
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
