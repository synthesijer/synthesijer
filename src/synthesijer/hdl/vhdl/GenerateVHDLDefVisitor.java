package synthesijer.hdl.vhdl;

import java.io.PrintWriter;

import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLIdent;
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
import synthesijer.hdl.literal.HDLSymbol;

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
	public void visitHDLIdent(HDLIdent o) {
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
		HDLUtils.print(dest, offset, String.format("%s : %s %s", o.getName(), o.getDir().getVHDL(), o.getType().getVHDL()));
	}

	@Override
	public void visitHDLSequencer(HDLSequencer o) {
		HDLUtils.println(dest, offset, String.format("type StateType_%s is (", o.getStateKey()));
		String sep = "";
		for(HDLSequencer.SequencerState s: o.getStates()){
			HDLUtils.print(dest, 0, sep);
			HDLUtils.print(dest, offset+2, String.format("%s", s.getStateId()));
			sep = ",\n";
		}
		HDLUtils.println(dest, offset, String.format("\n  );"));
		HDLUtils.println(dest, offset, String.format("signal %s : StateType_%s := %s;", o.getStateKey(), o.getStateKey(), o.getIdleState().getStateId()));
		HDLUtils.nl(dest);
	}

	@Override
	public void visitHDLSignal(HDLSignal o) {
		if(o.getType() instanceof HDLUserDefinedType){
			((HDLUserDefinedType)o.getType()).accept(this);
		}
		HDLUtils.println(dest, offset, String.format("signal %s : %s;", o.getName(), o.getType().getVHDL()));
	}

	@Override
	public void visitHDLType(HDLType o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitHDLUserDefinedType(HDLUserDefinedType o) {
		HDLUtils.println(dest, offset, String.format("type %s is (", o.getName()));
		String sep = "";
		for(HDLSymbol s: o.getItems()){
			HDLUtils.print(dest, 0, sep);
			HDLUtils.print(dest, offset+2, String.format("%s", s.getSymbol()));
				sep = ",\n";
		}
		HDLUtils.println(dest, offset, String.format("\n  );"));
	}

}
