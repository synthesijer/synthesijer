package synthesijer.scheduler;

import synthesijer.ast.Type;

public interface Operand {

	public String info();

	public String dump();

	public String toSexp();

	public String getName();

	public Type getType();

	public boolean isChaining(SchedulerItem ctx);

}
