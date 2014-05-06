package synthesijer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Main {

	public static void main(String... args) throws FileNotFoundException{
		openjdk.com.sun.tools.javac.main.Main compiler = new openjdk.com.sun.tools.javac.main.Main("javac", new PrintWriter(System.err, true));
		int err = compiler.compile(args);
		if(err == 0){
			PrintWriter dest = new PrintWriter(new FileOutputStream("dump.xml"), true);
			Manager.INSTANCE.dumpAsXML(dest);
			dest.close();
			Manager.INSTANCE.generate();
		}
		System.exit(err);
	}

}
