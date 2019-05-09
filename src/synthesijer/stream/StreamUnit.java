package synthesijer.stream;

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

}
