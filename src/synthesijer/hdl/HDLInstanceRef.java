package synthesijer.hdl;

import java.util.ArrayList;
import java.util.Optional;

import synthesijer.SynthesijerUtils;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.hdl.sequencer.SequencerState;

public class HDLInstanceRef implements HDLVariable{

    private final HDLModule module;
    private final String name;
    private final HDLModule target;

    private final ArrayList<HDLPort> ports = new ArrayList<>();

    public HDLInstanceRef(HDLModule m, HDLModule target, String name){
        this.module = m;
        this.name = name;
        this.target = target;

        for(HDLPort p: target.getPorts()){
            if(p != target.getSysClk() && p != target.getSysReset()){
                // add connections for ports except sysClk and sysReset
                HDLPort.DIR d;
                if(p.getDir() == HDLPort.DIR.OUT){
                    d = HDLPort.DIR.IN;
                }else if(p.getDir() == HDLPort.DIR.IN){
                    d = HDLPort.DIR.OUT;
                }else{
                    d = HDLPort.DIR.INOUT;
                }
                ports.add(m.newPort(name + "_" + p.getName(), d, p.getType()));
            }
        }
    }

    @Override
    public String getName(){
        return name;
    }

    public Optional<HDLPort> getPortByPostfix(String key){
        for(HDLPort p: ports){
            if(p.getName().equals(name + key)){
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    @Override
    public void setAssignForSequencer(HDLSequencer s, HDLExpr expr){}

    @Override
    public void setAssignForSequencer(HDLSequencer s, HDLExpr cond, HDLExpr expr){}

    @Override
    public void setAssign(SequencerState s, HDLExpr expr){}

    @Override
    public void setAssign(SequencerState s, int count, HDLExpr expr){}

    @Override
    public void setAssign(SequencerState s, HDLExpr cond, HDLExpr expr){}

    @Override
    public void setResetValue(HDLExpr s){}

    @Override
    public void setDefaultValue(HDLExpr s){}

    @Override
    public String getVHDL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getVerilogHDL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HDLExpr getResultExpr() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HDLSignal[] getSrcSignals() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HDLType getType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void accept(HDLTreeVisitor v) {
        v.visitHDLInstanceRef(this);
    }
}
