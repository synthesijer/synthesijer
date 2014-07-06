package synthesijer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Main {
	
	public static void main(String... args) throws FileNotFoundException{
		openjdk.com.sun.tools.javac.main.Main compiler = new openjdk.com.sun.tools.javac.main.Main("javac", new PrintWriter(System.err, true));
		int err = compiler.compile(args);
		if(err == 0){
			dump("dump000.xml");
			Manager.INSTANCE.preprocess();
			dump("dump001.xml");
			Manager.INSTANCE.generate();
		}
		System.exit(err);
	}
	
	public static void dump(String destName) throws FileNotFoundException{
		PrintWriter dest = new PrintWriter(new FileOutputStream(destName), true);
		Manager.INSTANCE.dumpAsXML(dest);
		dest.close();
	}

}
