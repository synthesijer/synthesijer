package synthesijer.hdl.vhdl;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import synthesijer.scala.vhdl.*;

public class VHDLLoader{

	public static void main(String... args) throws IOException{
		VHDLParser obj = new VHDLParser();

		Path path = Paths.get(args[0]);
		String content = Files.readString(path);
		
		System.out.println(obj.parse(content));
	}
	
}
