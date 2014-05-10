package synthesijer.hdl;

import java.util.ArrayList;

public class HDLSignal implements HDLTree, HDLExpr{
	
	private final HDLModule module;
	private final String name;
	private final HDLType type;
	private final ResourceKind kind;
	
	private HDLExpr resetValue;
	
	private ArrayList<AssignmentCondition> conditions = new ArrayList<AssignmentCondition>();
	
	public enum ResourceKind{
		REGISTER("reg"), WIRE("wire");
		String sym;
		private ResourceKind(String v){ this.sym = v; }
		public String toString(){ return sym; }
	}
	
	public HDLSignal(HDLModule module, String name, HDLType type, ResourceKind kind){
		this.module = module;
		this.name = name;
		this.type = type;
		this.kind = kind;
		resetValue = type.getDefaultValue(); 
	}
	
	public String getName(){
		return name;
	}
	
	public HDLType getType(){
		return type;
	}
	
	public ResourceKind getKind(){
		return kind;
	}
	
	public boolean isRegister(){
		return kind == ResourceKind.REGISTER;
	}
	
	public HDLModule getModule(){
		return module;
	}
	
	public void setResetValue(HDLExpr s){
		this.resetValue = s;
	}
	
	public HDLExpr getResetValue(){
		return resetValue;
	}
	
	public void setAssign(HDLSequencer.SequencerState s, HDLExpr expr){
		System.out.println(expr);
		AssignmentCondition c = new AssignmentCondition(s, expr);
		conditions.add(c);
	}
	
	public void setAssignCondition(String methodId, String stateKey, String stateId, String phaseKey, String phaseId, HDLExpr value){
		AssignmentCondition c = new AssignmentCondition(methodId, stateKey, stateId, phaseKey, phaseId, value);
		conditions.add(c);
	}

	public void setAssignCondition(String methodId, String stateKey, String stateId, HDLExpr value){
		AssignmentCondition c = new AssignmentCondition(methodId, stateKey, stateId, null, null, value);
		conditions.add(c);
	}
	
	public ArrayList<AssignmentCondition> getConditions(){
		return conditions;
	}
	
	public class AssignmentCondition{
		String methodId;
		String stateKey;
		String stateId;
		String phaseKey;
		String phaseId;
		private final HDLSequencer.SequencerState s;
		private final HDLExpr value;
		
		AssignmentCondition(String methodId, String stateKey, String stateId, String phaseKey, String phaseId, HDLExpr value){
			s = null;
			this.methodId = methodId;
			this.stateKey = stateKey;
			this.stateId = stateId;
			this.phaseKey = phaseKey;
			this.phaseId = phaseId;
			this.value = value;
		}
		
		public AssignmentCondition(HDLSequencer.SequencerState s, HDLExpr value) {
			this.s = s;
			this.value = value;
		}
		
		public String getCondExprAsVHDL(){
//			if(phaseKey != null){
//				return String.format("methodId = %s and %s = %s and %s = %s", methodId, stateKey, stateId, phaseKey, phaseId);
//			}else{
//				return String.format("methodId = %s and %s = %s", methodId, stateKey, stateId);
//			}
			return s.getKey() + " = " + s.getStateId(); 
		}

		public String getCondExprAsVerilogHDL(){
//			if(phaseKey != null){
//				return String.format("methodId == %s && %s == %s && %s == %s", methodId, stateKey, stateId, phaseKey, phaseId);
//			}else{
//				return String.format("methodId == %s && %s == %s", methodId, stateKey, stateId);
//			}
			return s.getKey() + " == " + s.getStateId(); 
		}
		
		public HDLExpr getValue(){
			return value.getResultExpr();
		}
	}

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLSignal(this);
	}

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
		return this;
	}
	
}
