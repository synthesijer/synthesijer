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
import synthesijer.hdl.HDLType;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitHDLInstance(HDLInstance o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitHDLLitral(HDLLiteral o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitHDLModule(HDLModule o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitHDLPort(HDLPort o) {
		HDLUtils.print(dest, offset, String.format("%s %s %s", o.getDir().getVerilogHDL(), o.getType().getVerilogHDL(), o.getName()));
	}

	@Override
	public void visitHDLSequencer(HDLSequencer o) {
		for(int i = 0; i < o.getStates().size(); i++){
			HDLUtils.println(dest, offset, String.format("parameter %s = 32'd%d;", o.getStates().get(i).getStateId(), i));
		}
		HDLUtils.println(dest, offset, String.format("reg [31:0] %s;", o.getStateKey()));
		HDLUtils.nl(dest);
	}

	@Override
	public void visitHDLSignal(HDLSignal o) {
		if(o.getType() instanceof HDLUserDefinedType){
			((HDLUserDefinedType) o.getType()).accept(this);
		}
		HDLUtils.println(dest, offset, String.format("%s %s %s;", o.getKind().toString(), o.getType().getVerilogHDL(), o.getName()));
	}

	@Override
	public void visitHDLType(HDLType o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitHDLUserDefinedType(HDLUserDefinedType o) {
		for(int i = 0; i < o.getItems().length; i++){
			HDLUtils.println(dest, offset, String.format("parameter %s = 32'd%d;", o.getItems()[i].getSymbol(), i));
		}
	}

}
