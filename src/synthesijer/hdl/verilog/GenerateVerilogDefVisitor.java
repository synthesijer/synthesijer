package synthesijer.hdl.verilog;

import java.io.PrintWriter;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLLiteral;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLTreeVisitor;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLUserDefinedType;
import synthesijer.hdl.HDLUtils;

public class GenerateVerilogDefVisitor implements HDLTreeVisitor{

	private final PrintWriter dest;
	private final int offset;
	
	public GenerateVerilogDefVisitor(PrintWriter dest, int offset){
		this.dest = dest;
		this.offset = offset;
	}

	@Override
	public void visitHDLExpr(HDLExpr o) {
	}

	@Override
	public void visitHDLInstance(HDLInstance o) {
	}

	@Override
	public void visitHDLLitral(HDLLiteral o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitHDLModule(HDLModule o) {
		for(HDLPort p: o.getPorts()){
			if(p.isOutput()) p.getSrcSignal().accept(new GenerateVerilogDefVisitor(dest, offset));
		}
		HDLUtils.nl(dest);
		for(HDLSignal s: o.getSignals()){ s.accept(new GenerateVerilogDefVisitor(dest, offset)); }
		HDLUtils.nl(dest);
		for(HDLSequencer m: o.getSequencers()){ m.accept(new GenerateVerilogDefVisitor(dest, offset)); }
		HDLUtils.nl(dest);
	}

	@Override
	public void visitHDLPort(HDLPort o) {
		HDLUtils.print(dest, offset, String.format("%s %s %s", o.getDir().getVerilogHDL(), o.getType().getVerilogHDL(), o.getName()));
	}

	@Override
	public void visitHDLSequencer(HDLSequencer o) {
	}

	@Override
	public void visitHDLSignal(HDLSignal o) {
		if(o.getType() instanceof HDLUserDefinedType){
			((HDLUserDefinedType) o.getType()).accept(this);
		}
		String s;
		if(o.getResetValue() != null && o.isRegister()){
			s = String.format("%s %s %s = %s;", o.getKind().toString(), o.getType().getVerilogHDL(), o.getName(), o.getResetValue().getVerilogHDL());
		}else{
			s = String.format("%s %s %s;", o.getKind().toString(), o.getType().getVerilogHDL(), o.getName());
		}
		HDLUtils.println(dest, offset, s);
	}

	@Override
	public void visitHDLType(HDLPrimitiveType o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitHDLUserDefinedType(HDLUserDefinedType o) {
		for(int i = 0; i < o.getItems().length; i++){
			HDLUtils.println(dest, offset, String.format("parameter %s = 32'd%d;", o.getItems()[i].getVerilogHDL(), i));
		}
	}

}
