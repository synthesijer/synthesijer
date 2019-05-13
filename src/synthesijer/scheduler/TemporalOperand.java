package synthesijer.scheduler;

import synthesijer.ast.Type;

public class TemporalOperand implements Operand {

	public final String name;

	public TemporalOperand(String n){
		this.name = n;
	}

	@Override
	public String info(){
		return "TEMPORAL " + name;
	}

	@Override
	public String dump(){
		return "TEMPORAL " + name;
	}

	@Override
	public String toSexp(){
		return "(TEMPORAL " + name + ")";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public boolean isChaining(SchedulerItem ctx) {
		return false;
	}

}
