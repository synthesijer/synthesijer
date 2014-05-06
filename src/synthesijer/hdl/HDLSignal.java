package synthesijer.hdl;

import java.io.PrintWriter;
import java.util.ArrayList;

public class HDLSignal implements SynthesizableObject{
	
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
	
	public void setResetValue(HDLExpr s){
		this.resetValue = s;
	}
	
	public void setAssignCondition(String methodId, String stateKey, String stateId, String phaseKey, String phaseId, HDLExpr value){
		AssignmentCondition c = new AssignmentCondition(methodId, stateKey, stateId, phaseKey, phaseId, value);
		conditions.add(c);
	}

	public void setAssignCondition(String methodId, String stateKey, String stateId, HDLExpr value){
		AssignmentCondition c = new AssignmentCondition(methodId, stateKey, stateId, null, null, value);
		conditions.add(c);
	}

	public void dumpAsVHDL(PrintWriter dest, int offset){
		HDLUtils.println(dest, offset, String.format("signal %s : %s;", name, type.getVHDL()));
	}

	public void dumpAsVerilogHDL(PrintWriter dest, int offset){
		HDLUtils.println(dest, offset, String.format("%s %s %s;", kind.toString(), type.getVerilogHDL(), name));
	}
	
	public void dumpAssignProcessAsVHDL(PrintWriter dest, int offset){
		HDLUtils.println(dest, offset, String.format("process(%s)", module.getSysClkName()));
		HDLUtils.println(dest, offset, "begin");
		HDLUtils.println(dest, offset+2, String.format("if %s'event and %s = '1' then", module.getSysClkName(), module.getSysClkName()));
		HDLUtils.println(dest, offset+4, String.format("if %s = '1' then", module.getSysResetName()));
		HDLUtils.println(dest, offset+6, String.format("%s <= %s;", name, resetValue.getVHDL()));
		HDLUtils.println(dest, offset+4, String.format("else"));
		if(conditions.size() > 0){
			String sep = "if";
			for(AssignmentCondition c: conditions){
				HDLUtils.println(dest, offset+6, String.format("%s %s then", sep, c.getCondExprAsVHDL()));
				HDLUtils.println(dest, offset+8, String.format("%s <= %s;", name, c.value.getVHDL()));
				sep = "elsif";
			}
			HDLUtils.println(dest, offset+6, String.format("end if;"));
		}else{
			HDLUtils.println(dest, offset+6, String.format("null;"));
		}
		HDLUtils.println(dest, offset+4, String.format("end if;"));
		HDLUtils.println(dest, offset+2, String.format("end if;"));
		HDLUtils.println(dest, offset, "end process;");
		HDLUtils.nl(dest);
	}
	
	public void dumpAssignProcessAsVerilogHDL(PrintWriter dest, int offset){
		HDLUtils.println(dest, offset, String.format("always @(posedge %s) begin", module.getSysClkName()));
		HDLUtils.println(dest, offset+2, String.format("if(%s == 1'b1) begin", module.getSysResetName()));
		HDLUtils.println(dest, offset+4, String.format("%s <= %s;", name, resetValue.getVerilogHDL()));
		HDLUtils.println(dest, offset+2, String.format("end else begin"));
		if(conditions.size() > 0){
			String sep = "";
			for(AssignmentCondition c: conditions){
				HDLUtils.println(dest, offset+4, String.format("%s if (%s) begin", sep, c.getCondExprAsVerilogHDL()));
				HDLUtils.println(dest, offset+6, String.format("%s <= %s;", name, c.value.getVerilogHDL()));
				sep = "end else";
			}
			HDLUtils.println(dest, offset+4, String.format("end"));
		}
		HDLUtils.println(dest, offset+2, String.format("end"));
		HDLUtils.println(dest, offset, "end");
		HDLUtils.nl(dest);
	}
	
	class AssignmentCondition{
		final String methodId;
		final String stateKey;
		final String stateId;
		final String phaseKey;
		final String phaseId;
		final HDLExpr value;
		
		AssignmentCondition(String methodId, String stateKey, String stateId, String phaseKey, String phaseId, HDLExpr value){
			this.methodId = methodId;
			this.stateKey = stateKey;
			this.stateId = stateId;
			this.phaseKey = phaseKey;
			this.phaseId = phaseId;
			this.value = value;
		}
		
		public String getCondExprAsVHDL(){
			if(phaseKey != null){
				return String.format("methodId = %s and %s = %s and %s = %s", methodId, stateKey, stateId, phaseKey, phaseId);
			}else{
				return String.format("methodId = %s and %s = %s", methodId, stateKey, stateId);
			}
		}

		public String getCondExprAsVerilogHDL(){
			if(phaseKey != null){
				return String.format("methodId == %s && %s == %s && %s == %s", methodId, stateKey, stateId, phaseKey, phaseId);
			}else{
				return String.format("methodId == %s && %s == %s", methodId, stateKey, stateId);
			}
		}
	}
	
}
