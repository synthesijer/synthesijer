package synthesijer.ast.opt;

import synthesijer.ast.Module;

public class SSAConverter {
    
    public Module conv(Module m){
        Module newM = new Module(m.getParentScope(), m.getName(), m.getImportTable(), m.getExtending(), m.getImplementingList());
        
        return newM;
    }

}
