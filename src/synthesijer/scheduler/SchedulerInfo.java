package synthesijer.scheduler;

import java.util.ArrayList;
import java.util.Hashtable;

public class SchedulerInfo {
	
	private ArrayList<SchedulerBoard> boardsList = new ArrayList<>();
	
	private ArrayList<Hashtable<String, VariableOperand>> varTableList = new ArrayList<>();
	
	private final String name;
	
	public SchedulerInfo(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public SchedulerBoard[] getBoardsList(){
		return boardsList.toArray(new SchedulerBoard[]{});
	}

	@SuppressWarnings("unchecked")
	public Hashtable<String, VariableOperand>[] getVarTableList(){
		return varTableList.toArray(new Hashtable[]{});
	}

	public void addBoard(SchedulerBoard b){
		boardsList.add(b);
	}

	public void addVarTable(Hashtable<String, VariableOperand> t){
		varTableList.add(t);
	}
		
	
}
