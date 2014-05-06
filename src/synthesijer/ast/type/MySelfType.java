package synthesijer.ast.type;

import java.io.PrintWriter;

import synthesijer.ast.Type;
import synthesijer.hdl.HDLType;

public class MySelfType implements Type{
	
	public void dumpAsXML(PrintWriter dest){
		dest.printf("<type kind=\"myself\"/>\n");
	}

	public HDLType getHDLType(){
		System.err.println("unsupported type: " + this);
		return null;
	}
	
}
