package synthesijer.model;

import synthesijer.ast.Module;

public class StatemachineOptimizer {

	private final Module module;
	
	public StatemachineOptimizer(Module m) {
		this.module  = m;
	}

	public void optimize(){
		GenDataFlowGraphVisitor v = new GenDataFlowGraphVisitor();
		module.accept(v);
	}

	

}
