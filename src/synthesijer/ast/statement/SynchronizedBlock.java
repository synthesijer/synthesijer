package synthesijer.ast.statement;

import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;

public class SynchronizedBlock extends BlockStatement{

	public SynchronizedBlock(Scope scope){
		super(scope);
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitSynchronizedBlock(this);
	}

}
