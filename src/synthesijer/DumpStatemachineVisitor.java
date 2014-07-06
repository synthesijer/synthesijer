package synthesijer;

import java.io.PrintWriter;

import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.SynthesijerAstVisitor;
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
import synthesijer.model.State;
import synthesijer.model.Statemachine;
import synthesijer.model.StatemachineVisitor;
import synthesijer.model.Transition;

public class DumpStatemachineVisitor implements SynthesijerAstVisitor, StatemachineVisitor{
	
	private PrintWriter dest;
	
	public DumpStatemachineVisitor(PrintWriter dest){
		this.dest = dest;
	}

	@Override
	public void visitMethod(Method o) {
		o.getStateMachine().accept(this);
	}

	@Override
	public void visitModule(Module o) {
		dest.printf("digraph " + o.getName() + "{\n");
		for(Method m: o.getMethods()){
			m.accept(this);
		}
		dest.printf("}\n");
	}

	@Override
	public void visitStatemachine(Statemachine o) {
		for(State s: o.getStates()){
			s.accept(this);
		}
	}

	@Override
	public void visitState(State o) {
		dest.printf("%s [label=\"%s\"];\n", o.getId(), o.getId() + "\\n" + o.getDescription());
		for(Transition t: o.getTransitions()){
			if(t.getDestination() != null) dest.printf("%s -> %s;\n", o.getId(), t.getDestination().getId());
		}
	}

	@Override
	public void visitBlockStatement(BlockStatement o) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitIfStatement(IfStatement o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitReturnStatement(ReturnStatement o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitSkipStatement(SkipStatement o) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitVariableDecl(VariableDecl o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitWhileStatement(WhileStatement o) {
		// TODO Auto-generated method stub
		
	}

}
