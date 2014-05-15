package synthesijer.model;

public interface StatemachineVisitor {
	
	public void visitStatemachine(Statemachine o);
	
	public void visitState(State o);

}
