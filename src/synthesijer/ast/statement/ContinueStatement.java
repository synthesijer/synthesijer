package synthesijer.ast.statement;

import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLModule;

public class ContinueStatement extends Statement{

	public ContinueStatement(Scope parent){
		super(parent);
	}

	public void generateHDL(HDLModule m) {
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitContinueStatement(this);
	}
}
