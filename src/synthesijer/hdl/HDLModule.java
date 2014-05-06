package synthesijer.hdl;

import java.io.PrintWriter;
import java.util.ArrayList;

public class HDLModule implements SynthesizableObject{
	
	private final String name;
	private final String sysClkName;
	private final String sysResetName;
	
	private ArrayList<HDLPort> ports = new ArrayList<HDLPort>();
	private ArrayList<HDLSignal> signals = new ArrayList<HDLSignal>();
	private ArrayList<HDLSequencer> statemachines = new ArrayList<HDLSequencer>();
	
	public HDLModule(String name, String sysClkName, String sysResetName){
		this.name = name;
		this.sysClkName = sysClkName;
		this.sysResetName = sysResetName;
		ports.add(new HDLPort(sysClkName, HDLPort.DIR.IN, HDLType.genBitType()));
		ports.add(new HDLPort(sysResetName, HDLPort.DIR.IN, HDLType.genBitType()));
	}
	
	public void addPort(HDLPort p){
		ports.add(p);
	}

	public void addSignal(HDLSignal s){
		signals.add(s);
	}

	public String getSysClkName(){
		return sysClkName;
	}

	public String getSysResetName(){
		return sysResetName;
	}
	
	public void addStateMachine(HDLSequencer m){
		statemachines.add(m);
	}

	public void dumpAsVHDL(PrintWriter dest){
		dumpAsVHDL(dest, 0);
	}
	
	public void dumpAsVerilogHDL(PrintWriter dest){
		dumpAsVerilogHDL(dest, 0);
	}
	
	public void dumpAsVHDL(PrintWriter dest, int offset){
		HDLUtils.println(dest, offset, String.format("entity %s is", name));
		HDLUtils.println(dest, offset+2, "port (");
		String sep = "";
		for(HDLPort p: ports){
			dest.print(sep);
			p.dumpAsVHDL(dest, offset+4);
			sep = ";\n";
		}
		HDLUtils.println(dest, offset, "\n  );");
		HDLUtils.println(dest, offset, String.format("end %s;", name));
		HDLUtils.nl(dest);
		HDLUtils.println(dest, offset, String.format("architecture RTL of %s is", name));
		for(HDLSignal s: signals){ s.dumpAsVHDL(dest, offset+2); }
		HDLUtils.nl(dest);
		for(HDLSequencer m: statemachines){ m.genStateDefinitionAsVHDL(dest, offset+2); }
		HDLUtils.println(dest, offset, String.format("begin"));
		HDLUtils.nl(dest);
		for(HDLPort p: ports){ p.assignAsVHDL(dest, offset+2); }
		HDLUtils.nl(dest);
		for(HDLSequencer m: statemachines){ m.genSequencerAsVHDL(dest, offset+2); }
		HDLUtils.nl(dest);
		for(HDLSignal s: signals){ s.dumpAssignProcessAsVHDL(dest, offset+2); }
		HDLUtils.println(dest, offset, String.format("end RTL;"));
	}

	public void dumpAsVerilogHDL(PrintWriter dest, int offset){
		HDLUtils.println(dest, offset, String.format("module %s (", name));
		String sep = "";
		for(HDLPort p: ports){
			dest.print(sep);
			p.dumpAsVerilogHDL(dest, offset+4);
			sep = ",\n";
		}
		HDLUtils.println(dest, offset, "\n);");
		HDLUtils.nl(dest);
		for(HDLSignal s: signals){ s.dumpAsVerilogHDL(dest, offset+2); }
		HDLUtils.nl(dest);
		for(HDLSequencer m: statemachines){ m.genStateDefinitionAsVerilogHDL(dest, offset+2); }
		HDLUtils.nl(dest);
		for(HDLSequencer m: statemachines){ m.genSequencerAsVerilogHDL(dest, offset+2); }
		HDLUtils.nl(dest);
		for(HDLPort p: ports){ p.assignAsVerilogHDL(dest, offset+2); }
		HDLUtils.nl(dest);
		for(HDLSignal s: signals){ s.dumpAssignProcessAsVerilogHDL(dest, offset+2); }
		HDLUtils.println(dest, offset, "endmodule");
	}

}
