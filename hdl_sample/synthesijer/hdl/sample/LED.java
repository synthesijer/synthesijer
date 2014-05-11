package synthesijer.hdl.sample;

import java.io.IOException;

import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLType;
import synthesijer.hdl.HDLUtils;
import synthesijer.hdl.literal.HDLConstant;
import synthesijer.hdl.literal.HDLValue;

public class LED {
	
	private static HDLModule genLED(){
		HDLModule m = new HDLModule("led", "clk", "reset");
		HDLPort q = m.newPort("q", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		
		// q <= counter(24)
		HDLSignal sig = q.getSrcSignal();
		HDLSignal counter = m.newSignal("counter", HDLPrimitiveType.genSignedType(32));
		sig.setAssign(null, m.newExpr(HDLOp.REF, counter, 5));
		
		// at main state, counter <= counter + 1
		HDLSequencer seq = m.newSequencer("main");
		HDLSequencer.SequencerState ss = seq.getIdleState();
		counter.setAssign(ss, m.newExpr(HDLOp.ADD, counter, 1));
		
		HDLUtils.generate(m, HDLUtils.VHDL);
		HDLUtils.generate(m, HDLUtils.Verilog);
		return m;
	}

	public static void main(String... args) throws IOException{
		
		HDLModule led = genLED();
		
		HDLModule m = new HDLModule("led_sim");
		HDLInstance inst = m.newModuleInstance(led, "U");
		HDLSignal clk = m.newSignal("clk", HDLPrimitiveType.genBitType());
		HDLSignal reset = m.newSignal("reset", HDLPrimitiveType.genBitType());
		
/*
		  process
		  begin
		    clk <= '1'; wait for STEP/2;
		    clk <= '0'; reset <= '1'; wait for STEP/2;
		    clk <= '1'; wait for STEP/2;
		    clk <= '0'; wait for STEP/2;
		    clk <= '1'; wait for STEP/2;
		    clk <= '0'; wait for STEP/2;
		    clk <= '1'; reset <= '0'; wait for STEP/2;
		    clk <= '0'; wait for STEP/2;
		    clk <= '1'; wait for STEP/2;
		    clk <= '0'; wait for STEP/2;
		    while true loop
		      clk <= '1'; wait for STEP/2;
		      clk <= '0'; wait for STEP/2;
		    end loop;
		  end process;
*/

		HDLSequencer seq = m.newSequencer("main");
		seq.setTransitionTime(10);
		
		HDLSequencer.SequencerState ss = seq.getIdleState();
		for(int i = 0; i < 5; i++){
			HDLSequencer.SequencerState s0 = seq.addSequencerState("S_" + (2*i));
			ss.addStateTransit(s0);
			clk.setAssign(s0, HDLConstant.HIGH);
			if(i == 3) reset.setAssign(s0, HDLConstant.LOW);
			HDLSequencer.SequencerState s1 = seq.addSequencerState("S_" + (2*i+1));
			s0.addStateTransit(s1);
			clk.setAssign(s1, HDLConstant.LOW);
			if(i == 0) reset.setAssign(s1, HDLConstant.HIGH);
			ss = s1;
		}

		
		
		HDLUtils.generate(m, HDLUtils.VHDL);
				
	}
	
}
