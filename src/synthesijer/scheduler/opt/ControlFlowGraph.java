package synthesijer.scheduler.opt;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.PrintStream;

import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;

public class ControlFlowGraph{

	public final BasicBlock[] bb;

	public ControlFlowGraph(SchedulerBoard board){
		SchedulerSlot[] slots = board.getSlots();
		if(!(slots.length > 0)){
			bb = new BasicBlock[]{};
			return;
		}
		ArrayList<BasicBlockItem> items = prepareItems(slots);
		bb = buildBasicBlocks(items);
	}

	private ArrayList<BasicBlockItem> prepareItems(SchedulerSlot[] slots){
		ArrayList<BasicBlockItem> items = new ArrayList<>();
		for(SchedulerSlot s: slots){
			items.add(new BasicBlockItem(s));
		}
		for(BasicBlockItem n: items){
			prepareItem(n, items);
		}
		return items;
	}
	
	private void prepareItem(BasicBlockItem target, ArrayList<BasicBlockItem> items){
		for(BasicBlockItem n : items){
			for(int id : n.slot.getNextStep()){
				if(target.slot.getStepId() == id){
					target.pred.add(n);
					n.succ.add(target);
				}
			}
		}
	}

	private BasicBlock[] buildBasicBlocks(ArrayList<BasicBlockItem> items){
		BasicBlock[] bb = getBasicBlocks(items);
		for(BasicBlock b : bb){
			for(BasicBlockItem n: b.items){
				for(BasicBlockItem p: n.pred){
					if(!b.pred.contains(p.bb) && b != p.bb){
						b.pred.add(p.bb);
					}
				}
				for(BasicBlockItem s: n.succ){
					if(!b.succ.contains(s.bb) && b != s.bb){
						b.succ.add(s.bb);
					}
				}
			}
		}
		return bb;
	}
	
	private BasicBlock[] getBasicBlocks(ArrayList<BasicBlockItem> items){
		ArrayList<BasicBlock> list = new ArrayList<>();
		Hashtable<BasicBlockItem,Boolean> table = new Hashtable<>();
		for(BasicBlockItem n: items){
			if(table.containsKey(n)) continue;
			BasicBlock bb = new BasicBlock(idGen.get());
			list.add(bb);
			getBasicBlocks(list, table, n, bb);
		}
		return list.toArray(new BasicBlock[]{});
	}

	private void getBasicBlocks(ArrayList<BasicBlock> list,
								Hashtable<BasicBlockItem, Boolean> table,
								BasicBlockItem node,
								BasicBlock bb){
		
		if(table.containsKey(node)) return;
		table.put(node, true); // make the node treated

		if(node.pred.size() > 1){ // join node
			if(bb.items.size() > 0){
				bb = new BasicBlock(idGen.get());
				list.add(bb);
			}
		}
		bb.items.add(node); node.bb = bb;
		
		if(node.succ.size() == 1){
			getBasicBlocks(list, table, node.succ.get(0), bb);
		}else{
			for(BasicBlockItem n: node.succ){
				bb = new BasicBlock(idGen.get());
				list.add(bb);
				getBasicBlocks(list, table, n, bb);
			}
		}
	}
	
	public void dumpBB(PrintStream out){
		for(BasicBlock b : bb){
			b.dump(out);
		}
	}
	
	class BasicBlockItem{

		final ArrayList<BasicBlockItem> pred = new ArrayList<>();
	
		final ArrayList<BasicBlockItem> succ = new ArrayList<>();
	
		final SchedulerSlot slot;

		BasicBlock bb;

		public BasicBlockItem(SchedulerSlot slot){
			this.slot = slot;
		}

		public void dump(PrintStream out){
			slot.dump(out, "   ");
		}
	
	}

	class BasicBlock{

		ArrayList<BasicBlockItem> items = new ArrayList<>();

		public final int id;
		public final ArrayList<BasicBlock> pred = new ArrayList<>();
		public final ArrayList<BasicBlock> succ = new ArrayList<>();

		public BasicBlock(int id){
			this.id = id;
		}
	
		public void dump(PrintStream out){
			out.println(this.id);
			out.print(" <-");
			for(BasicBlock b: pred){
				out.print(" " + b.id);
			}
			out.println();
			out.print(" ->");
			for(BasicBlock b: succ){
				out.print(" " + b.id);
			}
			out.println();
			for(BasicBlockItem n : items){
				n.dump(out);
			}
		}
	
	}

	private class BasicBlockID{
		private int id = 0;
		public int get(){
			int ret = id; id++; return ret;
		}
	}
	private BasicBlockID idGen = new BasicBlockID();
	
}

