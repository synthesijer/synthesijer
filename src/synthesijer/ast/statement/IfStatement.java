package synthesijer.ast.statement;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Statement;
import synthesijer.ast.SynthesijerAstVisitor;

public class IfStatement extends Statement{
	
	private Expr condition;
	private BlockStatement thenPart, elsePart;
	
	public IfStatement(Scope scope){
		super(scope);
	}
	
	public void setCondition(Expr expr){
		condition = expr;
	}
	
	public void setThenPart(BlockStatement s){
		thenPart = s;
	}
	
	public void setElsePart(BlockStatement s){
		elsePart = s;
	}

	public Expr getCondition(){
		return condition;
	}
	
	public BlockStatement getThenPart(){
		return thenPart;
	}
	
	public BlockStatement getElsePart(){
		return elsePart;
	}

	public void accept(SynthesijerAstVisitor v){
		v.visitIfStatement(this);
	}


}
