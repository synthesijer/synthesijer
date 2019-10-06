package synthesijer.ast.statement;

import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;

public class TryStatement extends Statement{

	private Statement body;

	public TryStatement(Scope scope){
		super(scope);
	}

	public void setBody(Statement s){
		this.body = s;
	}

	public Statement getBody(){
		return this.body;
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitTryStatement(this);
	}

}
