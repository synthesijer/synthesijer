package synthesijer.hdl.vhdl;

import java.io.PrintWriter;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLLiteral;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
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
		genPortList(dest, offset+2, o.getSubModule().getPorts());
		HDLUtils.println(dest, offset, String.format("end component %s;", o.getSubModule().getName()));
	}

	@Override
	public void visitHDLLitral(HDLLiteral o) {
		// TODO Auto-generated method stub
		
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
		for(HDLInstance i: o.getModuleInstances()){ i.accept(new GenerateVHDLDefVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		for(HDLPort p: o.getPorts()){
			if(p.isOutput()) p.getSrcSignal().accept(new GenerateVHDLDefVisitor(dest, offset+2));
		}
		HDLUtils.nl(dest);
		for(HDLSignal s: o.getSignals()){ s.accept(new GenerateVHDLDefVisitor(dest, offset+2)); }
		HDLUtils.nl(dest);
		
	}

	@Override
	public void visitHDLPort(HDLPort o) {
		HDLUtils.print(dest, offset, String.format("%s : %s %s", o.getName(), o.getDir().getVHDL(), o.getType().getVHDL()));
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
