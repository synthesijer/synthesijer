package synthesijer.ast.statement;

import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.ast.Expr;
import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Variable;
import synthesijer.model.State;
import synthesijer.model.Statemachine;

public class ForStatement extends Statement implements Scope {

	private Scope parent;
	
	private ArrayList<Statement> initializations = new ArrayList<>();
	private Expr condition;
	private ArrayList<Statement> updates = new ArrayList<>();
	private BlockStatement body;
	
	private Hashtable<String, Variable> varTable = new Hashtable<>();

	public ForStatement(Scope scope) {
		super(scope);
		this.parent = scope;
		addScope(this);
	}
	
	public void replaceInitializations(ArrayList<Statement> newList){
		initializations = newList;
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

	public void addInitialize(Statement s) {
		initializations.add(s);
	}
	
	public ArrayList<Statement> getInitializations(){
		return initializations;
	}

	public void setCondition(Expr expr) {
		condition = expr;
	}
	
	public Expr getCondition(){
		return condition;
	}

	public void addUpdate(Statement s) {
		updates.add(s);
	}

	public ArrayList<Statement> getUpdates(){
		return updates;
	}

	public void setBody(BlockStatement s) {
		this.body = s;
	}
	
	public Statement getBody(){
		return body;
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
	
	public State genStateMachine(Statemachine m, State dest, State terminal, State loopout, State loopCont){
		State d = dest;
		State c = m.newState("for_cond");
		for(int i = updates.size(); i > 0; i--){
			d = updates.get(i-1).genStateMachine(m, c, terminal, dest, c);
		}
		d = body.genStateMachine(m, d, terminal, dest, c);
		c.addTransition(dest, condition, false); // exit from this loop
		c.addTransition(d, condition, true); // repeat this loop
		d = c;
		for(int i = initializations.size(); i > 0; i--){
			d = initializations.get(i-1).genStateMachine(m, d, terminal, dest, c);
		}
		return d;
	}
	
	public void accept(SynthesijerAstVisitor v){
		v.visitForStatement(this);
	}


}
