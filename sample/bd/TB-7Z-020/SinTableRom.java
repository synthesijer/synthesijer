
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.sequencer.SequencerState;

public class SinTableRom extends HDLModule{

    public int[] sintable;
    
	public SinTableRom(String... args){
		super("sintable_rom", "clk", "reset");
		HDLUtils.genInputPort(this, "sintable_address", 32);
		HDLUtils.genInputPort(this, "sintable_din", 32);
		HDLUtils.genOutputPort(this, "sintable_dout", 32);
		HDLUtils.genOutputPort(this, "sintable_length", 32);
		HDLUtils.genInputPort(this, "sintable_we");
		HDLUtils.genInputPort(this, "sintable_oe");
	}
	
}
