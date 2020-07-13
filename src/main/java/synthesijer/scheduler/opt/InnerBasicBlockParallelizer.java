package synthesijer.scheduler.opt;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.HashMap;

import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.PhiSchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;

public class InnerBasicBlockParallelizer implements SchedulerInfoOptimizer{

	public static final boolean DEBUG = false;

	public String getKey(){
		return "inner_bb_parallelize";
	}

	public SchedulerInfo opt(SchedulerInfo info){
		SchedulerInfo result = info.getSameInfo();
		for(SchedulerBoard b: info.getBoardsList()){
			SchedulerBoard newB = b.copyBoard();
			result.addBoard(conv(newB));
		}
		return result;
	}

	public SchedulerBoard conv(SchedulerBoard src){
		SchedulerBoard ret = src.genSameEnvBoard();
		var cfg = new ControlFlowGraph(src, getKey());

		HashMap<Integer, SchedulerSlot> remap = new HashMap<>();
		ArrayList<PhiSchedulerItem> phiItems = new ArrayList<>(); // ad-hoc
		for(var bb : cfg.getBasicBlocks()){
			if(bb.size() == 0) continue;
			SchedulerSlot[] slots = parallelize(bb, remap, phiItems);
			for(SchedulerSlot slot: slots){
				ret.addSlot(slot);
			}
		}

		for(PhiSchedulerItem phi: phiItems){
			phi.remap(remap);
		}
		
		return ret;
	}
	
	public SchedulerSlot[] parallelize(ControlFlowGraphBB bb,
									   HashMap<Integer, SchedulerSlot> remap,
									   ArrayList<PhiSchedulerItem> phiItems){
		int entryStep = bb.getEntryStepId();
		int[] exitNextStep = bb.getExitNextStep();
		ArrayList<SchedulerSlot> ret = new ArrayList<>();
		DataFlowGraph dfg = new DataFlowGraph(bb);
		HashMap<DataFlowNode, Boolean> trace = new HashMap<>();
		trace.put(dfg.root, true);
		ArrayList<ArrayList<SchedulerItem>> itemGroups = new ArrayList<ArrayList<SchedulerItem>>();
		asap(itemGroups, trace, dfg.root, 0);
		SchedulerSlot slot = null;
		for(int i = 0; i < itemGroups.size(); i++){
			int stepId = (i == 0) ? entryStep : itemGroups.get(i).get(0).getStepId();
			if(slot != null){ // for previous slot
				updateBranchId(slot, stepId); // set next step id of the previous items.
			}
			slot = new SchedulerSlot(stepId);
			for(SchedulerItem item: itemGroups.get(i)){
				remap.put(item.getSlot().getStepId(), slot); // old slot -> new slot
				slot.addItem(item);
				item.setSlot(slot);
				if(item instanceof PhiSchedulerItem){
					phiItems.add((PhiSchedulerItem)item);
				}
			}
			ret.add(slot);
		}
		updateBranchIds(slot, exitNextStep);
		return ret.toArray(new SchedulerSlot[]{});
	}

	private void updateBranchId(SchedulerSlot slot, int id){
		for(SchedulerItem item: slot.getItems()){
			item.setBranchId(id);
		}
	}
	
	private void updateBranchIds(SchedulerSlot slot, int[] ids){
		for(SchedulerItem item: slot.getItems()){
			item.setBranchIds(ids);
		}
	}

	private void addItem(ArrayList<ArrayList<SchedulerItem>> itemGroups, SchedulerItem item, int level){
		while(itemGroups.size() <= level){
			itemGroups.add(new ArrayList<SchedulerItem>());
		}
		itemGroups.get(level).add(item);
	}
	
	private boolean doneAll(HashMap<DataFlowNode, Boolean> trace, DataFlowNode node){
		for(DataFlowNode n: node.parents){
			if(trace.containsKey(n) == false)
				return false;
		}
		return true;
	}

	private void asap(ArrayList<ArrayList<SchedulerItem>> itemGroups,
					  HashMap<DataFlowNode, Boolean> trace,
					  DataFlowNode node,
					  int level){
		for(DataFlowNode n: node.children){
			if(doneAll(trace, n)){
				trace.put(n, true);
				for(SchedulerItem item : n.slot.getItems()){
					addItem(itemGroups, item, level);
				}
			}
		}
		for(DataFlowNode n: node.children){
			if(doneAll(trace, n)){
				asap(itemGroups, trace, n, level+1);
			}
		}
	}



}
