package synthesijer.scheduler.opt;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Assert.*;

import java.util.Hashtable;
import java.util.ArrayList;

import synthesijer.ast.Module;
import synthesijer.scheduler.SchedulerInfo;

public class NullOptimizerTest{
	
    @Test
    public void firstTest(){
		Hashtable<String, String> importTable = new Hashtable<>();
		String extending = "";
		ArrayList<String> implementing = new ArrayList<>();
		Module m = new Module("test", importTable, extending, implementing);
		SchedulerInfo info = new SchedulerInfo("test", m);

		NullOptimizer opt = new NullOptimizer();
		opt.opt(info);
		
    }
}
