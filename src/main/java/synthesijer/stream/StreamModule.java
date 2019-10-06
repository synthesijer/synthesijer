package synthesijer.stream;

import java.io.PrintStream;
import java.util.ArrayList;

public class StreamModule{

	private String name;

	ArrayList<StreamUnit> units = new ArrayList<>();


	public StreamModule(String name)
	{
		this.name = name;
	}

	public void output(PrintStream out)
	{
		out.println("name: " + name);
		for(var u: units){
			u.output(out);
		}
	}

}
