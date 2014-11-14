package synthesijer;

public class SynthesijerUtils {
	
	public static void error(String s){
		System.err.println("Error: "+ s);
	}

	public static void warn(String s){
		System.err.println("Warning: " + s);
	}

	public static void info(String s){
		System.err.println("Info: " + s);
	}

	public static String escapeXML(String s){
		s = s.replaceAll("<", "&lt;");
		s = s.replaceAll(">", "&gt;");
		return s;
	}
	
	public static void dump(Object o){
		System.err.printf("%s[%s]\n", o.toString(), o.getClass().toString());
	}

}
