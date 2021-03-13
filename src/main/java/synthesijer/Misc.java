package synthesijer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;

public class Misc{

	private static final String NL = System.getProperty("line.separator");

	public static void genUCF(HDLModule m){
		String s = "";
		for(HDLPort p: m.getPorts()){
			s += "NET " + p.getName() + " LOC = " + p.getPinID();
			String a = p.getIoAttr();
			if(a != null){
				s += " | IOSTANDARD = " + a;
			}
			s += ";" + NL;
		}
		String filename = m.getName() + ".ucf";
		Path path = Paths.get(filename);
		try(BufferedWriter writer = Files.newBufferedWriter(path)){
			writer.write(s);
		}catch(IOException e){
			System.err.println("cannot generate UCF file: " + filename);
		}
	}

	public static void genXDC(HDLModule m){
		String s = "";
		for(HDLPort p: m.getPorts()){
			s += "set_property PACKAGE_PIN " + p.getPinID()  + " [get_ports " + p.getName() + "]" + NL;
			String a = p.getIoAttr();
			if(a != null){
				s += String.format("set_property IOSTANDARD %s [get_ports %s]", p.getIoAttr(), p.getName());
			}
			s += NL;
		}
		String filename = m.getName() + ".xdc";
		Path path = Paths.get(filename);
		try(BufferedWriter writer = Files.newBufferedWriter(path)){
			writer.write(s);
		}catch(IOException e){
			System.err.println("cannot generate XDC file: " + filename);
		}
	}


}
