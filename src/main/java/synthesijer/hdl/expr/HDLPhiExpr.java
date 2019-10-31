package synthesijer.hdl.expr;

import java.util.ArrayList;

import synthesijer.SynthesijerUtils;
import synthesijer.hdl.HDLExpr;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLOp;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLTreeVisitor;
import synthesijer.hdl.HDLType;
import synthesijer.hdl.sequencer.SequencerState;

public class HDLPhiExpr implements HDLExpr {

    private final int uid;
    private final HDLOp op;
    private final HDLExpr[] args;
	private final SequencerState[] patterns;

    private final HDLSignal result;

    public HDLPhiExpr(HDLModule m, int uid, HDLOp op, HDLExpr... args) {
        this.uid = uid;
        this.op = op;
        this.args = args;
		this.patterns = new SequencerState[args.length];
        for (HDLExpr expr : args) {
            if (expr == null)
                throw new RuntimeException("An argument of HDLCombinationExpr is null.");
        }
        HDLType type = args[0].getType();
        result = m.newSignal(String.format("tmp_%04d", uid), type, HDLSignal.ResourceKind.WIRE, this, true);
    }
	
    @Override
    public void accept(HDLTreeVisitor v) {
        v.visitHDLExpr(this);
    }

	public void setStatePatterns(SequencerState[] ss){
		for(int i = 0; i < patterns.length; i++){
			this.patterns[i] = ss[i];
		}
	}

    public String toString() {
        return "HDLPhiExpr::(" + op + " " + getArgsString(args) + ")";
    }

    private String getArgsString(HDLExpr[] args) {
        String s = "";
        for (HDLExpr a : args) {
            s += a.toString() + " ";
        }
        return s;
    }
	
	/**
	 *
	 * @return expression in VHDL style
	 */
    @Override
	public String getVHDL(){
		String s = "";
		for(int i = 0; i < args.length; i++){
			if(patterns[i] == null) continue;
			s += args[i].getVHDL();
			s += " when ";
			s += patterns[i].getSequencer().getPrevStateKey().getVHDL();
			s += " = ";
			s += patterns[i].getStateId().getVHDL();
			s += " else ";
		}
		return s;
	}

	/**
	 *
	 * @return expression in Verilog-HDL style
	 */
    @Override
	public String getVerilogHDL(){
		String s = "";
		for(int i = 0; i < args.length; i++){
			if(patterns[i] == null) continue;
			s += patterns[i].getSequencer().getPrevStateKey().getVerilogHDL();
			s += " == ";
			s += patterns[i].getStateId().getVHDL();
			s += " ? ";
			s += args[i].getVerilogHDL();
			s += " : ";
		}
		return s;
	}

	/**
	 *
	 * @return result expression
	 */
    @Override
	public HDLExpr getResultExpr(){
		return result;
	}

	/**
	 *
	 * @return result type of this expression
	 */
	@Override
	public HDLType getType(){
		return result.getType();
	}

    private void getSrcSignals(ArrayList<HDLSignal> list, HDLExpr arg) {
        HDLSignal[] src = arg.getSrcSignals();
        if (src != null) {
            for (HDLSignal s : src) {
                list.add(s);
            }
        }
        if (arg.getResultExpr() instanceof HDLSignal) {
            list.add((HDLSignal) arg.getResultExpr());
        }
    }

	/**
	 *
	 * @return an array of source signals of this expression
	 */
	@Override
	public HDLSignal[] getSrcSignals(){
        ArrayList<HDLSignal> list = new ArrayList<>();
        for (HDLExpr arg : args) {
            getSrcSignals(list, arg);
        }
        return list.toArray(new HDLSignal[] {});
	}

}
