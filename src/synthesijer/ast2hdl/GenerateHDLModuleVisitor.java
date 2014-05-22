package synthesijer.ast2hdl;

import java.util.Hashtable;

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
import synthesijer.hdl.HDLModule;
import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLPrimitiveType;
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLType;
import synthesijer.hdl.HDLUserDefinedType;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.model.State;

public class GenerateHDLModuleVisitor implements SynthesijerAstVisitor{
	
	final HDLModule module;
	final Hashtable<State, HDLSequencer.SequencerState> stateTable;
	private final Hashtable<Method, HDLPort> methodReturnTable;
	private final Hashtable<Variable, HDLSignal> variableTable = new Hashtable<Variable, HDLSignal>();
	private final Hashtable<Method, HDLValue> methodIdTable = new Hashtable<Method, HDLValue>();
	
	public GenerateHDLModuleVisitor(HDLModule m){
		this.module = m;
		this.stateTable = new Hashtable<State, HDLSequencer.SequencerState>();
		this.methodReturnTable = new Hashtable<Method, HDLPort>();
	}
	
	public HDLSignal getHDLSignal(Variable v){
		return variableTable.get(v);
	}
	
	@Override
	public void visitMethod(Method o) {
		for(VariableDecl v: o.getArgs()){
			HDLType t = getHDLType(v.getType());
			if(t != null){
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
		o.getStateMachine().accept(new Statemachine2HDLSequencerVisitor(this, req, busy));
		o.getBody().accept(this);
	}
	
	private HDLType getHDLType(Type type){
		if(type instanceof PrimitiveTypeKind){
			return ((PrimitiveTypeKind)type).getHDLType();
		}else if(type instanceof ArrayType){
			System.err.println("unsupported type: " + type);
			return null;
		}else if(type instanceof ComponentType){
			System.err.println("unsupported type: " + type);
			return null;
		}else{
			System.err.printf("unkonw type: %s(%s)\n", type, type.getClass());
			return null;
		}
	}
	
	private void genVariableTable(Scope s){
		for(Variable v: s.getVariables()){
			HDLType t = getHDLType(v.getType());
			if(t == null) continue;
			HDLSignal sig = module.newSignal(v.getUniqueName(), t);
			variableTable.put(v, sig);
		}			
	}
	
	@Override
	public void visitModule(Module o) {
		for(Scope s: o.getScope()){
			genVariableTable(s);
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
//		HDLType t = getHDLType(var.getType());
//		if(t == null) return;
//		HDLSignal s = module.newSignal(var.getUniqueName(), t);
//		variableTable.put(var, s);
		HDLSignal s = variableTable.get(var);
		if(o.getInitExpr() != null){
			GenerateHDLExprVisitor v = new GenerateHDLExprVisitor(this, stateTable.get(o.getState()));
			o.getInitExpr().accept(v);
			s.setResetValue(v.getResult());
		}
	}

	@Override
	public void visitWhileStatement(WhileStatement o) {
		o.getBody().accept(this);
	}

	@Override
	public void visitArrayType(ArrayType o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitComponentType(ComponentType o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitMySelfType(MySelfType o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitPrimitiveTypeKind(PrimitiveTypeKind o) {
		// TODO Auto-generated method stub
		
	}

}
