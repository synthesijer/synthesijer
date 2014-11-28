package synthesijer.scheduler;

import java.io.PrintStream;
import java.util.ArrayList;

public class SchedulerSlot {
	
	private final int stepId;
	
	public SchedulerSlot(int id){
		this.stepId = id;
	}
	
	/**
	 * container of items.
	 * the index in this array corresponds to computation step. 
	 */
	private ArrayList<SchedulerItem> items = new ArrayList<>();
	
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

	public void dump(PrintStream out){
		for(SchedulerItem item: items){
			out.println(item.info());
		}
	}

	public void dumpDot(PrintStream out){
		for(SchedulerItem item: items){
			for(int n: item.getBranchId()){
				out.printf("%s_%d [shape = box, label = \"%s\"];", item.getBoardName(), getStepId(), item.info());
				out.println();
				out.printf("%s_%d -> %s_%d;", item.getBoardName(), getStepId(), item.getBoardName(), n);
				out.println();
			}
		}
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
	
}
