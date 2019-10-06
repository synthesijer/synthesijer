package synthesijer.stream;

import java.io.OutputStream;
import java.io.PrintStream;

public class StreamInput{

	String name;

	public StreamInput(String name){
		this.name = name;
	}

	public void output(PrintStream out){
		out.println("  input: " + name);
	}

}
