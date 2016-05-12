package synthesijer.ast.statement;

import java.util.ArrayList;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstTree;
import synthesijer.ast.SynthesijerAstVisitor;

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
		
		public Expr getPattern(){
			return pat;
		}
		
		public void accept(SynthesijerAstVisitor v){
			v.visitSwitchCaseElement(this);
		}
				
	}
	
}
