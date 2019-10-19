package synthesijer.hdl.vhdl;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import synthesijer.scala.vhdl.*;

import net.wasamon.mjlib.util.GetOpt;

public class VHDLLoader{

	public static void main(String... args) throws IOException{
		GetOpt opt = new GetOpt("",	"check,error-only", args);
		
		Path path = Paths.get(opt.getArgs()[0]);
		byte[] bcontent = Files.readAllBytes(path);
		String content = new String(bcontent);

		if (opt.flag("check")){
			System.out.println(path);
		}

		VHDLParser obj = new VHDLParser();
		var result = obj.parse(content);
		
		if (opt.flag("check")){
			if(result.isEmpty()){
				System.out.println("..." + result);
			}else if(opt.flag("error-only") == false){
				System.out.println("..." + "OK");
			}
		}else{
			System.out.println(result);
		}

	}

}
