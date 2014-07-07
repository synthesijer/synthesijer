package synthesijer.ast2hdl;

import java.util.Hashtable;

import synthesijer.CompileState;
import synthesijer.Manager;
import synthesijer.ast.Expr;
import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;
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
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLType;
import synthesijer.hdl.HDLUserDefinedType;
import synthesijer.hdl.HDLVariable;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.model.State;

public class GenerateHDLModuleVisitor implements SynthesijerAstVisitor{
	
	final HDLModule module;
	final Hashtable<State, HDLSequencer.SequencerState> stateTable;
	private final Hashtable<Method, HDLPort> methodReturnTable;
	private final Hashtable<Variable, HDLVariable> variableTable = new Hashtable<Variable, HDLVariable>();
	private final Hashtable<Method, HDLValue> methodIdTable = new Hashtable<Method, HDLValue>();
	
	public GenerateHDLModuleVisitor(HDLModule m){
		this.module = m;
		this.stateTable = new Hashtable<State, HDLSequencer.SequencerState>();
		this.methodReturnTable = new Hashtable<Method, HDLPort>();
	}
	
	public HDLVariable getHDLVariable(Variable v){
		return variableTable.get(v);
	}
	
	@Override
	public void visitMethod(Method o) {
		if(o.isConstructor()) return; // skip 
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
		genVariableTables(o);
		o.getStateMachine().accept(new Statemachine2HDLSequencerVisitor(this, req, busy));
		o.getBody().accept(this);
	}
		
	private HDLVariable genHDLVariable(Variable v){
		Type t = v.getType();
		if(t instanceof PrimitiveTypeKind){
			HDLType t0 = getHDLType(v.getType());
			return module.newSignal(v.getUniqueName(), t0);
		}else if(t instanceof ArrayType){
			Manager.HDLModuleInfo info = Manager.INSTANCE.searchHDLModuleInfo("BlockRAM");
			HDLInstance inst = module.newModuleInstance(info.hm, v.getName());
			inst.getSignalForPort("clk").setAssign(null, module.getSysClk().getSignal());
			inst.getSignalForPort("reset").setAssign(null, module.getSysReset().getSignal());
			return inst;
		}else if(t instanceof ComponentType){
			ComponentType c = (ComponentType)t;
			Manager.HDLModuleInfo info = Manager.INSTANCE.searchHDLModuleInfo(c.getName());
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

	@Override
	public void visitVariableDecl(VariableDecl o) {
		Variable var = o.getVariable();
		HDLVariable s = variableTable.get(var);
		if(o.hasInitExpr()){
			GenerateHDLExprVisitor v = new GenerateHDLExprVisitor(this, stateTable.get(o.getState()));
			o.getInitExpr().accept(v);
			if(o.getInitExpr().isConstant()){
				s.setResetValue(v.getResult());
			}
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

	
}
