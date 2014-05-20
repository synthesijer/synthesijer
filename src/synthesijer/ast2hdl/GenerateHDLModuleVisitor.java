package synthesijer.ast2hdl;

import java.util.Hashtable;

import synthesijer.ast.Expr;
import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;
import synthesijer.ast.expr.ArrayAccess;
import synthesijer.ast.expr.AssignExpr;
import synthesijer.ast.expr.AssignOp;
import synthesijer.ast.expr.Ident;
import synthesijer.ast.expr.MethodInvocation;
import synthesijer.ast.expr.UnaryExpr;
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
	
	final GenerateHDLModuleVisitor parent;
	final HDLModule module;
	final Hashtable<State, HDLSequencer.SequencerState> stateTable;
	final Hashtable<Method, HDLPort> methodReturnTable;
	
	final Hashtable<String, HDLSignal> variableTable = new Hashtable<String, HDLSignal>();
	
	public GenerateHDLModuleVisitor(HDLModule m){
		this(null, m);
	}

	private GenerateHDLModuleVisitor(GenerateHDLModuleVisitor parent, HDLModule m){
		this.module = m;
		this.parent = parent;
		if(parent == null){
			this.stateTable = new Hashtable<State, HDLSequencer.SequencerState>();
			this.methodReturnTable = new Hashtable<Method, HDLPort>();
		}else{
			this.stateTable = parent.stateTable;
			this.methodReturnTable = parent.methodReturnTable;
		}
	}
	
	private HDLSignal getHDLSignal(Ident id){
		return getHDLSignal(id.getSymbol());
	}
	
	public HDLSignal getHDLSignal(String name){
		HDLSignal sig = variableTable.get(name);
		if(sig != null) return sig;
		if(parent != null) return parent.getHDLSignal(name);
		return null;
	}

	@Override
	public void visitMethod(Method o) {
		for(VariableDecl v: o.getArgs()){
			HDLType t = getHDLType(v.getType());
			if(t != null) module.newPort(v.getName(), HDLPort.DIR.IN, t);
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

	private Hashtable<Method, HDLValue> methodIdTable = new Hashtable<Method, HDLValue>();
	
	@Override
	public void visitModule(Module o) {
		for(VariableDecl v: o.getVariables()){
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
		// new scope
		GenerateHDLModuleVisitor visitor = new GenerateHDLModuleVisitor(this, module);
		for(Statement s: o.getStatements()){
			s.accept(visitor); 
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
	
	public void genExprStatement(AssignExpr expr){
		System.out.println(expr);
	}

	public void genExprStatement(State state, AssignOp expr){
		HDLSignal signal = getHDLSignal((Ident)(expr.getLhs()));
		GenerateHDLExprVisitor v = new GenerateHDLExprVisitor(this);
		expr.accept(v);
		signal.setAssign(stateTable.get(state), v.getResult());
	}
	
	public void genExprStatement(UnaryExpr expr){
		// TODO
	}

	public void genExprStatement(MethodInvocation expr){
		// TODO
	}
	
	public void genExprStatement(ArrayAccess expr){
		// TODO
	}

	@Override
	public void visitExprStatement(ExprStatement o) {
		State s = o.getState();
		Expr expr = o.getExpr();
		if(expr instanceof AssignExpr){
			genExprStatement((AssignExpr)expr);
		}else if(expr instanceof AssignOp){
			genExprStatement(s, (AssignOp)expr);
		}else if(expr instanceof UnaryExpr){
			genExprStatement((UnaryExpr)expr);
		}else if(expr instanceof MethodInvocation){
			genExprStatement((MethodInvocation)expr);
		}else if(expr instanceof ArrayAccess){
			genExprStatement((ArrayAccess)expr);
		}else{
			System.err.printf("unknown to handle: %s(%s)\n" + o, o.getClass());
		}
	}

	@Override
	public void visitForStatement(ForStatement o) {
		// local visitor for "for"
		GenerateHDLModuleVisitor visitor = new GenerateHDLModuleVisitor(this, module);
		for(Statement s: o.getInitializations()){
			s.accept(visitor);
		}
		o.getBody().accept(visitor);
		for(Statement s: o.getUpdates()){
			s.accept(visitor);
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
			GenerateHDLExprVisitor v = new GenerateHDLExprVisitor(this);
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
		GenerateHDLExprVisitor selector = new GenerateHDLExprVisitor(this);
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
		// new scope
		GenerateHDLModuleVisitor visitor = new GenerateHDLModuleVisitor(this, module);
		for(Statement s: o.getStatements()){
			s.accept(visitor);
		}
	}

	@Override
	public void visitTryStatement(TryStatement o) {
		o.getBody().accept(this);
	}

	@Override
	public void visitVariableDecl(VariableDecl o) {
		Variable var = o.getVariable();
		HDLType t = getHDLType(var.getType());
		if(t == null) return;
		HDLSignal s = module.newSignal(var.getUniqueName(), t);
		variableTable.put(var.getName(), s);
		if(o.getInitExpr() != null){
			GenerateHDLExprVisitor v = new GenerateHDLExprVisitor(this);
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
