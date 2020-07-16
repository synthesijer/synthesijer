package synthesijer.scheduler.opt;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.HashMap;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.File;

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
	private SchedulerInfo info;

	public String getKey(){
		return "inner_bb_parallelize";
	}

	public SchedulerInfo opt(SchedulerInfo info){
		this.info = info;
		SchedulerInfo result = info.getSameInfo();
		for(SchedulerBoard b: info.getBoardsList()){
			result.addBoard(conv(b));
		}
		return result;
	}

	public SchedulerBoard conv(SchedulerBoard src){
		SchedulerBoard ret = src.genSameEnvBoard();
		var cfg = new ControlFlowGraph(src, getKey());

		HashMap<Integer, SchedulerSlot> remap = new HashMap<>();
		ArrayList<PhiSchedulerItem> phiItems = new ArrayList<>(); // ad-hoc
		
		try{
			String base = String.format("%s_sjr_scehduler_board_%s_%s", info.getName(), getKey(), ret.getName());
			PrintStream dot = new PrintStream(new FileOutputStream(new File(base + "_dfg" + ".dot")));
			dot.printf("digraph %s {\n", ret.getName() + "_" + getKey() + "_dfg");
			
			int dfg_id = 0;
			for(var bb : cfg.getBasicBlocks()){
				if(bb.size() == 0) continue;
				DataFlowGraph dfg = new DataFlowGraph(bb);
				dfg.dumpDot(dot, dfg_id);
				parallelize(ret, bb, dfg, remap, phiItems);
				dfg_id++;
			}
			
			dot.printf("}\n");
			dot.close();
		}catch(Exception e){
			e.printStackTrace();
		}

		for(PhiSchedulerItem phi: phiItems){
			phi.remap(remap);
		}
		
		return ret;
	}
	
	public void parallelize(SchedulerBoard board,
							ControlFlowGraphBB bb,
							DataFlowGraph dfg,
							HashMap<Integer, SchedulerSlot> remap,
							ArrayList<PhiSchedulerItem> phiItems
							){
		ArrayList<SchedulerSlot> ret = new ArrayList<>();
		
		int entryStep = bb.getEntryStepId();
		int[] exitNextStep = bb.getExitNextStep();
			
		HashMap<DataFlowNode, Integer> trace = new HashMap<>();
		trace.put(dfg.root, -1);
		ArrayList<ArrayList<SchedulerItem>> itemGroups = new ArrayList<ArrayList<SchedulerItem>>();
		asap(itemGroups, trace, dfg.root);
		itemGroups = splitSpecialItems(itemGroups);
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
			board.addSlot(slot);
		}
		updateBranchIds(slot, exitNextStep);
		
		return;
	}

	private ArrayList<ArrayList<SchedulerItem>> splitSpecialItems(ArrayList<ArrayList<SchedulerItem>> itemGroups){
		ArrayList<ArrayList<SchedulerItem>> ret = new ArrayList<ArrayList<SchedulerItem>>();
		for(ArrayList<SchedulerItem> items: itemGroups){
			ArrayList<SchedulerItem> excludes = new ArrayList<>();
			for(SchedulerItem item: items){
				if(item.getOp() == Op.CALL || item.getOp() == Op.EXT_CALL){
					excludes.add(item);
				}
			}
			for(SchedulerItem item: excludes){
				items.remove(item);
			}
			if(items.size() > 0){
				ret.add(items);
			}
			for(SchedulerItem item: excludes){
				ArrayList<SchedulerItem> o = new ArrayList<>();
				o.add(item);
				ret.add(o);
			}
		}
		return ret;
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
	
	private int doneAll(HashMap<DataFlowNode, Integer> trace, DataFlowNode node){
		int max = 0;
		for(DataFlowNode n: node.parents){
			if(trace.containsKey(n) == false){
				return -1;
			}else{
				int lv = trace.get(n) + 1;
				max = max > lv ? max : lv;
			}
		}
		return max;
	}

	private void asap(ArrayList<ArrayList<SchedulerItem>> itemGroups,
					  HashMap<DataFlowNode, Integer> trace,
					  DataFlowNode node){
		for(DataFlowNode n: node.children){
			int level = -1;
			if(trace.get(n) == null && (level = doneAll(trace, n)) >= 0){
				trace.put(n, level);
				for(SchedulerItem item : n.slot.getItems()){
					addItem(itemGroups, item, level);
				}
			}
		}
		for(DataFlowNode n: node.children){
			if(trace.get(n) != null){
				asap(itemGroups, trace, n);
			}
		}
	}

}
