package synthesijer.hdl;

import java.util.ArrayList;

import synthesijer.hdl.sequencer.SequencerState;

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
	
	private final HDLExpr equivExpr;
	private final boolean equivFlag;
	
	private boolean ignoreFlag = false; 
	
	public enum ResourceKind{
		REGISTER("reg"), WIRE("wire");
		String sym;
		private ResourceKind(String v){ this.sym = v; }
		public String toString(){ return sym; }
	}
	
	HDLSignal(HDLModule module, String name, HDLType type, ResourceKind kind){
		this(module, name, type, kind, null, false);
	}
	
	HDLSignal(HDLModule module, String name, HDLType type, ResourceKind kind, HDLExpr equivExpr, boolean equivFlag){
		this.module = module;
		this.name = name;
		this.type = type;
		this.kind = kind;
		defaultValue = null;
		assignAlwaysFlag = false;
		this.equivExpr = equivExpr;
		this.equivFlag = equivFlag;
	}
	
	public String getName(){
		return name;
	}
	
	public HDLType getType(){
		return type;
	}

	public int getWidth(){
		if(type instanceof HDLPrimitiveType){
			return ((HDLPrimitiveType)type).getWidth();
		}else{
			return -1;
		}
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
	
	public void setIgnore(boolean flag){
		ignoreFlag = flag;
	}
	
	public boolean isIgnore(){
		return ignoreFlag;
	}

	@Override
	public void setAssign(SequencerState s, HDLExpr expr){
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
	public void setAssign(SequencerState s, int counter, HDLExpr expr){
		if(s != null){
			AssignmentCondition c = new AssignmentCondition(s, counter, expr);
			conditions.add(c);
		}else{
			kind = ResourceKind.WIRE; // change resource kind to allow using "assign" statement
			assignAlwaysFlag = true;
			assignAlwaysExpr = expr;
		}
	}

	@Override
	public void setAssign(SequencerState s, HDLExpr cond, HDLExpr expr){
		if(s != null){
			AssignmentCondition c = new AssignmentCondition(s, cond, expr);
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
	
	private void getSrcSignals(HDLExpr expr, ArrayList<HDLSignal> list){
		if(expr != null){
			HDLSignal[] src = expr.getSrcSignals();
			if(src != null){
				for(HDLSignal s: src){ list.add(s); }
			}
		}
	}
	
	public HDLSignal[] getDriveSignals(){
		ArrayList<HDLSignal> list = new ArrayList<>();
		for(AssignmentCondition c: conditions){
			if(!list.contains(c.getStateKey())){
				list.add(c.getStateKey());
			}
			/*
			if(c.getValue() instanceof HDLSignal){
				list.add((HDLSignal)c.getValue());
			}else{
				HDLSignal[] src = c.getValue().getSrcSignals(); // TODO call getDriverSignals, recursively?
				if(src != null){
					for(HDLSignal s: src){ list.add(s); }
				}
			}
			*/
		}
		return list.toArray(new HDLSignal[]{});
	}

	@Override
	public HDLSignal[] getSrcSignals(){
		ArrayList<HDLSignal> list = new ArrayList<>();
		//System.out.println("getSrcSignal:" + getName());
		/*
		for(AssignmentCondition c: conditions){
			if(!list.contains(c.getStateKey())){
				list.add(c.getStateKey());
			}
			HDLSignal[] src = c.getValue().getSrcSignals();
			if(src != null){
				for(HDLSignal s: src){ list.add(s); }
			}
		}
		*/
		getSrcSignals(assignAlwaysExpr, list);
		getSrcSignals(resetValue, list);
		getSrcSignals(defaultValue, list);
		getSrcSignals(equivExpr, list);

		return list.toArray(new HDLSignal[]{});
	}
	
	public class AssignmentCondition{
		private final SequencerState s;
		private final HDLExpr value;
		private final int count;
		private final HDLExpr cond;
		
		private AssignmentCondition(SequencerState s, HDLExpr cond, int count, HDLExpr value) {
			this.s = s;
			this.value = value;
			this.count = count;
			this.cond = cond;
		}

		public AssignmentCondition(SequencerState s, HDLExpr value) {
			this(s, null, -1, value);
		}

		public AssignmentCondition(SequencerState s, int count, HDLExpr value) {
			this(s, null, count, value);
		}

		public AssignmentCondition(SequencerState s, HDLExpr cond, HDLExpr value) {
			this(s, cond, -1, value);
		}

		public String getCondExprAsVHDL(){
			String str = "";
			if(count < 0){
				String c = String.format("%s = %s", s.getKey().getName(), s.getStateId().getValue());
				String ext = s.getExitConditionAsVHDL();
				if(!ext.equals("")) c += " and " + ext;
				str = c;
			}else{
				str = String.format("%s = %s and %s = %d", s.getKey().getName(), s.getStateId().getValue(), s.getSequencer().getDelayCounter().getName(), count);
			}
			if(cond != null){
				if(cond.getResultExpr().getType().isBit()){
					str += " and " + cond.getResultExpr().getVHDL() + " = '1'";
				}else{
					if(cond.getResultExpr().getType().isVector()){
						str += " and singed(" + cond.getResultExpr().getVHDL() + ")" + " /= 0";
					}else{
						str += " and " + cond.getResultExpr().getVHDL() + " /= 0";
					}
				}
			}
			return str;
		}

		public String getCondExprAsVerilogHDL(){
			String str;
			if(count < 0){
				String c = String.format("%s == %s", s.getKey().getName(), s.getStateId().getValue());
				String ext = s.getExitConditionAsVerilogHDL();
				if(!ext.equals("")) c += " && " + ext;
				str = c;
			}else{
				str = String.format("%s == %s && %s == %d", s.getKey().getName(), s.getStateId().getValue(), s.getSequencer().getDelayCounter().getName(), count);
			}
			if(cond != null){
				if(cond.getResultExpr().getType().isBit()){
					str += " && " + cond.getResultExpr().getVerilogHDL() + " == 1'b1";
				}else{
					str += " && " + cond.getResultExpr().getVerilogHDL() + " != 0";
				}
			}
			return str;
		}
		
		public HDLExpr getValue(){
			return value.getResultExpr();
		}
		
		public HDLSignal getStateKey(){
			return s.getKey();
		}
		
		public SequencerState getSequencerState(){
			return s;
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
