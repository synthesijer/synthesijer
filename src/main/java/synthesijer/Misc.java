package synthesijer;

import synthesijer.hdl.*;

public class Misc{

	private static final String NL = System.getProperty("line.separator");

	public static String genUCF(HDLModule m){
		String s = "";
		for(HDLPort p: m.getPorts()){
			s += "NET " + p.getName() + " LOC = " + p.getPinID();
			String a = p.getIoAttr();
			if(a != null){
				s += " | IOSTANDARD = " + a;
			}
			s += ";" + NL;
		}
		return s;
	}

	public static String genXDC(HDLModule m){
		String s = "";
		for(HDLPort p: m.getPorts()){
			s += "set_property PACKAGE_PIN " + p.getPinID()  + " [get_ports " + p.getName() + "]" + NL;
			String a = p.getIoAttr();
			if(a != null){
				s += String.format("set_property IOSTANDARD %s [get_ports %s]", p.getIoAttr(), p.getName());
			}
			s += NL;
		}
		return s;
	}


}
