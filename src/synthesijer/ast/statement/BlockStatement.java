package synthesijer.ast.statement;

import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Variable;
import synthesijer.model.State;
import synthesijer.model.Statemachine;

public class BlockStatement extends Statement implements Scope{
	
	private ArrayList<Statement> statements = new ArrayList<>();
	
	private final Scope parent;
	
	private Hashtable<String, Variable> varTable = new Hashtable<>();
	
	public BlockStatement(Scope scope){
		super(scope);
		this.parent = scope;
		parent.addScope(this);
	}
	
	public void addScope(Scope s){
		parent.addScope(s);
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

	public void addStatement(Statement stmt){
		if(stmt != null){
			statements.add(stmt);
		}
	}
	
	public void replaceStatements(ArrayList<Statement> newList){
		statements = newList;
	}
	
	public ArrayList<Statement> getStatements(){
		return statements;
	}
	
	public State genStateMachine(Statemachine m, State dest, State terminal, State loopout, State loopCont){
		State d = dest;
		for(int i = statements.size(); i > 0; i--){
			Statement stmt = statements.get(i-1);
			d = stmt.genStateMachine(m, d, terminal, loopout, loopCont);
		}
		return d;
	}
	
	public void addVariableDecl(VariableDecl v){
		varTable.put(v.getVariable().getName(), v.getVariable());
	}
	
	public Variable[] getVariables(){
		return varTable.values().toArray(new Variable[]{});
	}

	public Variable search(String s){
		Variable v = varTable.get(s);
		if(v != null) return v;
		return parent.search(s);
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitBlockStatement(this);
	}
}
