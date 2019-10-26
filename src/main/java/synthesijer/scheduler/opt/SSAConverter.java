package synthesijer.scheduler.opt;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;

public class SSAConverter implements SchedulerInfoOptimizer{

	private SchedulerInfo info;

	public SchedulerInfo opt(SchedulerInfo info){
		this.info = info;
		SchedulerInfo result = info.getSameInfo();
		for(SchedulerBoard b: info.getBoardsList()){
			result.addBoard(conv(b));
		}
		return result;
	}

	public String getKey(){
		return "ssa_converter";
	}

	public SchedulerBoard conv(SchedulerBoard src){

		SchedulerBoard ret = src.genSameEnvBoard();
		SchedulerSlot[] slots = src.getSlots();

		for(SchedulerSlot s: slots){
			ret.addSlot(s.sameSchedulerSlot());
		}

		ControlFlowGraph g = new ControlFlowGraph(ret, info.getName() + "_scheduler_board_" + getKey());
		insertPhiFuncAll(ret, g);

		return ret;
	}

	private void insertPhiFuncAll(SchedulerBoard board, ControlFlowGraph g){

		ControlFlowGraphBB[] blocks = g.getBasicBlocks();

		Hashtable<ControlFlowGraphBB, Operand> inserted = new Hashtable<>();
		Hashtable<ControlFlowGraphBB, Operand> work = new Hashtable<>();
		
		ArrayList<ControlFlowGraphBB> W = new ArrayList<>();
		
		var destinations = board.getDestinationVariables();
			
		for(var v: destinations){
			for(int i = 2; i < blocks.length; i++){
				var bb = blocks[i];
				if(bb.hasDefinitionOf(v)){
					W.add(bb);
					work.put(bb, v);
				}
			}
			
			while(W.size() > 0){
				var x = W.get(0); W.remove(x);
				for(var y : g.dominanceFrontierOf(x)){
					if(inserted.get(y) == null || inserted.get(y) != v){
						insertPhiFunc(board, y, v);
						inserted.put(y, v);
						if(work.get(y) == null || work.get(y) != v){
							W.add(y);
							work.put(y, v);
						}
					}
				}
			}
		}
	}

	private void insertPhiFunc(SchedulerBoard board, ControlFlowGraphBB bb, VariableOperand v){
		var slot = bb.nodes.get(0).slot;
		var phi = new SchedulerItem(board, Op.PHI, new Operand[]{}, v);
		phi.setBranchIds(slot.getNextStep());
		bb.nodes.get(0).slot.insertItemInTop(phi);
	}

}
