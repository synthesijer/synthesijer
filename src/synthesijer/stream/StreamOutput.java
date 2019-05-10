package synthesijer.stream;

import java.io.OutputStream;
import java.io.PrintStream;

public class StreamOutput{

	String name;
	String type;
	
	public StreamOutput(String name){
		this.name = name;
	}

	public void setType(String name){
		this.type = type;
	}

	public void output(PrintStream out){
		out.println("  output: " + "name:" + name + ", type:" + type);
	}

}
