package synthesijer.ast;

import synthesijer.hdl.HDLOp;
import com.sun.source.tree.Tree;

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
	
	public static Op getOp(Tree.Kind kind){
		switch(kind){
		case AND:                             return Op.AND;          // bitwise and logical "and" &.
		case AND_ASSIGNMENT:                  return Op.AND;          // bitwise and logical "and" assignment &=.
		case BITWISE_COMPLEMENT:              return Op.NOT;          // bitwise complement operator ~.
		case CONDITIONAL_AND:                 return Op.AND;          // conditional-and &&.
		case CONDITIONAL_OR:                  return Op.OR;           // conditional-or ||.
		case DIVIDE:                          return Op.DIV;          // division /.
		case DIVIDE_ASSIGNMENT:               return Op.DIV;          // division assignment /=.
		case EQUAL_TO:                        return Op.COMPEQ;       // equal-to ==.
		case GREATER_THAN:                    return Op.GT;           // greater-than >.
		case GREATER_THAN_EQUAL:              return Op.GEQ;          // greater-than-equal >=.
		case LEFT_SHIFT:                      return Op.LSHIFT;       // left shift <<.
		case LEFT_SHIFT_ASSIGNMENT:           return Op.LSHIFT;       // left shift assignment <<=.
		case LESS_THAN:                       return Op.LT;           // less-than <.
		case LESS_THAN_EQUAL:                 return Op.LEQ;          // less-than-equal <=.
		case LOGICAL_COMPLEMENT:              return Op.LNOT;         // logical complement operator !.
		case MINUS:                           return Op.MINUS;        // subtraction -.
		case MINUS_ASSIGNMENT:                return Op.MINUS;        // subtraction assignment -=.
		case MULTIPLY:                        return Op.MUL;          // multiplication *.
		case MULTIPLY_ASSIGNMENT:             return Op.MUL;          // multiplication assignment *=.
		case NOT_EQUAL_TO:                    return Op.NEQ;          // not-equal-to !=.
		case OR:                              return Op.OR;           // bitwise and logical "or" |.
		case OR_ASSIGNMENT:                   return Op.OR;           // bitwise and logical "or" assignment |=.
		case PLUS:                            return Op.PLUS;         // addition or string concatenation +.
		case PLUS_ASSIGNMENT:                 return Op.PLUS;         // addition or string concatenation assignment +=.
		case POSTFIX_DECREMENT:               return Op.DEC;          // postfix decrement operator --.
		case POSTFIX_INCREMENT:               return Op.INC;          // postfix increment operator ++.
		case PREFIX_DECREMENT:                return Op.DEC;          // prefix decrement operator --.
		case PREFIX_INCREMENT:                return Op.INC;          // prefix increment operator ++.
		case REMAINDER:                       return Op.MOD;          // remainder %.
		case REMAINDER_ASSIGNMENT:            return Op.MOD;          // remainder assignment %=.
		case RIGHT_SHIFT:                     return Op.ARITH_RSHIFT; // right shift >>.
		case RIGHT_SHIFT_ASSIGNMENT:          return Op.ARITH_RSHIFT; // right shift assignment >>=.
		case UNARY_MINUS:                     return Op.MINUS;        // unary minus operator -.
		case UNARY_PLUS:                      return Op.PLUS;         // unary plus operator +.
		case UNSIGNED_RIGHT_SHIFT:            return Op.LOGIC_RSHIFT; // unsigned right shift >>>.
		case UNSIGNED_RIGHT_SHIFT_ASSIGNMENT: return Op.LOGIC_RSHIFT; // unsigned right shift assignment >>>=.
		case XOR:                             return Op.XOR;          // bitwise and logical "xor" ^.
		case XOR_ASSIGNMENT:                  return Op.XOR;          // bitwise and logical "xor" assignment ^=.
		default:                              return Op.UNDEFINED;
		}
	}
	
		
}
