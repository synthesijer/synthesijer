package synthesijer;

import java.util.Hashtable;

import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Type;
import synthesijer.ast.Variable;
import synthesijer.ast.expr.ArrayAccess;
import synthesijer.ast.expr.AssignExpr;
import synthesijer.ast.expr.AssignOp;
import synthesijer.ast.expr.BinaryExpr;
import synthesijer.ast.expr.FieldAccess;
import synthesijer.ast.expr.Ident;
import synthesijer.ast.expr.Literal;
import synthesijer.ast.expr.MethodInvocation;
import synthesijer.ast.expr.NewArray;
import synthesijer.ast.expr.NewClassExpr;
import synthesijer.ast.expr.ParenExpr;
import synthesijer.ast.expr.TypeCast;
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
import synthesijer.hdl.HDLSequencer;
import synthesijer.hdl.HDLSignal;
import synthesijer.hdl.HDLType;
import synthesijer.hdl.HDLUserDefinedType;
import synthesijer.hdl.expr.HDLValue;
import synthesijer.model.State;
import synthesijer.model.Statemachine;
import synthesijer.model.StatemachineVisitor;
import synthesijer.model.Transition;

public class GenerateHDLModuleVisitor implements SynthesijerAstVisitor, StatemachineVisitor{
	
	private final HDLModule module;
	
	public GenerateHDLModuleVisitor(HDLModule m){
		this.module = m;
	}
	
	private Hashtable<Method, HDLPort> methodReturnTable = new Hashtable<Method, HDLPort>();

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
		o.getBody().accept(this);
		o.getStateMachine().accept(this);
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
	public void visitArrayAccess(ArrayAccess o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitAssignExpr(AssignExpr o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitAssignOp(AssignOp o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitBinaryExpr(BinaryExpr o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitFieldAccess(FieldAccess o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitIdent(Ident o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitLitral(Literal o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitMethodInvocation(MethodInvocation o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitNewArray(NewArray o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitNewClassExpr(NewClassExpr o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitParenExpr(ParenExpr o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitTypeCast(TypeCast o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitUnaryExpr(UnaryExpr o) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
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
			p.getSignal().setAssign(null, o.getExpr().getHDLExprResult(module));
		}
	}

	@Override
	public void visitSkipStatement(SkipStatement o) {
		// nothing to generate
	}

	@Override
	public void visitSwitchStatement(SwitchStatement o) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visitSwitchCaseElement(Elem o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitSynchronizedBlock(SynchronizedBlock o) {
		// TODO Auto-generated method stub
		
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
		if(o.getInitExpr() != null){
			s.setResetValue(o.getInitExpr().getHDLExprResult(module));
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
	public void visitPrimitivyTypeKind(PrimitiveTypeKind o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitStatemachine(Statemachine o) {
		HDLSequencer hs = module.newSequencer(o.getKey());
		Hashtable<State, HDLSequencer.SequencerState> map = new Hashtable<State, HDLSequencer.SequencerState>();
		for(State s: o.getStates()){
			map.put(s, hs.addSequencerState(s.getId()));
		}
		for(State s: o.getStates()){
			HDLSequencer.SequencerState ss = map.get(s);
			for(Transition c: s.getTransitions()){
				ss.addStateTransit(map.get(c.getDestination()));
			}
			if(s.isTerminate()){
				ss.addStateTransit(hs.getIdleState());
			}
		}
	}

	@Override
	public void visitState(State o) {
		// TODO Auto-generated method stub
		
	}

}
