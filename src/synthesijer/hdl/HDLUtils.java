package synthesijer.hdl;

import java.io.PrintWriter;

public class HDLUtils {
	
	public static void println(PrintWriter dest, int offset, String str){
		dest.println(pad(offset) + str);
	}
	
	public static void print(PrintWriter dest, int offset, String str){
		dest.print(pad(offset) + str);
	}

	public static void nl(PrintWriter dest){
		dest.println();
	}
	
	private static String pad(int offset){
		String s = "";
		for(int i = 0; i < offset; i++){
			s += " ";
		}
		return s;
	}

}
