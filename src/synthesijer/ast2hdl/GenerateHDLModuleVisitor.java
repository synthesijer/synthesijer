package synthesijer.ast2hdl;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Hashtable;

import synthesijer.CompileState;
import synthesijer.Manager;
import synthesijer.SynthesijerUtils;
import synthesijer.ast.Expr;
import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;
import synthesijer.ast.expr.Ident;
import synthesijer.ast.expr.Literal;
import synthesijer.ast.expr.NewArray;
import synthesijer.ast.expr.NewClassExpr;
import synthesijer.ast.statement.BlockStatement;
import synthesijer.ast.statement.BreakStatement;
import synthesijer.ast.statement.ContinueStatement;
import synthesijer.ast.statement.ExprStatement;
import synthesijer.ast.statement.ForStatement;
import synthesijer.ast.statement.IfStatement;
import synthesijer.ast.statement.ReturnStatement;
import synthesijer.ast.statement.SkipStatement;
import synthesijer.ast.statement.SwitchStatement;
import synthesijer.ast.statement.SwitchStatement.Elem;
import synthesijer.ast.statement.SynchronizedBlock;
import synthesijer.ast.statement.TryStatement;
import synthesijer.ast.statement.VariableDecl;
import synthesijer.ast.statement.WhileStatement;
import synthesijer.ast.type.ArrayType;
import synthesijer.ast.type.ComponentType;
import synthesijer.ast.type.MySelfType;
import synthesijer.ast.type.PrimitiveTypeKind;
import synthesijer.hdl.HDLInstance;
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPort.DIR;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLType;
import synthesijer.hdl.HDLUserDefinedType;
import synthesijer.hdl.HDLVariable;
import synthesijer.hdl.expr.HDLCombinationExpr;
import synthesijer.hdl.expr.HDLPreDefinedConstant;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.model.State;

public class GenerateHDLModuleVisitor implements SynthesijerAstVisitor{
	
	final HDLModule module;
	final Hashtable<State, HDLSequencer.SequencerState> stateTable;
	private final Hashtable<Method, HDLPort> methodReturnTable;
	private final Hashtable<Variable, HDLVariable> variableTable = new Hashtable<>();
	private final Hashtable<Method, HDLValue> methodIdTable = new Hashtable<>();
	
	public GenerateHDLModuleVisitor(HDLModule m){
		this.module = m;
		this.stateTable = new Hashtable<>();
		this.methodReturnTable = new Hashtable<>();
	}
	
	public HDLVariable getHDLVariable(Variable v){
		return variableTable.get(v);
	}	
	
	@Override
	public void visitMethod(Method o) {
		if(o.isConstructor()) return; // skip 
		if(o.isUnsynthesizable()) return; // skip
		for(VariableDecl v: o.getArgs()){
			HDLType t = getHDLType(v.getType());
			if(t != null){
				//System.out.println(v);
				HDLPort p = module.newPort(o.getName() + "_" + v.getName(), HDLPort.DIR.IN, t);
				variableTable.put(v.getVariable(), p.getSignal());
			}
		}
		HDLType t = getHDLType(o.getType());
		if(t != null){
			HDLPort p = module.newPort(o.getName() + "_return", HDLPort.DIR.OUT, t);
			methodReturnTable.put(o, p);
		}
		HDLPort req = module.newPort(o.getName() + "_req", HDLPort.DIR.IN, HDLPrimitiveType.genBitType());
		HDLPort busy = module.newPort(o.getName() + "_busy", HDLPort.DIR.OUT, HDLPrimitiveType.genBitType());
		HDLSignal req_local = module.newSignal(o.getName() + "_req_local", HDLPrimitiveType.genBitType(), HDLSignal.ResourceKind.REGISTER);
		req_local.setDefaultValue(HDLPreDefinedConstant.LOW);
		genVariableTables(o);
		o.getStateMachine().accept(new Statemachine2HDLSequencerVisitor(this, req, req_local, busy));
		o.getBody().accept(this);
		if(isThreadStart(o)){ genThreadStart(o); }
		if(isThreadJoin(o)){ genThreadJoin(o); }
		if(isThreadYield(o)){ genThreadYield(o); }
	}
	
	private HDLVariable genHDLVariable(Variable v, ArrayType t){
		Manager.HDLModuleInfo info = null;
		Type t0 = t.getElemType();
		if(t0 instanceof PrimitiveTypeKind == false){
			throw new RuntimeException("unsupported type: " + t);
		}
		switch((PrimitiveTypeKind)t0){
		case BOOLEAN: info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM1");  break;
		case BYTE:    info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM8");  break;
		case SHORT:   info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM16"); break;
		case INT:     info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM32"); break;
		case LONG:    info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM64"); break;
		case FLOAT:   info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM32"); break;
		case DOUBLE:  info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM64"); break;
		default: throw new RuntimeException("unsupported type: " + t);
		}
		HDLInstance inst = module.newModuleInstance(info.hm, v.getName());
		inst.getSignalForPort("clk").setAssign(null, module.getSysClk().getSignal());
		inst.getSignalForPort("reset").setAssign(null, module.getSysReset().getSignal());
		return inst;
	}
		
	private HDLVariable genHDLVariable(Variable v){
		Type t = v.getType();
		if(t instanceof PrimitiveTypeKind){
			HDLType t0 = getHDLType(v.getType());
			return module.newSignal(v.getUniqueName(), t0);
		}else if(t instanceof ArrayType){
			return genHDLVariable(v, (ArrayType)t);
		}else if(t instanceof ComponentType){
			ComponentType c = (ComponentType)t;
			Manager.HDLModuleInfo info = Manager.INSTANCE.searchHDLModuleInfo(c.getName());
			if(info == null){
				SynthesijerUtils.error(c.getName() + " is not found.");
				System.exit(0);
			}
			if(info.getCompileState().isBefore(CompileState.GENERATE_HDL)){
				Manager.INSTANCE.genHDL(info);
			}
			HDLInstance inst = module.newModuleInstance(info.hm, v.getName());
			inst.getSignalForPort("clk").setAssign(null, module.getSysClk().getSignal());
			inst.getSignalForPort("reset").setAssign(null, module.getSysReset().getSignal());
			return inst;
		}else{
			throw new RuntimeException("unsupported type: " + t);
		}
	}
	
	private void genVariableTables(Scope s){
		for(Variable v: s.getVariables()){
			//System.out.println("genVariableTable: " + v);
			if(variableTable.containsKey(v)) continue; // skip
			HDLVariable var = genHDLVariable(v);
			variableTable.put(v, var);
		}			
	}
	
	@Override
	public void visitModule(Module o) {
		for(Scope s: o.getScope()){
			if(s instanceof Method) continue; // variables declared in method scope should be instantiated as port. 
			genVariableTables(s);
		}
		for(VariableDecl v: o.getVariableDecls()){
			v.accept(this);
		}
		HDLUserDefinedType type = module.newUserDefinedType("methodId", new String[]{"IDLE"}, 0);		
		for(Method m: o.getMethods()){
			if(m.isConstructor()) continue;
			HDLValue v = type.addItem(m.getUniqueName());
			methodIdTable.put(m, v);
		}
		module.newSignal("methodId", type);
		for(Method m: o.getMethods()){
			m.accept(this);
		}
	}

	@Override
	public void visitBlockStatement(BlockStatement o) {
		for(Statement s: o.getStatements()){
			s.accept(this); 
		}
	}

	@Override
	public void visitBreakStatement(BreakStatement o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitContinueStatement(ContinueStatement o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitExprStatement(ExprStatement o) {
		Expr expr = o.getExpr();
		GenerateHDLExprVisitor v = new GenerateHDLExprVisitor(this, stateTable.get(o.getState()));
		expr.accept(v);
	}

	@Override
	public void visitForStatement(ForStatement o) {
		for(Statement s: o.getInitializations()){
			s.accept(this);
		}
		o.getBody().accept(this);
		for(Statement s: o.getUpdates()){
			s.accept(this);
		}
	}

	@Override
	public void visitIfStatement(IfStatement o) {
		o.getThenPart().accept(this);
		if(o.getElsePart() != null) o.getElsePart().accept(this);
	}

	@Override
	public void visitReturnStatement(ReturnStatement o) {
		if(o.getExpr() != null){
			HDLPort p = methodReturnTable.get(o.getScope().getMethod());
			HDLSequencer.SequencerState state = stateTable.get(o.getState());
			GenerateHDLExprVisitor v = new GenerateHDLExprVisitor(this, state);
			o.getExpr().accept(v);
			p.getSignal().setAssign(state, v.getResult());
		}
	}

	@Override
	public void visitSkipStatement(SkipStatement o) {
		// nothing to generate
	}

	@Override
	public void visitSwitchStatement(SwitchStatement o) {
		GenerateHDLExprVisitor selector = new GenerateHDLExprVisitor(this, stateTable.get(o.getState()));
		o.getSelector().accept(selector);
		for(Elem e: o.getElements()){
			e.accept(this);
		}
	}

	@Override
	public void visitSwitchCaseElement(Elem o) {
		for(Statement s: o.getStatements()){
			s.accept(this);
		}
	}

	@Override
	public void visitSynchronizedBlock(SynchronizedBlock o) {
		for(Statement s: o.getStatements()){
			s.accept(this);
		}
	}

	@Override
	public void visitTryStatement(TryStatement o) {
		o.getBody().accept(this);
	}

	// TODO, experimental code
	private void newArrayInst(HDLInstance inst, ArrayType type, NewArray init){
		if(init.getDimExpr().get(0) instanceof Literal){
			Literal value = (Literal)(init.getDimExpr().get(0));
			inst.getParameterPair("WORDS").setValue(value.getValueAsStr());
			int dims = Integer.valueOf(value.getValueAsStr());
			int depth = (int)Math.ceil(Math.log(dims) / Math.log(2.0));
			inst.getParameterPair("DEPTH").setValue(String.valueOf(depth));
		}else{
			throw new RuntimeException("not supported to generate array with non-literal dimension");
		}
	}
	
	// TODO, experimental code
	private void newModuleInst(HDLInstance inst, NewClassExpr expr){
		if(expr.getParameters().size() == 0) return;
		NewArray param = (NewArray)(expr.getParameters().get(0));
		ArrayList<Expr> elem = param.getElems();
		for(int i = 0; i < elem.size()/2; i ++){
			String key = ((Literal)elem.get(2*i)).getValueAsStr();
			String value = ((Literal)elem.get(2*i+1)).getValueAsStr();
			if(inst.getParameterPair(key) == null){
				SynthesijerUtils.error(key + " is not defined in " + inst.getSubModule().getName());
				System.exit(0);
			}
			inst.getParameterPair(key).setValue(value);
		}
		for(HDLPort p: inst.getSubModule().getPorts()){
			if(p.isSet(HDLPort.OPTION.EXPORT)){
				HDLSignal s0 = inst.getSignalForPort(p);
				HDLPort p0 = module.newPort(s0.getName(), p.getDir(), p.getType(), EnumSet.of(HDLPort.OPTION.EXPORT, HDLPort.OPTION.NO_SIG));
				inst.rmPortPair(inst.getPortPair(p));
				inst.addPortPair(p0, p);
			}
		}
	}
	
	@Override
	public void visitVariableDecl(VariableDecl o) {
		Variable var = o.getVariable();
		HDLVariable s = variableTable.get(var);
		if(o.hasInitExpr()){
			GenerateHDLExprVisitor v = new GenerateHDLExprVisitor(this, stateTable.get(o.getState()));
			o.getInitExpr().accept(v);
			//System.out.println(o + "<-" + o.getExpr() + "@" + o.getState());
			if(v.getResult() != null && stateTable.get(o.getState()) != null){
				s.setAssign(stateTable.get(o.getState()), v.getResult());
			}
			if(o.getInitExpr().isConstant()){
				s.setResetValue(v.getResult());
			}
			if(o.getType() instanceof ArrayType){
				newArrayInst((HDLInstance)s, (ArrayType)o.getType(), (NewArray)o.getInitExpr());
			}else if(o.getType() instanceof ComponentType){
				newModuleInst((HDLInstance)s, (NewClassExpr)(o.getInitExpr()));
			}
		}
		
		if(o.getScope() instanceof Module && var.getType() instanceof PrimitiveTypeKind){ // added an accessor for the member variable.
			//System.out.print("global:" + o);
			//System.out.println("  " + s.getType());
			HDLPort port = module.newPort("field_" + o.getName() + "_output", DIR.OUT, s.getType());
			port.getSignal().setAssign(null, s);
		}
	}

	@Override
	public void visitWhileStatement(WhileStatement o) {
		o.getBody().accept(this);
	}
	
	private HDLPrimitiveType getHDLType(PrimitiveTypeKind t){
		switch(t){
		case BOOLEAN: return HDLPrimitiveType.genBitType(); 
		case BYTE: return HDLPrimitiveType.genSignedType(8); 
		case CHAR: return HDLPrimitiveType.genVectorType(16);
		case SHORT: return HDLPrimitiveType.genSignedType(16);
		case INT: return HDLPrimitiveType.genSignedType(32);
		case LONG: return HDLPrimitiveType.genSignedType(64);
		case FLOAT: return HDLPrimitiveType.genVectorType(32);
		case DOUBLE: return HDLPrimitiveType.genVectorType(64);
		default: return null; // return HDLPrimitiveType.genUnknowType();
		}
	}
	
	private HDLType getHDLType(Type type){
		if(type instanceof PrimitiveTypeKind){
			return getHDLType((PrimitiveTypeKind)type);
		}else if(type instanceof ArrayType){
			return getHDLType((ArrayType)type);
		}else if(type instanceof ComponentType){
			return getHDLType((ComponentType)type);
		}else if(type instanceof MySelfType){
			return getHDLType((MySelfType)type);
		}else{
			return null;
		}
	}

	private HDLPrimitiveType getHDLType(MySelfType t){
		System.err.println("unsupported type: " + t);
		return null;
	}
	
	private HDLPrimitiveType getHDLType(ComponentType t){
		System.err.println("unsupported type: " + t);
		return null;
	}
	
	private HDLPrimitiveType getHDLType(ArrayType t){
		System.err.println("unsupported type: " + t);
		return null;
	}

	private boolean isThreadStart(Method o){
		return (o.getName().equals("start")) && ((Module)o.getParentScope()).getExtending().equals("Thread");
	}
	private boolean isThreadJoin(Method o){
		return (o.getName().equals("join")) && ((Module)o.getParentScope()).getExtending().equals("Thread");
	}
	private boolean isThreadYield(Method o){
		return (o.getName().equals("yield")) && ((Module)o.getParentScope()).getExtending().equals("Thread");
	}
	
	// TODO experimental
	private void genThreadStart(Method o){
		HDLSequencer seq = module.getSequencer("S_start");
		HDLSignal s = module.getSignal("run_req_local");
		if(module.getSignal("run_req_local") == null){
			SynthesijerUtils.error("run() is not defined in " + ((Module)(o.getParentScope())).getName());
			System.exit(0);
		}
		s.setAssign(seq.getIdleState(), module.getPort("start_req").getSignal());
	}
	
	// TODO experimental
	private void genThreadJoin(Method o){
		module.getPort("join_busy").getSignal().setAssign(null, module.getPort("run_busy").getSignal());
	}
	
	private void genThreadYield(Method o){
		
	}
}
