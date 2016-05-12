package synthesijer.ast.statement;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import synthesijer.ast.Method;
import synthesijer.ast.Module;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.ast.Variable;

public class BlockStatement extends Statement implements Scope{
	
    private final Scope parent;

    private ArrayList<Statement> statements = new ArrayList<>();
    
    private ArrayList<VariableDecl> variableDecls = new ArrayList<>();
    
    private LinkedHashMap<String, Variable> varTable = new LinkedHashMap<>();
	
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
		//System.out.println(" replace -> #" + statements.size());
	}
	
	public ArrayList<Statement> getStatements(){
		return statements;
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
		v.visitBlockStatement(this);
	}
}
