package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;
import synthesijer.ast.type.PrimitiveTypeKind;
import synthesijer.ast.type.StringType;

public class Literal extends Expr{

	private boolean valueBoolean;
	private byte valueByte;
	private char valueChar;
	private short valueShort;
	private int valueInt;
	private long valueLong;
	private double valueDouble;
	private float valueFloat;
	private String valueStr;

	private Type type = PrimitiveTypeKind.UNDEFINED;

	private int width;

	public Literal(Scope scope){
		super(scope);
	}

	public Literal copy(Scope scope){
		Literal newL = new Literal(scope);
		newL.valueBoolean = this.valueBoolean;
		newL.valueByte = this.valueByte;
		newL.valueChar = this.valueChar;
		newL.valueShort = this.valueShort;
		newL.valueInt = this.valueInt;
		newL.valueLong = this.valueLong;
		newL.valueDouble = this.valueDouble;
		newL.valueFloat = this.valueFloat;
		newL.valueStr = this.valueStr;
		newL.type = this.type;
		newL.width = this.width;
		return newL;
	}

	public Type getType(){
		return type;
	}

	public boolean isBoolean(){
		return (getType()) == PrimitiveTypeKind.BOOLEAN;
	}

	public void setValue(boolean value){
		this.type = PrimitiveTypeKind.BOOLEAN;
		this.valueBoolean = value;
		this.width = 1;
	}

	public void setValue(byte value){
		this.type = PrimitiveTypeKind.BYTE;
		this.valueByte = value;
		this.width = 8;
	}

	public void setValue(char value){
		this.type = PrimitiveTypeKind.CHAR;
		this.valueChar = value;
		this.width = 16;
	}

	public void setValue(short value){
		this.type = PrimitiveTypeKind.SHORT;
		this.valueShort = value;
		this.width = 16;
	}

	public void setValue(int value){
		this.type = PrimitiveTypeKind.INT;
		this.valueInt = value;
		this.width = 32;
	}

	public void setValue(long value){
		this.type = PrimitiveTypeKind.LONG;
		this.valueLong = value;
		this.width = 64;
	}

	public void setValue(double value){
		this.type = PrimitiveTypeKind.DOUBLE;
		this.valueDouble = value;
		this.width = 64;
	}

	public void setValue(float value){
		this.type = PrimitiveTypeKind.FLOAT;
		this.valueFloat = value;
		this.width = 32;
	}

	public void setValue(String value){
		this.type = new StringType();
		this.valueStr = value;
		this.width = 0;
	}

	public void setNull(){
		this.type = PrimitiveTypeKind.NULL;
		this.valueStr = null;
		this.width = 0;
	}

	public void setUndefined(){
		this.type = PrimitiveTypeKind.UNDEFINED;
		this.valueStr = null;
		this.width = 0;
	}

	public String getValueAsStr(){
		if(type instanceof PrimitiveTypeKind){
			switch((PrimitiveTypeKind)type){
				case BOOLEAN: return String.valueOf(valueBoolean);
				case BYTE:    return String.valueOf(valueByte);
				case CHAR:    return String.valueOf((int)valueChar);
				case SHORT:   return String.valueOf(valueShort);
				case INT:     return String.valueOf(valueInt);
				case LONG:    return String.valueOf(valueLong);
				case DOUBLE:  return String.valueOf(valueDouble);
				case FLOAT:   return String.valueOf(valueFloat);
				case NULL:    return "NULL";
				default: return "UNKNOWN";
			}
		}else if(type instanceof StringType){
			return valueStr;
		}else{
			return "UNKNOWN";
		}
	}

	public Literal castType(Type newType){
		System.out.println("cast");
		if(newType instanceof PrimitiveTypeKind){
			switch((PrimitiveTypeKind)newType){
				case BOOLEAN: valueBoolean = Boolean.valueOf(getValueAsStr()); break;
				case BYTE:{
					System.out.print(getValueAsStr());
					System.out.println(" => " + Integer.valueOf(getValueAsStr()).byteValue());
					valueByte    = Integer.valueOf(getValueAsStr()).byteValue(); break;//Byte.valueOf(getValueAsStr()); break;
				}
				case CHAR:    valueChar    = (char)(Integer.valueOf(getValueAsStr()) & 0x0000FFFF); break;
				case SHORT:   valueShort   = Short.valueOf(getValueAsStr()); break;
				case INT:     valueInt     = Integer.valueOf(getValueAsStr()); break;
				case LONG:    valueLong    = Long.valueOf(getValueAsStr()); break;
				case DOUBLE:  valueDouble  = Double.valueOf(getValueAsStr()); break;
				case FLOAT:   valueFloat   = Float.valueOf(getValueAsStr()); break;
				case NULL:    valueStr     = null; break;
				default: throw new RuntimeException(String.format("cannot cast from %s into %s", type, newType));
			}
		}else if(newType instanceof StringType){
			valueStr = getValueAsStr();
		}else{
			throw new RuntimeException(String.format("cannot cast from %s into %s", type, newType));
		}
		type = newType;
		return this;
	}

	public void accept(SynthesijerExprVisitor v){
		v.visitLitral(this);
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public boolean isVariable() {
		return true;
	}

	public String toString(){
		return "Literal:" + getValueAsStr() + "@" + getType();
	}

	@Override
	public Variable[] getSrcVariables(){
		return new Variable[]{};
	}

	@Override
	public Variable[] getDestVariables(){
		return new Variable[]{};
	}

	@Override
	public boolean hasMethodInvocation() {
		return false;
	}
}
