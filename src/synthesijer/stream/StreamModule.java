package synthesijer.stream;

import java.io.PrintStream;

public class StreamModule{

	private String name;

	public StreamModule(String name){
		this.name = name;
	}

	public void otuput(PrintStream out){
		out.println("name");
	}
	
	
}
