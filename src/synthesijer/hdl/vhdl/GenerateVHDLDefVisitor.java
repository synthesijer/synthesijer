package synthesijer.hdl.vhdl;

import java.io.PrintWriter;
import java.util.Hashtable;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLLiteral;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLParameter;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPort.OPTION;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLTreeVisitor;
import synthesijer.hdl.HDLUserDefinedType;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLValue;

public class GenerateVHDLDefVisitor implements HDLTreeVisitor{
	
	private PrintWriter dest;
	private int offset;
	
	public GenerateVHDLDefVisitor(PrintWriter dest, int offset){
		this.dest = dest;
		this.offset = offset;
	}

	@Override
	public void visitHDLExpr(HDLExpr o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitHDLInstance(HDLInstance o) {
		HDLUtils.println(dest, offset, String.format("component %s", o.getSubModule().getName()));
		if(o.getSubModule().getParameters().length > 0){
			genGenericList(dest, offset+2, o.getSubModule().getParameters());
		}
		genPortList(dest, offset+2, o.getSubModule().getPorts());
		HDLUtils.println(dest, offset, String.format("end component %s;", o.getSubModule().getName()));
	}

	@Override
	public void visitHDLLitral(HDLLiteral o) {
		// TODO Auto-generated method stub
		
	}
	
	private void genGenericList(PrintWriter dest, int offset, HDLParameter[] params){
		HDLUtils.println(dest, offset, "generic (");
		String sep = "";
		for(HDLParameter p: params){
			dest.print(sep);
			p.accept(new GenerateVHDLDefVisitor(dest, offset+2));
			sep = ";\n";
		}
		HDLUtils.println(dest, 0, "");
		HDLUtils.println(dest, offset, ");");
	}

	private void genPortList(PrintWriter dest, int offset, HDLPort[] ports){
		HDLUtils.println(dest, offset, "port (");
		String sep = "";
		for(HDLPort p: ports){
			dest.print(sep);
			p.accept(new GenerateVHDLDefVisitor(dest, offset+2));
			sep = ";\n";
		}
		HDLUtils.println(dest, 0, "");
		HDLUtils.println(dest, offset, ");");
	}

	@Override
	public void visitHDLModule(HDLModule o) {

		HDLUtils.println(dest, offset, String.format("entity %s is", o.getName()));
		if(o.getPorts().length > 0){
			genPortList(dest, offset+2, o.getPorts());
		}
		HDLUtils.println(dest, offset, String.format("end %s;", o.getName()));
		HDLUtils.nl(dest);
		
		// architecture
		HDLUtils.println(dest, offset, String.format("architecture RTL of %s is", o.getName()));
		HDLUtils.nl(dest);
		
		Hashtable<String, Boolean> componentFlags = new Hashtable<>();
		for(HDLInstance i: o.getModuleInstances()){
			if(componentFlags.containsKey(i.getSubModule().getName())) continue; // already			
			i.accept(new GenerateVHDLDefVisitor(dest, offset+2));
			System.out.println(i.getSubModule().getName());
			componentFlags.put(i.getSubModule().getName(), true);
		}
		
		HDLUtils.nl(dest);
		for(HDLPort p: o.getPorts()){
			if(p.isSet(OPTION.NO_SIG) == false){
				p.getSignal().accept(new GenerateVHDLDefVisitor(dest, offset+2));
			}
		}
		HDLUtils.nl(dest);
		for(HDLSignal s: o.getSignals()){ s.accept(new GenerateVHDLDefVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		
	}

	@Override
	public void visitHDLPort(HDLPort o) {
		//System.out.println(o);
		HDLUtils.print(dest, offset, String.format("%s : %s %s", o.getName(), o.getDir().getVHDL(), o.getType().getVHDL()));
	}

	@Override
	public void visitHDLParameter(HDLParameter o) {
		HDLUtils.print(dest, offset, String.format("%s : %s := %s", o.getName(), o.getType().getVHDL(), o.getDefaultValue()));
	}

	@Override
	public void visitHDLSequencer(HDLSequencer o) {
	}

	@Override
	public void visitHDLSignal(HDLSignal o) {
		if(o.getType() instanceof HDLUserDefinedType){
			((HDLUserDefinedType)o.getType()).accept(this);
		}
		String s;
		if(o.getResetValue() != null && o.isRegister()){
			s = String.format("signal %s : %s := %s;", o.getName(), o.getType().getVHDL(), o.getResetValue().getVHDL());
		}else{
			s = String.format("signal %s : %s;", o.getName(), o.getType().getVHDL());
		}
		HDLUtils.println(dest, offset, s);
	}

	@Override
	public void visitHDLType(HDLPrimitiveType o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitHDLUserDefinedType(HDLUserDefinedType o) {
		HDLUtils.println(dest, offset, String.format("type %s is (", o.getName()));
		String sep = "";
		for(HDLValue s: o.getItems()){
			HDLUtils.print(dest, 0, sep);
			HDLUtils.print(dest, offset+2, String.format("%s", s.getVHDL()));
			sep = ",\n";
		}
		HDLUtils.println(dest, offset, String.format("\n  );"));
	}

}
