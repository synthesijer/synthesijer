package synthesijer.ast;

import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.ComponentType;
import synthesijer.ast.type.PrimitiveTypeKind;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLType;

public class Variable {

	private final String name;
	private final Type type;
	private final Method method;
	
	public Variable(String n, Type t, Method method){
		this.name = n;
		this.type = t;
		this.method = method;
	}
	
	public String getName(){
		return name;
	}
	
	public Type getType(){
		return type;
	}
	
	public String getUniqueName(){
		if(method != null){
			return method.getName() + "_" + name;
		}else{
			return name;
		}
	}

	public HDLSignal genHDLSignal(HDLModule m){
		HDLType t = type.getHDLType();
		if(type instanceof PrimitiveTypeKind){
			HDLSignal s = new HDLSignal(m, getUniqueName(), t, HDLSignal.ResourceKind.REGISTER);
			m.addSignal(s);
			return s;
		}else if(type instanceof ArrayType){
			System.err.println("unsupported type: " + type);
			return null;
		}else if(type instanceof ComponentType){
			System.err.println("unsupported type: " + type);
			return null;
		}else{
			System.err.printf("unkonw type: %s(%s)\n", type, type.getClass());
			return null;
		}
	}

	public HDLPort genHDLPort(HDLModule m, HDLPort.DIR dir){
		if(type instanceof PrimitiveTypeKind){
			HDLType t = ((PrimitiveTypeKind)type).getHDLType();
			HDLPort port = new HDLPort(getUniqueName(), dir, t);
			m.addPort(port);
			return port;
		}else if(type instanceof ArrayType){
			System.err.println("unsupported type: " + type);
			return null;
		}else if(type instanceof ComponentType){
			System.err.println("unsupported type: " + type);
			return null;
		}else{
			System.err.printf("unkonw type: %s(%s)\n", type, type.getClass());
			return null;
		}
	}

}
