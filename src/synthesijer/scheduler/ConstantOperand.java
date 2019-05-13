package synthesijer.scheduler;

import synthesijer.ast.Type;
import synthesijer.ast.type.ArrayRef;
import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.PrimitiveTypeKind;

public class ConstantOperand implements Operand{

	private final String name;

	private final String value;

	private final String origValue;

	private Type type;

	public ConstantOperand(String name, String value, Type type){
		this.name = name;
		this.origValue = value;
		if(type instanceof PrimitiveTypeKind){
			switch((PrimitiveTypeKind)type){
				case FLOAT:
					float f = Float.parseFloat(value);
					this.value = String.valueOf(Float.floatToRawIntBits(f));
					break;
				case DOUBLE:
					double d = Float.parseFloat(value);
					this.value = String.valueOf(Double.doubleToLongBits(d));
					break;
				default:
					this.value = value;
			}
		}else{
			this.value = value;
		}
		this.type = getReferedType(type);
	}

	private Type getReferedType(Type t){

		if(t instanceof ArrayRef){
			return getReferedType(((ArrayRef)t).getRefType());
		}else if(t instanceof ArrayType){
			return getReferedType(((ArrayType)t).getElemType());
		}else{
			return t;
		}
	}

	@Override
	public String getName(){
		return name;
	}

	@Override
	public Type getType(){
		return type;
	}

	public void setType(Type t){
		this.type = getReferedType(t);
	}

	public String getValue(){
		return value;
	}

	public String getOrigValue(){
		return origValue;
	}

	@Override
	public String info(){
		return value + ":" + type + "("+ name +")";
	}

	@Override
	public boolean isChaining(SchedulerItem ctx){
		return false;
	}

	@Override
	public String dump(){
		return name + ":" + type + ", value = " + value;
	}

	@Override
	public String toSexp(){
		return "(CONSTANT " + type + " " +  getName() + " " + value + ")";
	}

}
