package synthesijer.tools.xilinx;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSignalBinding;
import synthesijer.hdl.HDLType;

public class HDLModuleToComponentXML {

	private static String dir(HDLPort.DIR d){
		switch(d){
			case IN:
				return "in";
			case OUT:
				return "out";
			case INOUT:
				return "inout";
			default:
				throw new RuntimeException("Unknown port DIR:" + d);
		}
	}

	private static String type(HDLType type){
		if(type.isBit()){
			return "std_logic";
		}else if(type.isInteger()){
			return "integer";
		}else if(type.isSigned()){
			return "signed";
		}else{
			return "std_logic_vector";
		}
	}

	public static void listOfRequires(HDLModule m, ArrayList<HDLModule> list){
		if(list.contains(m)) return;
		list.add(m);
		if(m.getModuleInstances().length == 0) return;
		for(HDLInstance inst: m.getModuleInstances()){
			listOfRequires(inst.getSubModule(), list);
		}
	}

	public static ArrayList<HDLModule> listOfRequires(HDLModule m){
		ArrayList<HDLModule> list = new ArrayList<>();
		for(HDLInstance inst: m.getModuleInstances()){
			listOfRequires(inst.getSubModule(), list);
		}
		return list;
	}

	public static void conv(HDLModule module, String[] libs, String vendor, String lib){
		ArrayList<PortInfo> list = new ArrayList<>();
		HDLPort[] ports = module.getPorts();
		for(HDLPort p: ports){
			PortInfo info;
			if(p.getType().isVector()){
				int width = ((HDLPrimitiveType)p.getType()).getWidth();
				info = new PortInfo(p.getName(), dir(p.getDir()), type(p.getType()), true, width-1, 0);
			}else{
				info = new PortInfo(p.getName(), dir(p.getDir()), type(p.getType()));
			}
			list.add(info);
		}
		ArrayList<String> src = new ArrayList<>();
		if(libs != null){
			for(String s: libs) src.add(s);
		}
		ArrayList<HDLModule> requires = listOfRequires(module);

		for(HDLModule m: requires){
			src.add("src/" + m.getName() + ".vhd");
		}
		src.add("src/" + module.getName() + ".vhd");

		ArrayList<HDLSignalBinding> bindings = new ArrayList<>();
		for(HDLPort p: module.getPorts()){
			if(p.isBinded()){
				HDLSignalBinding b = p.getSignalBinding();
				if(bindings.contains(b) == false) bindings.add(b);
			}
		}

		GenComponentXML o = new GenComponentXML(vendor, lib, module.getName(), 1, 0, list.toArray(new PortInfo[0]), src.toArray(new String[0]), bindings.toArray(new HDLSignalBinding[0]));

		File basedir = new File(o.getCoreUniqName());
		basedir.mkdir();

		File file = new File(basedir, "component.xml");
		o.write(file);

		File srcdir = new File(basedir, "src");
		srcdir.mkdir();

		File xguidir = new File(basedir, "xgui");
		xguidir.mkdir();
		try(PrintStream xgui = new PrintStream(new File(xguidir, o.getCoreUniqName() + ".tcl"))){
			xgui.println("# Definitional proc to organize widgets for parameters.");
			xgui.println("proc init_gui { IPINST } {");
			xgui.println("	set Page0 [ipgui::add_page $IPINST -name \"Page 0\" -layout vertical]");
			xgui.println("	set Component_Name [ipgui::add_param $IPINST -parent $Page0 -name Component_Name]");
			xgui.println("}");
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

}
