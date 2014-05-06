package synthesijer.ast;

import java.io.PrintWriter;

import synthesijer.hdl.HDLType;


public interface Type {
	
	public void dumpAsXML(PrintWriter dest);
	
	public HDLType getHDLType();
	
}
