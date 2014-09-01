package synthesijer.model;

import java.io.PrintStream;
import java.util.ArrayList;

import synthesijer.ast.Method;
import synthesijer.ast.Module;

/**
 * This class manages a table to manage resource/operation usages and 
 * computation flow
 * 
 * @author miyo
 *
 */
public class SynthesisTable {
	
	private final ArrayList<SynthesisTableItem> items = new ArrayList<>();
	
	private final Module module;
	private final Method method;
	
	public SynthesisTable(Module module, Method method){
		this.module = module;
		this.method = method;
	}
	
	public String getName(){
		return module.getName() + "." + method.getName();
	}
	
	public void add(SynthesisTableItem item){
		items.add(item);
	}
	
	public void dump(PrintStream out){
		for(SynthesisTableItem i: items){
			out.println(i);
		}
	}
	
}
