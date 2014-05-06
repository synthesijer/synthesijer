package synthesijer.ast.type;

import java.io.PrintWriter;

import synthesijer.ast.Type;
import synthesijer.hdl.HDLType;

public class ComponentType implements Type{
	
	private final String name;
	
	public ComponentType(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void dumpAsXML(PrintWriter dest){
		dest.printf("<type kind=\"component\" name=\"%s\"/>\n", name);
	}

	public HDLType getHDLType(){
		System.err.println("unsupported type: " + this);
		return null;
	}

}
