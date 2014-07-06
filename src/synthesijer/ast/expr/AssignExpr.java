package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.Type;

public class AssignExpr extends Expr{
	
	private Expr lhs, rhs;
	
	public AssignExpr(Scope scope){
		super(scope);
	}
	
	public void setLhs(Expr expr){
		lhs = expr;
	}
	
	public void setRhs(Expr expr){
		rhs = expr;
	}

	public Expr getLhs(){
		return lhs;
	}
	
	public Expr getRhs(){
		return rhs;
	}
		
	public void accept(SynthesijerExprVisitor v){
		v.visitAssignExpr(this);
	}
	
	@Override
	public boolean isConstant() {
		return false;
	}
	
	@Override
	public boolean isVariable() {
		return false;
	}

	@Override
	public Type getType(){
		return lhs.getType();
	}
}
