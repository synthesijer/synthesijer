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

public class ForStatement extends Statement implements Scope {

	private final Scope parent;

	private ArrayList<Statement> initializations = new ArrayList<>();
	private Expr condition;
	private ArrayList<Statement> updates = new ArrayList<>();
	private BlockStatement body;

	private ArrayList<VariableDecl> variableDecls = new ArrayList<>();
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

	public BlockStatement getBody(){
		return body;
	}

	public void addVariableDecl(VariableDecl v){
		variableDecls.add(v);
		varTable.put(v.getVariable().getName(), v.getVariable());
	}

	public VariableDecl[] getVariableDecls(){
		return variableDecls.toArray(new VariableDecl[]{});
	}

	public Variable search(String s){
		Variable v = varTable.get(s);
		if(v != null) return v;
		return parent.search(s);
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitForStatement(this);
	}


}
