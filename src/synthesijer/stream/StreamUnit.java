package synthesijer.stream;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class StreamUnit{

	String name;
	ArrayList<StreamInput> inputs = new ArrayList<>();
	ArrayList<StreamOutput> outputs = new ArrayList<>();

	public StreamUnit(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void output(PrintStream out){
		for(var v: inputs){
			v.output(out);
		}
		for(var v: outputs){
			v.output(out);
		}
	}

}
