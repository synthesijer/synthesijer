package synthesijer.scheduler.opt;

import synthesijer.scheduler.SchedulerInfo;

public interface SchedulerInfoOptimizer {

	public SchedulerInfo opt(SchedulerInfo info);

	public String getKey();

}
