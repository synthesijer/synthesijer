package synthesijer.ast.statement;

import java.util.ArrayList;
import java.util.Hashtable;

import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Variable;
import synthesijer.hdl.HDLModule;
import synthesijer.model.State;
import synthesijer.model.StateMachine;

public class BlockStatement extends Statement implements Scope{
	
	private ArrayList<Statement> statements = new ArrayList<Statement>();
	
	private final Scope parent;
	
	private Hashtable<String, Variable> varTable = new Hashtable<String, Variable>();
	
	public BlockStatement(Scope scope){
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

	public void addStatement(Statement stmt){
		if(stmt != null){
			statements.add(stmt);
		}
	}
	
	public ArrayList<Statement> getStatements(){
		return statements;
	}
	
	public State genStateMachine(StateMachine m, State dest, State terminal, State loopout, State loopCont){
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
	
	public Variable search(String s){
		Variable v = varTable.get(s);
		if(v != null) return v;
		return parent.search(s);
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitBlockStatement(this);
	}
}
