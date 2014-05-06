package synthesijer.ast.statement;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.ast.Expr;
import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.Variable;
import synthesijer.hdl.HDLModule;
import synthesijer.model.State;
import synthesijer.model.StateMachine;

public class SwitchStatement extends Statement implements Scope{

	private Expr selector;
	private ArrayList<Elem> elements = new ArrayList<Elem>();
	private Scope parent;
	private Hashtable<String, Variable> varTable = new Hashtable<String, Variable>();
	
	public SwitchStatement(Scope scope){
		super(scope);
		this.parent = scope;
	}
	
	public Scope getParentScope(){
		return parent;
	}
	
	public Module getModule(){
		return parent.getModule();
	}
	
	public Method getMethod(){
		return parent.getMethod();
	}

	public void setSelector(Expr expr){
		selector = expr;
	}

	public Elem newElement(Expr pat){
		Elem elem = new Elem(pat);
		elements.add(elem);
		return elem;
	}
	
	public State genStateMachine(StateMachine m, State dest, State terminal, State loopout, State loopCont){
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
	
	public void dumpAsXML(PrintWriter dest){
		dest.printf("<statement type=\"switch\">\n");
		dest.printf("<selector>\n");
		selector.dumpAsXML(dest);
		dest.printf("</selector>\n");
		dest.printf("<elements>\n");
		for(Elem elem: elements){
			elem.dumpAsXML(dest);
		}
		dest.printf("</elements>\n");
		dest.printf("</statement>\n");
	}
	
	public void makeCallGraph(){
		selector.makeCallGraph();
		for(Elem elem: elements){
			elem.makeCallGraph();
		}
	}
	
	public void registrate(Variable v){
		varTable.put(v.getName(), v);
	}
	
	public Variable search(String s){
		Variable v = varTable.get(s);
		if(v != null) return v;
		return parent.search(s);
	}
	
	public class Elem{
		
		private final Expr pat;
		private ArrayList<Statement> statements = new ArrayList<Statement>();
		
		private Elem(Expr pat){
			this.pat = pat;
		}
		
		public void addStatement(Statement s){
			statements.add(s);
		}
		
		public void makeCallGraph(){
			pat.makeCallGraph();
			for(Statement s: statements){
				s.makeCallGraph();
			}
		}
		
		public State genStateMachine(StateMachine m, State dest, State terminal, State loopout, State loopCont){
			State d = dest;
			for(int i = statements.size(); i > 0; i--){
				d = statements.get(i-1).genStateMachine(m, d, terminal, loopout, loopCont);
			}
			return d;
		}
		
		public Expr getPattern(){
			return pat;
		}
		
		public void dumpAsXML(PrintWriter dest){
			dest.printf("<element>\n");
			dest.printf("<pattern>\n");
			pat.dumpAsXML(dest);
			dest.printf("</pattern>\n");
			dest.printf("<statements>\n");
			for(Statement s: statements) s.dumpAsXML(dest);
			dest.printf("</statements>\n");
			dest.printf("</element>\n");	
		}
		
	}

	@Override
	public void generateHDL(HDLModule m) {
		// TODO Auto-generated method stub
		
	}
	
	
}
