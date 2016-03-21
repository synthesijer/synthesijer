package synthesijer.hdl;

public class HDLSimModule extends HDLModule{
    
    private HDLSignal sysClk; 
    private HDLSignal sysReset;

    public HDLSimModule(String name){
		super(name);
    }
    
    public void setSysClk(HDLSignal s){
		sysClk = s;
    }
    
    public void setSysReset(HDLSignal s){
		sysReset = s;
    }

    @Override
    public HDLPortPairItem getSysClkPairItem(){
		return sysClk;
    }
    
    @Override
    public HDLPortPairItem getSysResetPairItem(){
		return sysReset;
    }

}
