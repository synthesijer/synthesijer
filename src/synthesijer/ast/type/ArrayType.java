package synthesijer.ast.type;

import java.io.PrintWriter;

import synthesijer.ast.Type;
import synthesijer.hdl.HDLType;

public class ArrayType implements Type{
	
	private final Type elemType;
	
	public ArrayType(Type k){
		elemType = k;
	}
	
	public void dumpAsXML(PrintWriter dest){
		dest.printf("<type kind=\"array\">\n");
		elemType.dumpAsXML(dest);
		dest.printf("</type>\n");
	}

	public HDLType getHDLType(){
		System.err.println("unsupported type: " + this);
		return null;
	}
}
