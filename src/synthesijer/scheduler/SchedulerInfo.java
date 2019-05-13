package synthesijer.scheduler;

import java.util.ArrayList;
import synthesijer.ast.Module;

public class SchedulerInfo {

	private ArrayList<SchedulerBoard> boardsList = new ArrayList<>();

	private final String name;

	private final Module module;

	//private final ArrayList<VariableOperand> varList;
	private final ArrayList<Operand> varList;

	public SchedulerInfo(String name, Module module){
		this.name = name;
		this.module = module;
		varList = new ArrayList<>();
	}

	private SchedulerInfo(SchedulerInfo i){
		this.name = i.name;
		this.module = i.module;
		varList = i.varList;
	}

	public SchedulerInfo getSameInfo(){
		return new SchedulerInfo(this);
	}

	public String getName(){
		return name;
	}

	public Module getModule(){
		return module;
	}

	public SchedulerBoard[] getBoardsList(){
		return boardsList.toArray(new SchedulerBoard[]{});
	}

	@SuppressWarnings("unchecked")
	//public ArrayList<VariableOperand>[] getVarTableList(){
	public ArrayList<Operand>[] getVarTableList(){
		//ArrayList<ArrayList<VariableOperand>> ret = new ArrayList<>();
		ArrayList<ArrayList<Operand>> ret = new ArrayList<>();
		ret.add(varList);
		for(SchedulerBoard b: boardsList){
			//for(ArrayList<VariableOperand> va : b.getVarTableList()){
			//	ret.add(va);
			//}
			ret.add(b.getVarList());
		}
		return ret.toArray(new ArrayList[]{});
	}

	public void addBoard(SchedulerBoard b){
		boardsList.add(b);
	}

	public void addOperand(Operand o){
		varList.add(o);
	}

//	public void addModuleVarList(ArrayList<VariableOperand> t){
//		if(varList != null){
//			SynthesijerUtils.warn("DUPLICATE addModuleVarTable:" + name);
//		}
//		this.varList = t;
//	}

	//public ArrayList<VariableOperand> getModuleVarList(){
	public ArrayList<Operand> getModuleVarList(){
		return varList;
	}

}
