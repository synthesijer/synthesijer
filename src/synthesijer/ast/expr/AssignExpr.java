package synthesijer.ast.expr;

import synthesijer.ast.Expr;
import synthesijer.ast.Scope;
import synthesijer.ast.SynthesijerAstVisitor;
import synthesijer.hdl.HDLExpr;

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

	public void makeCallGraph(){
		lhs.makeCallGraph();
		rhs.makeCallGraph();
	}
	
	public HDLExpr getHDLExprResult(){
		return lhs.getHDLExprResult();
	}
	
	public void accept(SynthesijerAstVisitor v){
		v.visitAssignExpr(this);
	}
	
}
