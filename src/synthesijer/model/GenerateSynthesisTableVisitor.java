package synthesijer.model;

import java.io.PrintStream;
import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Op;
import synthesijer.ast.SynthesijerMethodVisitor;
import synthesijer.ast.SynthesijerModuleVisitor;
import synthesijer.ast.Variable;
import synthesijer.ast.statement.ExprContainStatement;
import synthesijer.ast.statement.ExprStatement;
import synthesijer.ast.statement.ReturnStatement;
import synthesijer.ast.statement.VariableDecl;

public class GenerateSynthesisTableVisitor implements SynthesijerModuleVisitor, SynthesijerMethodVisitor, StatemachineVisitor{
	
	private final PrintStream out;
	private final ArrayList<SynthesisTable> tables;
	private SynthesisTable cur;
	
	public GenerateSynthesisTableVisitor(PrintStream out, ArrayList<SynthesisTable> tables){
		this.out = out;
		this.tables = tables;
		this.cur = null;
	}
	
	public void dump(PrintStream out){
		for(SynthesisTable t: tables){
			out.println("------------------- " + t.getName());
			t.dump(out);
			out.println("-------------------");
		}
	}
	
	@Override
	public void visitMethod(Method o) {
		out.println("--- method: " + o.getName());
		for(VariableDecl v: o.getArgs()){
			out.println(" :arg = " +v);
		}
		o.getStateMachine().accept(this);
	}

	@Override
	public void visitModule(Module o) {
		out.println("*** module: " + o.getName());
		for(Method m: o.getMethods()){
			cur = new SynthesisTable(o, m);
			tables.add(cur);
			m.accept(this);
		}
	}

	@Override
	public void visitStatemachine(Statemachine o) {
		out.println("->" + o.getEntryState().getId());
		for(State s: o.getStates()){
			s.accept(this);
		}
	}
	
	private void buildItemEntries(SynthesisTableItem item, ReturnStatement s){
		out.println("return");
		SynthesisTableItem.Entry entry = item.newEntry();
		if(s.getExpr() != null) s.getExpr().accept(new GenerateSynthesisTableItemVisitor(out, entry));
		entry.op = Op.RETURN;
	}

	private void buildItemEntries(SynthesisTableItem item, ExprStatement s){
		out.println("expr");
		SynthesisTableItem.Entry entry = item.newEntry();
		s.getExpr().accept(new GenerateSynthesisTableItemVisitor(out, entry));
	}

	private void buildItemEntries(SynthesisTableItem item, VariableDecl s){
		out.println("var");
		SynthesisTableItem.Entry entry = item.newEntry();
		if(s.getExpr() != null) s.getExpr().accept(new GenerateSynthesisTableItemVisitor(out, entry));
		for(Variable v: s.getDestVariables()){
			entry.dest = v;
			entry.op = Op.ASSIGN;
		}
	}

	private void buildItemEntries(SynthesisTableItem item, State t){
		SynthesisTableItem.Entry entry = item.newEntry();
		entry.op = Op.J;
		entry.next = t;
	}

	private void buildItemEntries(SynthesisTableItem item, Expr expr, State t){
		out.println("expr");
		SynthesisTableItem.Entry entry = item.newEntry();
		expr.accept(new GenerateSynthesisTableItemVisitor(out, entry));
		entry.op = Op.JC;
		entry.next = t;
	}

	private void buildItemEntries(SynthesisTableItem item, Expr expr0, Expr expr1, State t){
		out.println("expr");
		SynthesisTableItem.Entry entry = item.newEntry();
		expr0.accept(new GenerateSynthesisTableItemVisitor(out, entry));
		expr1.accept(new GenerateSynthesisTableItemVisitor(out, entry));
		entry.op = Op.JEQ;
		entry.next = t;
	}

	private void buildItemEntries(SynthesisTableItem item, ExprContainStatement s){
		if(s instanceof ReturnStatement){
			buildItemEntries(item, (ReturnStatement)s);
		}else if(s instanceof ExprStatement){
			buildItemEntries(item, (ExprStatement)s);
		}else{ //if(s instanceof VariableDecl)
			buildItemEntries(item, (VariableDecl)s);
		}
	}
	
	@Override
	public void visitState(State o) {
		//out.printf("%s [label=\"%s\"];\n", o.getId(), o.getId() + "\\n" + o.getDescription());
		out.println("S:" + o.getId() + "{");
		SynthesisTableItem item = new SynthesisTableItem(o);
		cur.add(item);
		for(ExprContainStatement s: o.getBodies()){
			buildItemEntries(item, s);
		}
		for(Transition t: o.getTransitions()){
			if(t.getDestination() != null){
				if(t.getFlag()){
					out.printf(" -> %s (%s,%b)\n", t.getDestination().getId(), t.getCondition(), t.getFlag());
					if(t.getCondition() != null){
						buildItemEntries(item, t.getCondition(), t.getDestination());
					}else{
						buildItemEntries(item, t.getDestination());
					}
				}else{
					out.printf(" -> %s (%s=%s)\n", t.getDestination().getId(), t.getCondition(), t.getPattern());
					if(t.getCondition() != null && t.getPattern() != null){
						buildItemEntries(item, t.getCondition(), t.getPattern(), t.getDestination());
					}else{
						buildItemEntries(item, t.getDestination());
					}
				}
			}
		}
		if(o.isTerminate()){
			out.printf(" -> IDLE\n", o.getId());
		}
		out.println("}");
	}

}
