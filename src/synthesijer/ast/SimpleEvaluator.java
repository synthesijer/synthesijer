package synthesijer.ast;

import synthesijer.ast.expr.Literal;
import synthesijer.ast.type.PrimitiveTypeKind;

public class SimpleEvaluator {
	
    interface MathBiFunction<E>{
        E operation(E a, E b);
    }
    
    interface CompareBiFunction<E>{
    	boolean operation(E a, E b);
    }
    
    interface UniFunction<E>{
        E operation(E a);
    }
		
    public static Literal eval(Op op, Type t0, String v0) throws SynthesijerAstException{
        
        UniFunction<Double> floatFunc = null;
        UniFunction<Long> intFunc = null;
        UniFunction<Boolean> boolFunc = null;
        
        switch(op){
        case ASSIGN:
            floatFunc = (a) -> a;
            intFunc = (a) -> a;
            boolFunc = (a) -> a;
            break;
        case MINUS:
        case MMMINUS:
            floatFunc = (a) -> -a;
            intFunc = (a) -> -a;
            break;
        case NOT:
            intFunc = (a) -> ~a;
            break;
        case LNOT:
            boolFunc = (a) -> !a;
            break;
        case INC:
            floatFunc = (a) -> a+1;
            intFunc = (a) -> a+1;
            break;
        case DEC:
            floatFunc = (a) -> a-1;
            intFunc = (a) -> a-1;
            break;
        default:
            throw new SynthesijerAstException();
        }

        
        long la = 0L, lc = 0L;
        double da = 0d, dc = 0d;
        boolean ba = false, bc = false;
        
        boolean floatFlag = false;
        boolean booleanFlag = false;
        
        if(t0 == PrimitiveTypeKind.FLOAT || t0 == PrimitiveTypeKind.DOUBLE){
            da = Double.parseDouble(v0);
            floatFlag = true;
        }else if(t0 == PrimitiveTypeKind.BOOLEAN){
            ba = Boolean.parseBoolean(v0);
            booleanFlag = true;
        }else{
            la = Long.parseLong(v0);
        }
        
        Literal result = new Literal(null);
        
        if(booleanFlag){
            bc = boolFunc.operation(ba);
            result.setValue(bc);
        }else if(floatFlag){
            dc = floatFunc.operation(da);
            result.setValue(dc);
        }else{
            lc = intFunc.operation(la);
            result.setValue(lc);
        }
        return result;
    }
    
    public static Literal eval(Op op, Type t0, String v0, Type t1, String v1) throws SynthesijerAstException{
		
    	MathBiFunction<Double> floatFunc = null;
    	MathBiFunction<Long> intFunc = null;
    	
    	CompareBiFunction<Double> floatCompare = null;
    	CompareBiFunction<Long> intCompare = null;
    	CompareBiFunction<Boolean> boolCompare = null;
    	
    	boolean mathFunc = false;
    	boolean compareFunc = false;
		
		switch(op){
    	case PLUS:
    		floatFunc = (a, b) -> a + b;
    		intFunc = (a, b) -> a + b;
    		mathFunc = true;
    		break;
    	case MINUS:
    		floatFunc = (a, b) -> a - b;
    		intFunc = (a, b) -> a - b;
    		mathFunc = true;
    		break;
    	case MUL:
    		floatFunc = (a, b) -> a * b;
    		intFunc = (a, b) -> a * b;
    		mathFunc = true;
    		break;
    	case DIV:
    		floatFunc = (a, b) -> a / b;
    		intFunc = (a, b) -> a / b;
    		mathFunc = true;
    		break;
    	case MOD:
    		floatFunc = (a, b) -> a % b;
    		intFunc = (a, b) -> a % b;
    		mathFunc = true;
    		break;
    	case COMPEQ:
    		floatCompare = (a, b) -> a == b;
    		intCompare = (a, b) -> a == b;
    		boolCompare = (a, b) -> a == b;
    		compareFunc = true;
    		break;
		case NEQ:
    		floatCompare = (a, b) -> a != b;
    		intCompare = (a, b) -> a != b;
    		boolCompare = (a, b) -> a != b;
    		compareFunc = true;
    		break;
		case GT:
    		floatCompare = (a, b) -> a > b;
    		intCompare = (a, b) -> a > b;
    		compareFunc = true;
    		break;
		case GEQ:
    		floatCompare = (a, b) -> a >= b;
    		intCompare = (a, b) -> a >= b;
    		compareFunc = true;
    		break;
		case LT:
    		floatCompare = (a, b) -> a < b;
    		intCompare = (a, b) -> a < b;
    		compareFunc = true;
    		break;
		case LEQ:
    		floatCompare = (a, b) -> a <= b;
    		intCompare = (a, b) -> a <= b;
    		compareFunc = true;
    		break;
		case LSHIFT:
    		intFunc = (a, b) -> a << b;
    		mathFunc = true;
    		break;
		case LOGIC_RSHIFT:
    		intFunc = (a, b) -> a >>> b;
    		mathFunc = true;
    		break;
		case ARITH_RSHIFT:
    		intFunc = (a, b) -> a >> b;
    		mathFunc = true;
    		break;
		case AND:
    		intFunc = (a, b) -> a & b;
			boolCompare = (a, b) -> a & b;
    		mathFunc = true;
			break;
		case LAND:
			boolCompare = (a, b) -> a && b;
			break;
		case LOR:
			boolCompare = (a, b) -> a || b;
			break;
		case OR:
    		intFunc = (a, b) -> a | b;
			boolCompare = (a, b) -> a | b;
    		mathFunc = true;
			break;
		case XOR:
    		intFunc = (a, b) -> a ^ b;
			boolCompare = (a, b) -> a ^ b;
    		mathFunc = true;
			break;
		default:
            throw new SynthesijerAstException();
		}
        
        long la = 0L, lb = 0L, lc = 0L;
        double da = 0d, db = 0d, dc = 0d;
        boolean ba = false, bb = false, bc = false;
        
        boolean floatFlag = false;
        boolean booleanFlag = false;
        
        if(t0 == PrimitiveTypeKind.FLOAT || t0 == PrimitiveTypeKind.DOUBLE
                || t1 == PrimitiveTypeKind.FLOAT || t1 == PrimitiveTypeKind.DOUBLE){
            da = Double.parseDouble(v0);
            db = Double.parseDouble(v1);
            floatFlag = true;
        }else if(t0 == PrimitiveTypeKind.BOOLEAN || t1 == PrimitiveTypeKind.BOOLEAN){
            ba = Boolean.parseBoolean(v0);
            bb = Boolean.parseBoolean(v1);
            booleanFlag = true;
        }else{
            la = Long.parseLong(v0);
            lb = Long.parseLong(v1);
        }
        
        Literal result = new Literal(null);
        
        if(booleanFlag){
            bc = boolCompare.operation(ba, bb);
            result.setValue(bc);
        }else if(floatFlag){
            if(mathFunc){
                dc = floatFunc.operation(da, db);
                result.setValue(dc);
            }else{
                bc = floatCompare.operation(da, db);
                result.setValue(bc);
            }
        }else{
            if(mathFunc){
                lc = intFunc.operation(la, lb);
                result.setValue(lc);
            }else{
                bc = intCompare.operation(la, lb);
                result.setValue(bc);
            }
        }
        return result;
	}
	
	public static void main(String... args) throws Exception{
		System.out.println(eval(Op.PLUS, PrimitiveTypeKind.INT, "10", PrimitiveTypeKind.INT, "20"));
		System.out.println(eval(Op.PLUS, PrimitiveTypeKind.DOUBLE, "10.1", PrimitiveTypeKind.FLOAT, "20.2"));
		System.out.println(eval(Op.MINUS, PrimitiveTypeKind.DOUBLE, "10.1", PrimitiveTypeKind.FLOAT, "20.2"));
	}

}
