package synthesijer.jcfrontend;

import java.util.ArrayList;
import java.util.Hashtable;

public class SourceInfo{

	public String className;

	public boolean isAnnotation = false;
	public boolean isInterface = false;
	public boolean isSynthesijerHDL = false;
	
	public String extending = null;
	public ArrayList<String> implementing = new ArrayList<>();
	public Hashtable<String, String> importTable = new Hashtable<>();
	
}
