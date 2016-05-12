package synthesijer.ast.statement;

import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLModule;

public class BreakStatement extends Statement{
	
	public BreakStatement(Scope scope){
		super(scope);
	}
		
	public void generateHDL(HDLModule m) {
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitBreakStatement(this);
	}
}
