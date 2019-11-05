package synthesijer.scheduler.opt;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashMap;

import synthesijer.ast.type.PrimitiveTypeKind;
import synthesijer.ast.Type;

import synthesijer.scheduler.Op;
import synthesijer.scheduler.Operand;
import synthesijer.scheduler.SchedulerBoard;
import synthesijer.scheduler.SchedulerInfo;
import synthesijer.scheduler.SchedulerItem;
import synthesijer.scheduler.PhiSchedulerItem;
import synthesijer.scheduler.SchedulerSlot;
import synthesijer.scheduler.VariableOperand;
import synthesijer.scheduler.VariableRefOperand;

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
			SchedulerSlot slot = new SchedulerSlot(s.getStepId());
			for(var i: s.getItems()){
				slot.addItem(i.copy(ret, slot));
			}
			ret.addSlot(slot);
		}

		ControlFlowGraph g = new ControlFlowGraph(ret, info.getName() + "_scheduler_board_" + getKey());
		insertPhiFuncAll(ret, g);
		setPhiFuncValuesAll(ret, g);

		return ret;
	}

	private void insertPhiFuncAll(SchedulerBoard board, ControlFlowGraph g){

		ControlFlowGraphBB[] blocks = g.getBasicBlocks();

		Hashtable<ControlFlowGraphBB, Operand> inserted = new Hashtable<>();
		Hashtable<ControlFlowGraphBB, Operand> work = new Hashtable<>();
		
		ArrayList<ControlFlowGraphBB> W = new ArrayList<>();
		
		var destinations = board.getDestinationVariables();
			
		for(var v: destinations){
			if(isExcludeFromSSA(v)) continue;
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

	private boolean isExcludeFromSSA(VariableOperand v){
		if(v instanceof VariableRefOperand) return true;
		if(v.isMember()) return true;
		if(v.isMethodParam()) return true;
		Type t = v.getType();
		if(t != PrimitiveTypeKind.BOOLEAN &&
		   t != PrimitiveTypeKind.BYTE &&
		   t != PrimitiveTypeKind.CHAR &&
		   t != PrimitiveTypeKind.INT &&
		   t != PrimitiveTypeKind.LONG &&
		   t != PrimitiveTypeKind.SHORT &&
		   t != PrimitiveTypeKind.DOUBLE &&
		   t != PrimitiveTypeKind.FLOAT){
			return true;
		}
		return false;
	}

	private void insertPhiFunc(SchedulerBoard board, ControlFlowGraphBB bb, VariableOperand v){
		var slot = bb.nodes.get(0).slot;
		Operand[] operands = new Operand[bb.pred.size()];
		for(int i = 0; i < bb.pred.size(); i++){
			operands[i] = v;
		}
		SchedulerSlot[] slots = new SchedulerSlot[bb.pred.size()];
		for(int i = 0; i < bb.pred.size(); i++){
			slots[i] = bb.pred.get(i).getLastNode().slot;
		}
		var phi = new PhiSchedulerItem(board, slots, operands, v);
		phi.setBranchIds(slot.getNextStep());
		bb.nodes.get(0).slot.insertItemInTop(phi);
	}

	private void setPhiFuncValuesAll(SchedulerBoard board, ControlFlowGraph g){
		SSAIDManager S = new SSAIDManager(0);
		HashMap<VariableOperand, Integer> C = new HashMap<>();
		HashMap<String, VariableOperand> V = new HashMap<>();
		ControlFlowGraphBB root = g.root;
		setPhiFuncValues(board, g, S, C, V, root);
	}

	private VariableOperand getSSAVariable(SchedulerBoard board, HashMap<String, VariableOperand> V, VariableOperand orig, String newName){
		VariableOperand v;
		if(V.containsKey(newName)){
			v = V.get(newName);
		}else{
			v = new VariableOperand(orig, newName); // copy
			V.put(newName, v);
			board.getVarList().add(v);
		}
		return v;
	}
	
	private void setPhiFuncValues(SchedulerBoard board, ControlFlowGraph g, SSAIDManager S, HashMap<VariableOperand, Integer> C, HashMap<String, VariableOperand> V, ControlFlowGraphBB x){
		for(var a : x.getItems()){
			if(a.getOp() != Op.PHI){
				Operand[] operands = a.getSrcOperand();
				if(operands != null){
					for(int id = 0; id < operands.length; id++){
						if(operands[id] instanceof VariableOperand){
							VariableOperand v = (VariableOperand)operands[id];
							if(isExcludeFromSSA(v) == false){
								int i = S.top(v);
								VariableOperand vv = getSSAVariable(board, V, v, getSSAName(v.getName(), i));
								a.overwriteSrc(id, vv);
							}
						}
					}
				}
			}
			var v = a.getDestOperand();
			if(v != null && isExcludeFromSSA(v) == false){
				int i = C.getOrDefault(v, new Integer(1)); // C's default value is 1
				VariableOperand vv = getSSAVariable(board, V, v, getSSAName(v.getName(), i));
				a.setDestOperand(vv);
				S.push(v, i);
				C.put(v, i+1);
			}
		}

		// make write-after-read-chain in same ScheduleSlot
		for(var node : x.nodes){
			var slot = node.slot;
			HashMap<String, SchedulerItem> chainingSrcMap = new HashMap<>();
			for(var a : slot.getItems()){
				Operand o = a.getDestOperand();
				if(o != null){
					chainingSrcMap.put(o.getName(), a);
				}
			}
			for(var a : slot.getItems()){
				Operand[] src = a.getSrcOperand();
				if(src != null){
					for(var s : src){
						if(s instanceof VariableOperand && chainingSrcMap.containsKey(s.getName())){
							VariableOperand v = (VariableOperand)s;
							v.setChaining(a, chainingSrcMap.get(s.getName()));
						}
					}
				}
			}
		}
		
		for(var y : x.succ){
			int j = y.getPredIndex(x);
			for(var item : y.getItems()){
				if(item.getOp() == Op.PHI){
					VariableOperand v = (VariableOperand)(item.getSrcOperand()[j]);
					if(isExcludeFromSSA(v) == false){
						int i = S.top(v);
						item.overwriteSrc(j, getSSAVariable(board, V, v, getSSAName(v.getName(), i)));
					}
				}
			}
		}
		
		for(var y : g.getChildren(x)){
			setPhiFuncValues(board, g, S, C, V, y);
		}
		for(var a : x.getItems()){
			var v = a.getDestOperand();
			if(v != null && isExcludeFromSSA(v) == false){
				S.pop(v);
			}
		}
	}

	private String getSSAName(String base, int i){
		return base + "_" + i;
	}

}

class SSAIDManager{

	HashMap<VariableOperand, ArrayList<Integer>> T = new HashMap<>();

	final int defaultValue;

	SSAIDManager(int defaultValue){
		this.defaultValue = defaultValue;
	}

	public int top(VariableOperand v){
		if(T.get(v) == null){
			ArrayList<Integer> a = new ArrayList<>();
			a.add(0, defaultValue);
			T.put(v, a);
		}
		return T.get(v).get(0);
	}
	
	public void push(VariableOperand v, int i){
		if(T.get(v) == null){
			ArrayList<Integer> a = new ArrayList<>();
			a.add(0, defaultValue);
			T.put(v, a);
		}
		T.get(v).add(0, i); // insert 'i' at index 0
	}
	
	public void pop(VariableOperand v){
		if(T.get(v) == null){
			ArrayList<Integer> a = new ArrayList<>();
			a.add(0, defaultValue);
			T.put(v, a);
		}else{
			if(T.get(v).size() > 1){
				T.get(v).remove(0);
			}
		}
	}
	
}
