package synthesijer.model;


public class GenDataFlowGraphVisitor implements StatemachineVisitor{

	private DataFlowGraph dfg = new DataFlowGraph();
	private final DataFlowGraph parent;
	
	public GenDataFlowGraphVisitor(DataFlowGraph parent){
		this.parent = parent;
	}

	public GenDataFlowGraphVisitor(){
		this(null);
	}
	
	public DataFlowGraph getDataFlowGraph(){
		return dfg;
	}

	@Override
	public void visitStatemachine(Statemachine o) {
		State s = o.getEntryState();
		s.accept(this);
	}
	
	private DataFlowGraph stepIn(DataFlowGraph dfg, State s){
		GenDataFlowGraphVisitor v = new GenDataFlowGraphVisitor(dfg);
		s.accept(v);
		return v.getDataFlowGraph();
	}

	@Override
	public void visitState(State o) {
		DataFlowNode node = null;
		if(parent != null) node = parent.contains(o); // get if added already
		if(node == null) node = new DataFlowNode(o, o.getBody()); // unless added
		for(Transition t: o.getTransitions()){
			if(t.getDestination() != null){
				DataFlowGraph g = stepIn(dfg, t.getDestination());
				if(node != null) g.apply(node);
				dfg.addNodes(g);
			}
		}
		dfg.addNode(node);
	}

}
