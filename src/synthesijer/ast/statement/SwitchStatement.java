package synthesijer.ast.statement;

import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstTree;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.model.State;
import synthesijer.model.Statemachine;

public class SwitchStatement extends Statement{

	private Expr selector;
	private ArrayList<Elem> elements = new ArrayList<>();
	private final Elem defaultElem = new Elem(null);
	
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

	public Elem getDefaultElement(){
		return defaultElem;
	}

	public ArrayList<Elem> getElements(){
		return elements;
	}
	
	private State state;
	
	public State genStateMachine(Statemachine m, State dest, State terminal, State loopout, State loopCont){
		State d = dest;
		State[] stats = new State[elements.size()];
		//System.out.println(" *** begin of switch::genStateMachine");
		for(int i = elements.size(); i > 0; i--){
			d = elements.get(i-1).genStateMachine(m, d, terminal, dest, loopCont);
			stats[i-1] = d;
		}
		State s = m.newState("switch_selector");
		for(int i = 0; i < elements.size(); i++){
			s.addTransition(stats[i], selector, elements.get(i).getPattern());
		}
		//System.out.println("Switch::genStateMachine:" + defaultElem.genStateMachine(m, dest, terminal, dest, loopCont));
		s.addTransition(defaultElem.genStateMachine(m, dest, terminal, dest, loopCont));
		state = s;
		//System.out.println(" *** end of switch::genStateMachine");
		return s;
	}
	
	public State getState(){
		return state;
	}
	
	public void accept(SynthesijerAstVisitor v){
		v.visitSwitchStatement(this);
	}

	public class Elem implements SynthesijerAstTree{
		
		private final Expr pat;
		private ArrayList<Statement> statements = new ArrayList<>();
		
		private Elem(Expr pat){
			this.pat = pat;
		}
		
		public void addStatement(Statement s){
			statements.add(s);
		}
		
		public ArrayList<Statement> getStatements(){
			return statements;
		}
		
		public void replaceStatements(ArrayList<Statement> newList){
			statements = newList;
			//System.out.println("--- replace ---");
			//for(Statement s: statements){	System.out.println(s);}
			//System.out.println("---------------");
		}

		public State genStateMachine(Statemachine m, State dest, State terminal, State loopout, State loopCont){
			//System.out.println(" *** begin of SwitchElem::genStateMachine");
			State d = dest;
			for(int i = statements.size(); i > 0; i--){
				d = statements.get(i-1).genStateMachine(m, d, terminal, loopout, loopCont);
			}
			//System.out.println(" *** end of SwitchElem::genStateMachine");
			return d;
		}
		
		public Expr getPattern(){
			return pat;
		}
		
		public void accept(SynthesijerAstVisitor v){
			v.visitSwitchCaseElement(this);
		}
				
	}
	
}
