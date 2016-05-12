package synthesijer.ast.statement;

import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;

public class SkipStatement extends Statement{
	
	public SkipStatement(Scope scope){
		super(scope);
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitSkipStatement(this);
	}
	
}
