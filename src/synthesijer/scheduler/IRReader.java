package synthesijer.scheduler;

import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.SynthesijerUtils;
import synthesijer.ast.Module;
import synthesijer.ast.Type;
import synthesijer.ast.type.MultipleType;
import synthesijer.ast.type.TypeGen;

public class IRReader {
	
	public final String fileName;
	
	private final SExp sexp;
	
	public final SchedulerInfo result;

	public IRReader(String src){
		this.fileName = src;
		try{
			sexp = SExp.load(src);
			result = parseModule(sexp);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	private SchedulerInfo parseModule(SExp sexp) throws Exception{
		SchedulerInfo info;
		ArrayList<ChainingInfo> chainingVars = new ArrayList<>();
		if(sexp.size() > 1 && sexp.get(0).equals("MODULE")){
			Module m = new Module(sexp.get(1).toString(), new Hashtable<String, String>(), "", new ArrayList<String>());
			info = new SchedulerInfo(sexp.get(1).toString(), m);
		}else{
			throw new IRReaderException("Illegal format");
		}		
		for(int i = 2; i < sexp.size(); i++){
			Object o = sexp.get(i);
			if(o instanceof SExp && ((SExp)o).size() > 0){
				SExp child = (SExp)o;
				switch(child.get(0).toString()){
				case "VARIABLES":
					parseVariables(child, info, info.getModuleVarList(), chainingVars);
					break;
				case "BOARD":
					parseBoard(child, info, chainingVars);
					break;
				default:
					throw new IRReaderException("Illegal format: expected VARIABLES or BOARD, but " + o);
				}
			}else{
				throw new IRReaderException("Illegal format: expected VARIABLES or BOARD, but " + o);
			}
		}

		genChainingRelations(info, chainingVars);
		
		return info;
	}
	
	private void genChainingRelations(SchedulerInfo info, ArrayList<ChainingInfo> chainingVars) throws Exception{
		for(ChainingInfo chaining: chainingVars){
			SExp list = chaining.expr;
			for(int i = 0; i < list.size(); i++){
				SExp e = (SExp)(list.get(i));
				String ctx_name = e.get(0).toString();
				int ctx_id = Integer.parseInt(e.get(1).toString());
				SchedulerItem ctx = searchConsumingItem(info, ctx_name, ctx_id, chaining.o);
				SchedulerItem pred = searchProducingItem(info, ctx_name, ctx_id, chaining.o);
				//System.out.println(ctx.info());
				//System.out.println(" <- " + pred.info());
				chaining.o.setChaining(ctx, pred);
			}
		}
	}
	
	private SchedulerItem searchConsumingItem(SchedulerInfo info, String name, int id, Operand target){
		SchedulerBoard board = null;
		for(SchedulerBoard b: info.getBoardsList()){
			if(b.getName().equals(name)){ board = b; break; }
		}
		if(board == null) return null;
		SchedulerSlot slot = null;
		for(SchedulerSlot s: board.getSlots()){
			if(s.getStepId() == id){ slot = s; break; }
		}
		if(slot == null) return null;
		for(SchedulerItem i: slot.getItems()){
			Operand[] src = i.getSrcOperand();
			for(Operand o: src){
				if(o == target) return i;
			}
		}
		return null;
	}

	private SchedulerItem searchProducingItem(SchedulerInfo info, String name, int id, Operand target){
		SchedulerBoard board = null;
		for(SchedulerBoard b: info.getBoardsList()){
			if(b.getName().equals(name)){ board = b; break; }
		}
		if(board == null) return null;
		SchedulerSlot slot = null;
		for(SchedulerSlot s: board.getSlots()){
			if(s.getStepId() == id){ slot = s; break; }
		}
		if(slot == null) return null;
		for(SchedulerItem i: slot.getItems()){
			Operand dest = i.getDestOperand();
			if(dest == target) return i;
		}
		return null;
	}

	private void parseVariables(SExp sexp, SchedulerInfo info, ArrayList<Operand> table, ArrayList<ChainingInfo> chainingVars) throws Exception{
		for(int i = 1; i < sexp.size(); i++){
			Object o = sexp.get(i);
			if(o instanceof SExp && ((SExp)o).size() > 0){
				SExp child = (SExp)o;
				switch(child.get(0).toString()){
				case "VAR":
					table.add(genVariableOperand(child, info, table, chainingVars));
					break;
				case "CONSTANT":
					table.add(genConstantOperand(child));
					break;
				case "ARRAY-REF":
					table.add(genArrayRefOperand(child));
					break;
				case "INSTANCE-REF":
					table.add(genInstanceRefOperand(child));
					break;
				default:
					throw new IRReaderException("Illegal format: expected VAR or CONSTANT, but " + o);
				}
			}else{
				throw new IRReaderException("Illegal format: expected VAR or CONSTANT, but " + o);
			}
		}
		for(Operand o: table){
			if(o instanceof VariableOperand){
				VariableOperand v = (VariableOperand)o; 
				if(v.getInitSrc() != null && v.getInitSrc() instanceof TemporalOperand){ // local
					Operand replaced = search(table, v.getInitSrc().getName());
					v.setInitSrc(replaced);
				}				
				if(v.getInitSrc() != null && v.getInitSrc() instanceof TemporalOperand){ // global
					Operand replaced = search(info.getModuleVarList(), v.getInitSrc().getName());
					v.setInitSrc(replaced);
				}				
			}
		}
	}
	
	private Operand search(ArrayList<Operand> table, String k){
		for(Operand o: table){
			if(o.getName().equals(k)) return o;
		}
		return new TemporalOperand(k);
	}
	
	private VariableOperand genVariableOperand(SExp node, SchedulerInfo info, ArrayList<Operand> table, ArrayList<ChainingInfo> chainingVars) throws Exception{
	
		String name = node.get(2).toString();
		Type type;
		if(node.get(1) instanceof String){
			type = TypeGen.get(node.get(1).toString());
		}else if(sexp.get(1) instanceof SExp){
			// too AD-HOC
			SExp t = (SExp)(sexp.get(1));
			if(t.get(0) instanceof String && t.get(0).toString().equals("MULTI")){
				ArrayList<Type> types = new ArrayList<Type>(); 
				for(int i = 1; i < t.size(); i++){
					types.add(TypeGen.get(t.get(i).toString()));
				}
				type = new MultipleType(types);
			}else{
				SynthesijerUtils.warn("unknown type: " + t);
				SynthesijerUtils.warn("This type is handled as UNKNOWN");
				type = TypeGen.get("UNKNOWN");
			}
		}else{
			SynthesijerUtils.warn("unknown type: " + node.get(1));
			SynthesijerUtils.warn("This type is handled as UNKNOWN");
			type = TypeGen.get("UNKNOWN");
		}
		Operand initSrc = null;
		boolean publicFlag = false;
		boolean globalConstantFlag = false;
		boolean methodParamFlag = false;
		String origName = null;
		String methodName = null;
		boolean privateMethodFlag = false;
		boolean volatileFlag = false;
		boolean memberFlag = false;
		
		boolean chainingFlag = false;
		SExp chainingList = null;

		for(int i = 3; i < node.size(); i = i + 2){
			String k = node.get(i).toString();
			Object v = node.get(i+1);
			switch(k){
			case ":public": publicFlag = Boolean.parseBoolean(v.toString()); break;
			case ":global_constant": globalConstantFlag = Boolean.parseBoolean(v.toString()); break;
			case ":method_param": methodParamFlag = Boolean.parseBoolean(v.toString()); break;
			case ":original": origName = v.toString(); break;
			case ":method": methodName = v.toString(); break;
			case ":private_method": privateMethodFlag = Boolean.parseBoolean(v.toString()); break;
			case ":volatile": volatileFlag = Boolean.parseBoolean(v.toString()); break;
			case ":member": memberFlag = Boolean.parseBoolean(v.toString()); break;
			case ":init":{
				if(v instanceof SExp && ((SExp)v).size() > 0){
					SExp child = ((SExp)v);
					if(child.get(0).equals("REF")){
						Operand target = search(table, child.get(2).toString());
						initSrc = target;
					}else{
						throw new IRReaderException("expected REF object for the argument of :init" + node);
					}
				}else{
					throw new IRReaderException("expected SExp object for the argument of :init" + node);
				}
				break;
			}
			case ":chaining":{
				chainingFlag = true;
				chainingList = (SExp)v;
			}
			break;
			default:
				SynthesijerUtils.warn("unknown keyword : " + k + ", the value " + v + " is skipped");
			}
		}
		
		VariableOperand v = new VariableOperand(
				name, type, initSrc, publicFlag, globalConstantFlag, methodParamFlag, origName, methodName, privateMethodFlag, volatileFlag, memberFlag);
		
		if(chainingFlag){
			chainingVars.add(new ChainingInfo(v, chainingList));
		}
		
		return v;
	}
	
	private ConstantOperand genConstantOperand(SExp node) throws Exception{
		String name = node.get(2).toString();
		Type type = TypeGen.get(node.get(1).toString());
		String value = node.get(3).toString();
		ConstantOperand c = new ConstantOperand(name, value, type);
		return c;
	}

	private ArrayRefOperand genArrayRefOperand(SExp node) throws Exception{
		String name = node.get(2).toString();
		Type type = TypeGen.get(node.get(1).toString());
		int depth = Integer.parseInt(parseArgument(node, ":depth").toString());
		int words = Integer.parseInt(parseArgument(node, ":words").toString());
		ArrayRefOperand o = new ArrayRefOperand(name, type, depth, words);
		return o;
	}

	private InstanceRefOperand genInstanceRefOperand(SExp node) throws Exception{
		String name = node.get(2).toString();
		String className = node.get(1).toString();
		Type type = TypeGen.get("DECLARED"); 
		InstanceRefOperand o = new InstanceRefOperand(name, type, className);
		return o;
	}

	private void parseBoard(SExp sexp, SchedulerInfo info, ArrayList<ChainingInfo> chainingVars) throws Exception{
		String name = sexp.get(2).toString();
		Type returnType;
		if(sexp.get(1) instanceof String){
			returnType = TypeGen.get(sexp.get(1).toString());
		}else if(sexp.get(1) instanceof SExp){
			// too AD-HOC
			SExp t = (SExp)(sexp.get(1));
			if(t.get(0) instanceof String && t.get(0).toString().equals("MULTI")){
				ArrayList<Type> types = new ArrayList<Type>(); 
				for(int i = 1; i < t.size(); i++){
					types.add(TypeGen.get(t.get(i).toString()));
				}
				returnType = new MultipleType(types);
			}else{
				SynthesijerUtils.warn("unknown type: " + t);
				SynthesijerUtils.warn("This type is handled as UNKNOWN");
				returnType = TypeGen.get("UNKNOWN");
			}
		}else{
			SynthesijerUtils.warn("unknown type: " + sexp.get(1));
			SynthesijerUtils.warn("This type is handled as UNKNOWN");
			returnType = TypeGen.get("UNKNOWN");
		}
		boolean privateFlag = false;
		boolean autoFlag = false;
		boolean callStackFlag = false;
		int callStackSize = -1;
		boolean hasWaitWithMethod = false;
		String waitMethodName = "";
		
		int i = 3;
		while(i < sexp.size()){
			Object o = sexp.get(i);
			if(o instanceof String){
				String k = o.toString();
				Object v = sexp.get(i+1);
				switch(k){
				case ":private": privateFlag = Boolean.parseBoolean(v.toString()); break;
				case ":auto": autoFlag = Boolean.parseBoolean(v.toString()); break;
				case ":call_stack_size":{
					callStackFlag = true;
					callStackSize = Integer.parseInt(v.toString());
				}
				break;
				case ":dependent_board":{
					hasWaitWithMethod = true;
					waitMethodName = v.toString();
				}
				break;
				default:
					SynthesijerUtils.warn("unknown keyword : " + k + ", the value " + v + " is skipped");
				}
				i = i + 2;
			}else{
				i = i + 1;
			}
		}
		
		SchedulerBoard board = new SchedulerBoard(name, returnType, privateFlag, autoFlag, callStackFlag, callStackSize, hasWaitWithMethod, waitMethodName);
		
		for(i = 3; i < sexp.size(); i++){
			Object o = sexp.get(i);
			if(o instanceof SExp && ((SExp)o).size() > 0){
				SExp child = (SExp)o;
				switch(child.get(0).toString()){
				case "VARIABLES":
					parseVariables(child, info, board.getVarList(), chainingVars);
					break;
				case "SEQUENCER":
					parseSequencer(child, info, board);
					break;
				default:
					throw new IRReaderException("Illegal format: expected VARIABLES or SEQUENCER, but " + o);
				}
			}else{
				throw new IRReaderException("Illegal format: expected VARIABLES or SEQUENCER, but " + o);
			}
		}
		info.addBoard(board);
	}
	
	private void parseSequencer(SExp node, SchedulerInfo info, SchedulerBoard board) throws Exception{
		for(int i = 2; i < node.size(); i++){
			Object o = node.get(i);
			if(o instanceof SExp && ((SExp)o).size() > 0){
				SExp child = (SExp)o;
				if(child.get(0).equals("SLOT")){
					SchedulerSlot slot = parseSlot(child, info, board);
					board.addSlot(slot);
				}else{
					throw new IRReaderException("Illegal format: expected SLOT, but " + o);
				}
			}else{
				throw new IRReaderException("Illegal format: expected SExp object of SLOT, but " + o);
			}
		}
	}
	
	private SchedulerSlot parseSlot(SExp node, SchedulerInfo info, SchedulerBoard board) throws Exception{
		int id = Integer.parseInt(node.get(1).toString());
		SchedulerSlot slot = new SchedulerSlot(id);
		for(int i = 2; i < node.size(); i++){
			Object o = node.get(i);
			if(o instanceof SExp && ((SExp)o).size() > 0){
				SExp child = (SExp)o;
				parseItem(child, info, board, slot);
			}else{
				throw new IRReaderException("Illegal format: expected SExp object of some items, but " + o);
			}
		}
		return slot;
	}
	
	private Operand search(SchedulerInfo info, SchedulerBoard board, String k){
		for(Operand o: board.getVarList()){
			if(o.getName().equals(k)) return o;
		}
		for(Operand o: info.getModuleVarList()){
			if(o.getName().equals(k)) return o;
		}
		return new TemporalOperand(k);
	}
	
	public void parseItem(SExp node, SchedulerInfo info, SchedulerBoard board, SchedulerSlot slot) throws Exception{
		
		String opLabel = node.get(0).toString();
		SchedulerItem item = null;
		
		switch(opLabel){
		case "METHOD_ENTRY":{
			item = new MethodEntryItem(board, board.getName());
		}
		break;
		case "CALL":{
			// MethodInvokeItem
			String name = parseArgument(node, ":name").toString();
			SExp n = (SExp)(parseArgument(node, ":args"));
			String[] args = new String[n.size()];
			Operand[] src = parseSrcOperands(node, info, board);
			for(int i = 0; i < args.length; i++) args[i] = n.get(i).toString();
			item = new MethodInvokeItem(board, name, src, null, args);
		}
		break;
		case "EXT_CALL":{
			// MethodInvokeItem
			String name = parseArgument(node, ":name").toString();
			Operand obj = search(info, board, parseArgument(node, ":obj").toString());
			SExp n = (SExp)(parseArgument(node, ":args"));
			String[] args = new String[n.size()];
			Operand[] src = parseSrcOperands(node, info, board);
			for(int i = 0; i < args.length; i++) args[i] = n.get(i).toString();
			item = new MethodInvokeItem(board, (VariableOperand)obj, name, src, null, args);
		}
		break;
		case "FIELD_ACCESS":{
			// FieldAccessItem
			Operand obj = search(info, board, parseArgument(node, ":obj").toString());
			String name = parseArgument(node, ":name").toString();
			Operand[] src = parseSrcOperands(node, info, board);
			item = new FieldAccessItem(board, (VariableOperand)obj, name, src, null);
		}
		break;
		case "SELECT":{
			Operand target = search(info, board, parseArgument(node, ":target").toString());
			SExp args = (SExp)(parseArgument(node, ":patterns"));
			Operand[] pat = new Operand[args.size()];
			for(int i = 0; i < pat.length; i++){
				pat[i] = search(info, board, args.get(i).toString());
			}
			item = new SelectItem(board, target, pat);
		}
		break;
		case "SET":{
			// with destination
			Operand dest = search(info, board, node.get(1).toString());
			SExp expr = (SExp)(node.get(2));
			Op op = Op.parseOp(expr.get(0).toString());
			Operand[] src = parseSrcOperands(expr, info, board);
			switch(op){
			case CALL:{
				String name = parseArgument(expr, ":name").toString();
				SExp n = (SExp)(parseArgument(expr, ":args"));
				String[] args = new String[n.size()];
				for(int i = 0; i < args.length; i++) args[i] = n.get(i).toString();
				item = new MethodInvokeItem(board, name, src, (VariableOperand)dest, args);
			}
			break;
			case EXT_CALL:{
				String name = parseArgument(expr, ":name").toString();
				Operand obj = search(info, board, parseArgument(expr, ":obj").toString());
				SExp n = (SExp)(parseArgument(expr, ":args"));
				String[] args = new String[n.size()];
				for(int i = 0; i < args.length; i++) args[i] = n.get(i).toString();
				item = new MethodInvokeItem(board, (VariableOperand)obj, name, src, (VariableOperand)dest, args);
			}
			break;
			case FIELD_ACCESS:{
				Operand obj = search(info, board, parseArgument(expr, ":obj").toString());
				String name = parseArgument(expr, ":name").toString();
				item = new FieldAccessItem(board, (VariableOperand)obj, name, src, (VariableOperand)dest);
			}
			break;
			case CAST:{
				// TypeCastItem
				Type orig = TypeGen.get(parseArgument(expr, ":orig").toString());
				Type target = TypeGen.get(parseArgument(expr, ":target").toString());
				item = TypeCastItem.newCastItem(board, src[0], (VariableOperand)dest, orig, target);
			}
			break;
			default:{
				item = new SchedulerItem(board, op, src, (VariableOperand)dest);
			}
			}
		}
		break;
		default:{
			Op op = Op.parseOp(opLabel);
			Operand[] src = parseSrcOperands(node, info, board);
			item = new SchedulerItem(board, op, src, null);
		}
		}
		
		if(item != null){
			item.setBranchIds(parseBranchIds(node));
			slot.addItem(item);
			item.setSlot(slot);
		}else{
			SynthesijerUtils.warn("skipped:" + node);
		}
	}
	
	private Operand[] parseSrcOperands(SExp node, SchedulerInfo info, SchedulerBoard board) throws Exception{
		ArrayList<Operand> operands = new ArrayList<>();
		int i = 1;
		while(i < node.size()){
			if(node.get(i).toString().startsWith(":")){
				i = i + 2;
			}else{
				operands.add(search(info, board, node.get(i).toString()));
				i = i + 1;
			}
		}
		return operands.toArray(new Operand[]{});
	}

	private int[] parseBranchIds(SExp node) throws Exception{
		int id = -1;
		for(int i = 0; i < node.size(); i++){
			if(node.get(i).toString().equals(":next") && node.get(i+1) instanceof SExp){
				id = i+1;
				break;
			}
		}
		int[] d;
		if(id > 0){
			SExp n = (SExp)(node.get(id));
			d = new int[n.size()];
			for(int i = 0; i < d.length; i++){
				d[i] = Integer.parseInt(n.get(i).toString());
			}
		}else{
			d = new int[]{};
		}
		return d;
	}

	private Object parseArgument(SExp node, String k) throws Exception{
		for(int i = 0; i < node.size(); i++){
			if(node.get(i).toString().equals(k)){
				return node.get(i+1);
			}
		}
		return null;
	}
	
	private class ChainingInfo{
		public final VariableOperand o;
		public final SExp expr;
		public ChainingInfo(VariableOperand o, SExp expr){
			this.o = o;
			this.expr = expr;
		}
	}

	public static void main(String... args) throws Exception{
		IRReader reader = new IRReader(args[0]);
		if(args.length == 2) return;
		System.out.println("VARIABLES:");
		for(Operand v: reader.result.getModuleVarList()){
			System.out.println(v.dump());
		}
		System.out.println("");
		System.out.println("BOARDS");
		for(SchedulerBoard b: reader.result.getBoardsList()){
			System.out.println("BOARD:" + b.getName());
			System.out.println("VARIABLES:");
			for(Operand v: b.getVarList()){
				System.out.println(v.dump());
			}
			System.out.println("SEQUENCER:");
			b.dump(System.out);
		}

	}

}

class IRReaderException extends Exception{

	public IRReaderException(String s){
		super(s);
	}
	
}
