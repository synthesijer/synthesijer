package synthesijer.model;

import java.util.ArrayList;

import synthesijer.ast.Variable;
import synthesijer.ast.statement.ExprContainStatement;

public class DataFlowNode {
	
	public final State state;
	public final ExprContainStatement stmt;
	
	private boolean flagScheduled = false;
	
	private final ArrayList<DataFlowNode> pred = new ArrayList<>();
	private final ArrayList<DataFlowNode> succ = new ArrayList<>();
	
	private final static boolean DUMP = false;
	
	public DataFlowNode(State state, ExprContainStatement stmt){
		this.state = state;
		this.stmt = stmt;
		if(DUMP){
			System.out.println("::" + stmt.getExpr() + "@" + stmt.getClass());
			for(Variable v: stmt.getSrcVariables()){
				System.out.println("   <- " + v);
			}
			for(Variable v: stmt.getDestVariables()){
				System.out.println("   -> " + v);
			}
		}
	}
	
	public void addPred(DataFlowNode n){
		pred.add(n);
	}
	
	public void addSucc(DataFlowNode n){
		succ.add(n);
	}
	
	public boolean isScheduled(){
		return flagScheduled;
	}
		
	public void setScheduled(){
		flagScheduled = true;
	}

	public boolean isReady(){
		if(state.isTerminate()) return false;
		boolean ready = true;
		for(DataFlowNode p: pred){
			ready &= p.isScheduled();
		}
		return ready;
	}

}
