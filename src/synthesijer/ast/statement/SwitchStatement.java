package synthesijer.ast.statement;

import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstTree;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLModule;
import synthesijer.model.State;
import synthesijer.model.Statemachine;

public class SwitchStatement extends Statement{

	private Expr selector;
	private ArrayList<Elem> elements = new ArrayList<Elem>();
	
	public SwitchStatement(Scope scope){
		super(scope);
	}

	public void setSelector(Expr expr){
		selector = expr;
	}

	public Expr getSelector(){
		return selector;
	}

	public Elem newElement(Expr pat){
		Elem elem = new Elem(pat);
		elements.add(elem);
		return elem;
	}
	
	public ArrayList<Elem> getElements(){
		return elements;
	}
	
	public State genStateMachine(Statemachine m, State dest, State terminal, State loopout, State loopCont){
		State d = dest;
		State[] stats = new State[elements.size()];
		for(int i = elements.size(); i > 0; i--){
			d = elements.get(i-1).genStateMachine(m, d, terminal, dest, loopCont);
			stats[i-1] = d;
		}
		State s = m.newState("swithc_selector");
		for(int i = 0; i < elements.size(); i++){
			s.addTransition(stats[i], selector, elements.get(i).getPattern());	
		}
		return s;
	}
	
	public class Elem implements SynthesijerAstTree{
		
		private final Expr pat;
		private ArrayList<Statement> statements = new ArrayList<Statement>();
		
		private Elem(Expr pat){
			this.pat = pat;
		}
		
		public void addStatement(Statement s){
			statements.add(s);
		}
		
		public ArrayList<Statement> getStatements(){
			return statements;
		}
		
		public State genStateMachine(Statemachine m, State dest, State terminal, State loopout, State loopCont){
			State d = dest;
			for(int i = statements.size(); i > 0; i--){
				d = statements.get(i-1).genStateMachine(m, d, terminal, loopout, loopCont);
			}
			return d;
		}
		
		public Expr getPattern(){
			return pat;
		}
		
		public void accept(SynthesijerAstVisitor v){
			v.visitSwitchCaseElement(this);
		}
				
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitSwitchStatement(this);
	}
	
}
