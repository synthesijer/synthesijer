package synthesijer.scheduler;

import java.io.PrintStream;
import java.util.ArrayList;

public class SchedulerSlot {

	private final int stepId;

	/**
	 * container of items.
	 * the index in this array corresponds to computation step. 
	 */
	private ArrayList<SchedulerItem> items = new ArrayList<>();

	public SchedulerSlot(int id){
		this.stepId = id;
	}

	public SchedulerItem[] getItems(){
		return items.toArray(new SchedulerItem[]{});
	}

	/**
	 * adds an item into the container with new slot, and set stepId into the item.
	 * @param item
	 * @return the added item
	 */
	public SchedulerItem addItem(SchedulerItem item){
		items.add(item);
		return item;
	}
	
	public SchedulerItem insertItemInTop(SchedulerItem item){
		items.add(0, item);
		item.setSlot(this);
		return item;
	}
	
	public int getStepId(){
		return this.stepId;
	}

	public int[] getNextStep(){
		int max = 0;
		for(SchedulerItem item: items){
			if(item.isBranchOp()) return item.getBranchId();
			max = item.getBranchId()[0] > max ? item.getBranchId()[0] : max;
		}
		return new int[]{max};
	}

	public int getLatency(){
		int max = 0;
		for(SchedulerItem item: items){
			max = item.getOp().latency > max ? item.getOp().latency : max;
		}
		return max;
	}

	public boolean hasBranchOp(){
		for(SchedulerItem item: items){
			if(item.getOp().isBranch) return true;
		}
		return false;
	}

	public boolean hasMethodExit(){
		for(SchedulerItem item: items){
			if(item.getOp() == Op.METHOD_EXIT) return true;
		}
		return false;
	}
	
	public boolean hasMethodEntry(){
		for(SchedulerItem item: items){
			if(item.getOp() == Op.METHOD_ENTRY) return true;
		}
		return false;
	}

	public void dump(PrintStream out){
		dump(out, "");
	}

	public void dump(PrintStream out, String sep){
		for(SchedulerItem item: items){
			out.println(sep + item.info());
		}
	}

	public void dumpDot(PrintStream out){
		if(items.size() == 0) return;
		String base = items.get(0).getBoardName();
		String s = "";
		for(SchedulerItem item: items){
			s += item.info() + "\\l";
		}
		out.printf("%s_%d [shape = box, label =\"%s\"];", base, getStepId(), s);
		out.println();
		if(hasMethodExit()) return;
		for(int n: items.get(0).getBranchId()){
			out.printf("%s_%d -> %s_%d [headport=n, tailport=s];", base, getStepId(), base, n);
			out.println();
		}
	}

	public String toInfo(){
		String s = "";
		String sep = "";
		for(SchedulerItem item: items){
			s += sep + item.info();
			sep = ",";
		}
		return s;
	}

	public Operand[] getSrcOperands(){
		ArrayList<Operand> operand = new ArrayList<>();
		for(SchedulerItem item: items){
			Operand[] src = item.getSrcOperand();
			if(src == null) continue;
			for(Operand o: src){
				operand.add(o);
			}
		}
		return operand.toArray(new Operand[]{});
	}

	public Operand[] getDestOperands(){
		ArrayList<Operand> operand = new ArrayList<>();
		for(SchedulerItem item: items){
			Operand d = item.getDestOperand();
			operand.add(d);
		}
		return operand.toArray(new Operand[]{});
	}

	public boolean hasDefinitionOf(Operand v){
		for(Operand o: getDestOperands()){
			if(v == o){
				return true;
			}
		}
		return false;
	}

}
