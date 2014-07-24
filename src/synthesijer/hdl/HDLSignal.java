package synthesijer.hdl;

import java.util.ArrayList;

public class HDLSignal implements HDLTree, HDLExpr, HDLVariable, HDLPortPairItem{
	
	private final HDLModule module;
	private final String name;
	private final HDLType type;
	private ResourceKind kind;
	
	private HDLExpr resetValue;
	private HDLExpr defaultValue;
	
	private ArrayList<AssignmentCondition> conditions = new ArrayList<>();
	
	private boolean assignAlwaysFlag;
	private HDLExpr assignAlwaysExpr;
	
	public enum ResourceKind{
		REGISTER("reg"), WIRE("wire");
		String sym;
		private ResourceKind(String v){ this.sym = v; }
		public String toString(){ return sym; }
	}
	
	HDLSignal(HDLModule module, String name, HDLType type, ResourceKind kind){
		this.module = module;
		this.name = name;
		this.type = type;
		this.kind = kind;
		defaultValue = null;
		assignAlwaysFlag = false;
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
	
	@Override
	public void setResetValue(HDLExpr s){
		this.resetValue = s;
	}

	@Override
	public void setDefaultValue(HDLExpr s){
		this.defaultValue = s;
	}

	public HDLExpr getResetValue(){
		if(resetValue != null){
			return resetValue;
		}else{
			return type.getDefaultValue();
		}
	}

	public boolean hasDefaultValue(){
		return defaultValue != null;
	}

	public HDLExpr getDefaultValue(){
		return defaultValue;
	}
	
	public String toString(){
		return String.format("HDLSignal:: name=%s, type=%s, kind=%s", name, type, kind);
	}

	@Override
	public void setAssign(HDLSequencer.SequencerState s, HDLExpr expr){
		if(s != null){
			AssignmentCondition c = new AssignmentCondition(s, expr);
			conditions.add(c);
		}else{
			kind = ResourceKind.WIRE; // change resource kind to allow using "assign" statement
			assignAlwaysFlag = true;
			assignAlwaysExpr = expr;
		}
	}

	@Override
	public void setAssign(HDLSequencer.SequencerState s, int counter, HDLExpr expr){
		if(s != null){
			AssignmentCondition c = new AssignmentCondition(s, counter, expr);
			conditions.add(c);
		}else{
			kind = ResourceKind.WIRE; // change resource kind to allow using "assign" statement
			assignAlwaysFlag = true;
			assignAlwaysExpr = expr;
		}
	}

	public boolean isAssignAlways(){
		return assignAlwaysFlag;
	}
	
	public HDLExpr getAssignAlwaysExpr(){
		return assignAlwaysExpr;
	}
	
	public AssignmentCondition[] getConditions(){
		return conditions.toArray(new AssignmentCondition[]{});
	}
	
	@Override
	public HDLSignal[] getSrcSignals(){
		ArrayList<HDLSignal> list = new ArrayList<>();
		for(AssignmentCondition c: conditions){
			if(!list.contains(c.getStateKey())){
				list.add(c.getStateKey());
			}
			if(c.getValue().getSrcSignals() != null){
				for(HDLSignal s: c.getValue().getSrcSignals()){ list.add(s); }
			}
		}
		return list.toArray(new HDLSignal[]{});
	}
	
	public class AssignmentCondition{
		private final HDLSequencer.SequencerState s;
		private final HDLExpr value;
		private final int count;
		
		public AssignmentCondition(HDLSequencer.SequencerState s, HDLExpr value) {
			this(s, -1, value);
		}

		public AssignmentCondition(HDLSequencer.SequencerState s, int count, HDLExpr value) {
			this.s = s;
			this.value = value;
			this.count = count;
		}

		public String getCondExprAsVHDL(){
			if(count < 0){
				String c = String.format("%s = %s", s.getKey().getName(), s.getStateId().getValue());
				String ext = s.getExitConditionAsVHDL();
				if(!ext.equals("")) c += " and " + ext;
				return c;
			}else{
				return String.format("%s = %s and %s = %d", s.getKey().getName(), s.getStateId().getValue(), s.getSequencer().getDelayCounter().getName(), count);
			}
		}

		public String getCondExprAsVerilogHDL(){
			if(count < 0){
				String c = String.format("%s == %s", s.getKey().getName(), s.getStateId().getValue());
				String ext = s.getExitConditionAsVerilogHDL();
				if(!ext.equals("")) c += " && " + ext;
				return c;
			}else{
				return String.format("%s == %s && %s == %d", s.getKey().getName(), s.getStateId().getValue(), s.getSequencer().getDelayCounter().getName(), count);
			}
		}
		
		public HDLExpr getValue(){
			return value.getResultExpr();
		}
		
		public HDLSignal getStateKey(){
			return s.getKey();
		}
		
		
	}

	@Override
	public void accept(HDLTreeVisitor v) {
		v.visitHDLSignal(this);
	}

	@Override
	public String getVHDL() {
		return name;
	}

	@Override
	public String getVerilogHDL() {
		return name;
	}

	@Override
	public HDLExpr getResultExpr() {
		return this;
	}
	
}
