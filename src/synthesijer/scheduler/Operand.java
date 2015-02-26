package synthesijer.scheduler;

import synthesijer.ast.Type;

public interface Operand {
	
	public String info();
	
	public Type getType();
	
	public boolean isChaining(SchedulerItem ctx);

}
